package com.ureca.uble.domain.users.service;

import com.ureca.uble.domain.brand.repository.BrandClickLogDocumentRepository;
import com.ureca.uble.domain.category.repository.CategoryRepository;
import com.ureca.uble.domain.users.dto.request.UpdateUserInfoReq;
import com.ureca.uble.domain.users.dto.response.*;
import com.ureca.uble.domain.users.exception.UserErrorCode;
import com.ureca.uble.domain.users.repository.UsageHistoryDocumentRepository;
import com.ureca.uble.domain.users.repository.UserCategoryRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Category;
import com.ureca.uble.entity.User;
import com.ureca.uble.entity.UserCategory;
import com.ureca.uble.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserCategoryRepository userCategoryRepository;
	private final CategoryRepository categoryRepository;
	private final WebClient fastapiWebClient;
	private final BrandClickLogDocumentRepository brandClickLogDocumentRepository;
	private final UsageHistoryDocumentRepository usageHistoryDocumentRepository;

	/**
	 * 사용자 정보 조회
	 */
	@Transactional(readOnly = true)
	public GetUserInfoRes getUserInfo(Long userId) {
		User user = findUser(userId);

		List<Long> categoryIds = userCategoryRepository.findByUser(user).stream()
			.map(uc -> uc.getCategory().getId())
			.toList();

		return GetUserInfoRes.of(user, categoryIds);
	}

	/**
	 * 사용자 정보 갱신
	 */
	@Transactional
	public UpdateUserInfoRes updateUserInfo(Long userId, UpdateUserInfoReq request) {
		User user = findUser(userId);

		user.updateUserInfo(
			request.getRank(),
			request.getGender(),
			request.getBirthDate(),
			request.getBarcode()
		);

		userCategoryRepository.deleteByUser(user);

		List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
		categories.forEach(category -> {
			UserCategory userCategory = UserCategory.of(user, category);
			userCategoryRepository.save(userCategory);
		});

		return UpdateUserInfoRes.of(user, request.getCategoryIds());
	}

	/**
	 * 사용자 맞춤 추천
	 */
	public GetRecommendationListRes getRecommendations(Long userId, Double latitude, Double longitude) {
		if (userId == null || latitude == null || longitude == null){
			throw new GlobalException(UserErrorCode.INVALID_PARAMETER);
		}
		try {
			return fastapiWebClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("api/recommend/hybrid")
					.queryParam("user_id", userId)
					.queryParam("lat", latitude)
					.queryParam("lng", longitude)
					.build())
				.retrieve()
				.onStatus(status -> status.isError(), response ->
					Mono.error(new GlobalException(UserErrorCode.EXTERNAL_API_ERROR)))
				.bodyToMono(GetRecommendationListRes.class)
				.blockOptional()
				.orElseThrow(() -> new GlobalException(UserErrorCode.RECOMMENDATION_NOT_FOUND));
		} catch (Exception e) {
			throw new GlobalException(UserErrorCode.EXTERNAL_API_ERROR);
		}
	}

	/**
	 * 개인 통계 전체 조회
	 */
	@Transactional(readOnly = true)
	public GetUserStatisticsRes getUserStatistics(Long userId) {
		User user = findUser(userId);

		ElasticsearchAggregations rankResult = brandClickLogDocumentRepository.getCategoryAndBrandRankByUserId(userId);
		ElasticsearchAggregations usageResult = usageHistoryDocumentRepository.getUsageDateAndDiffAndCount(user);

		// 카테고리 순위
		List<CategoryRankRes> categoryRankList = rankResult.aggregationsAsMap().get("category_rank").aggregation()
			.getAggregate().sterms().buckets().array().stream()
			.map(b -> CategoryRankRes.of(b.key().stringValue(), b.docCount()))
			.toList();

		// 제휴처 순위
		List<BrandRankRes> brandRankList = rankResult.aggregationsAsMap().get("brand_rank").aggregation()
			.getAggregate().sterms().buckets().array().stream()
			.map(b -> BrandRankRes.of(b.key().stringValue(), b.docCount()))
			.toList();

		// 가장 많이 사용한 날(일)
		String mostUsedDay = usageResult.aggregationsAsMap().get("most_used_day_of_month").aggregation().getAggregate().filter().aggregations()
			.get("by_day").sterms().buckets().array().stream().findFirst().map(b -> b.key().stringValue()).orElse(null);

		String mostUsedWeekDay = usageResult.aggregationsAsMap().get("most_used_weekday").aggregation().getAggregate().filter().aggregations()
			.get("weekday").sterms().buckets().array().stream().findFirst().map(b -> b.key().stringValue()).orElse(null);

		String mostUsedTime = usageResult.aggregationsAsMap().get("most_used_hour").aggregation().getAggregate().filter().aggregations()
			.get("hour").sterms().buckets().array().stream().findFirst().map(b -> b.key().stringValue()).orElse(null);

		BenefitUsagePatternRes benefitUsagePatternRes = BenefitUsagePatternRes.of(mostUsedDay, mostUsedWeekDay, mostUsedTime);

		// 성별나이 평균 대비 사용 횟수
		double totalCount = usageResult.aggregationsAsMap().get("target_group").aggregation()
			.getAggregate().filter().aggregations().get("total_count").valueCount().value();
		double totalUserCount = usageResult.aggregationsAsMap().get("target_group").aggregation()
			.getAggregate().filter().aggregations().get("user_count").cardinality().value();
		int userHistoryCount = (int) usageResult.aggregationsAsMap().get("my_usage_count").aggregation()
			.getAggregate().filter().aggregations().get("total_history_count").valueCount().value();

		BenefitUsageComparisonRes benefitUsageComparisonRes = BenefitUsageComparisonRes.of(totalCount / totalUserCount, userHistoryCount);

		// 월별 사용 횟수 (6개월)
		List<MonthlyBenefitUsageRes> monthlyBenefitUsageList = usageResult.aggregationsAsMap().get("monthly_usage").aggregation().getAggregate().filter().aggregations()
			.get("monthly").dateHistogram().buckets().array().stream()
			.map(b -> {
				YearMonth ym = YearMonth.parse(b.keyAsString(), DateTimeFormatter.ofPattern("yyyy-MM"));
				return MonthlyBenefitUsageRes.of(ym.getYear(), ym.getMonthValue(), b.docCount());
			}).toList();

		return GetUserStatisticsRes.of(categoryRankList, brandRankList, benefitUsagePatternRes, benefitUsageComparisonRes, monthlyBenefitUsageList);
	}

	private User findUser(Long userId){
		return userRepository.findById(userId)
			.orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND));
	}
}
