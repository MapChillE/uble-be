package com.ureca.uble.domain.users.repository;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch._types.query_dsl.DateRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.util.NamedValue;
import com.ureca.uble.domain.common.util.SearchFilterUtils;
import com.ureca.uble.entity.User;
import com.ureca.uble.entity.document.UsageHistoryDocument;
import com.ureca.uble.entity.enums.BenefitType;
import com.ureca.uble.entity.enums.Gender;
import com.ureca.uble.entity.enums.Rank;
import com.ureca.uble.entity.enums.RankTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CustomUsageHistoryDocumentRepositoryImpl implements CustomUsageHistoryDocumentRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public ElasticsearchAggregations getUsageDateAndDiffAndCount(User user) {
        long userId = user.getId();
        int currentYear = LocalDate.now().getYear();


        // userId 필터
        Query userFilter = Query.of(u -> u
            .term(t -> t.field("userId").value(userId))
        );

        // 사용자 성별, 연령대의 평균 사용량 정보
        int userAgeRange = ((currentYear - user.getBirthDate().getYear() + 1) / 10) * 10;
        int minBirthYear = currentYear - (userAgeRange + 9) + 1;
        int maxBirthYear = currentYear - userAgeRange + 1;

        String fromBirthDate = LocalDate.of(minBirthYear, 1, 1).format(DateTimeFormatter.ISO_DATE);
        String toBirthDate = LocalDate.of(maxBirthYear, 12, 31).format(DateTimeFormatter.ISO_DATE);

        Query ageRangeFilter = DateRangeQuery.of(r -> r
            .field("userBirthDate")
            .gte(fromBirthDate)
            .lte(toBirthDate)
        )._toRangeQuery()._toQuery();

        Query genderFilter = Query.of(q -> q
            .term(t -> t.field("userGender").value(user.getGender().toString()))
        );

        Aggregation targetGroupAgg = Aggregation.of(a -> a
            .filter(f -> f.bool(b -> b
                .filter(List.of(ageRangeFilter, genderFilter))
            ))
            .aggregations(Map.of(
                "user_count", Aggregation.of(ag -> ag.cardinality(c -> c.field("userId"))),
                "total_count", Aggregation.of(ag -> ag.valueCount(vc -> vc.field("userId")))
            ))
        );

        // 사용자 전체 사용량
        Aggregation myUsageCountAgg = Aggregation.of(a -> a
            .filter(userFilter)
            .aggregations(Map.of(
                "total_history_count", Aggregation.of(ag -> ag.valueCount(vc -> vc.field("userId")))
            ))
        );

        // 월별 사용량 (6개월)
        LocalDate nowDate = LocalDate.now();
        String fromDate = nowDate.minusMonths(5).withDayOfMonth(1).format(DateTimeFormatter.ISO_DATE);

        Query dateRangeFilter = DateRangeQuery.of(r -> r
            .field("createdAt")
            .gte(fromDate)
            .lte("now")
        )._toRangeQuery()._toQuery();

        Aggregation monthlyUsageAgg = Aggregation.of(a -> a
            .filter(f -> f
                .bool(b -> b
                    .filter(List.of(userFilter, dateRangeFilter))
                )
            )
            .aggregations("monthly", Aggregation.of(aa -> aa
                .dateHistogram(dh -> dh
                    .field("createdAt")
                    .calendarInterval(CalendarInterval.Month)
                    .format("yyyy-MM")
                    .timeZone("Asia/Seoul")
                    .minDocCount(0)
                )
            ))
        );

        // 가장 많이 사용한 날
        Aggregation mostUsedDayOfMonthAgg = Aggregation.of(a -> a
            .filter(userFilter)
            .aggregations("by_day", Aggregation.of(aa -> aa
                .terms(t -> t
                    .script(s -> s
                        .source("doc['createdAt'].value.getDayOfMonth()")
                        .lang("painless")
                    )
                    .size(1)
                    .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
                )
            ))
        );

        // 가장 많이 사용한 요일
        Aggregation mostUsedWeekdayAgg = Aggregation.of(a -> a
            .filter(userFilter)
            .aggregations("weekday", Aggregation.of(aa -> aa
                .terms(t -> t
                    .script(s -> s
                        .source("""
                            int d = doc['createdAt'].value.getDayOfWeek().getValue();
                            if (d == 1) return '월';
                            if (d == 2) return '화';
                            if (d == 3) return '수';
                            if (d == 4) return '목';
                            if (d == 5) return '금';
                            if (d == 6) return '토';
                            if (d == 7) return '일';
                            return '';
                        """)
                        .lang("painless")
                    )
                    .size(1)
                    .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
                )
            ))
        );

        // 가장 많이 사용한 시간
        Aggregation mostUsedHourAgg = Aggregation.of(a -> a
            .filter(userFilter)
            .aggregations("hour", Aggregation.of(aa -> aa
                .terms(t -> t
                    .script(s -> s
                        .source("doc['createdAt'].value.getHour()")
                        .lang("painless")
                    )
                    .size(1)
                    .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
                )
            ))
        );

        // 최종 쿼리 생성
        NativeQuery query = NativeQuery.builder()
            .withAggregation("target_group", targetGroupAgg)
            .withAggregation("my_usage_count", myUsageCountAgg)
            .withAggregation("monthly_usage", monthlyUsageAgg)
            .withAggregation("most_used_day_of_month", mostUsedDayOfMonthAgg)
            .withAggregation("most_used_weekday", mostUsedWeekdayAgg)
            .withAggregation("most_used_hour", mostUsedHourAgg)
            .withMaxResults(0)
            .build();

        // 검색 및 반환
        return (ElasticsearchAggregations) elasticsearchOperations.search(query, UsageHistoryDocument.class).getAggregations();
    }

    /**
     * (Admin) 제휴처/카테고리 이용 순위
     */
    @Override
    public ElasticsearchAggregations getUsageRankByFiltering(RankTarget rankTarget, Gender gender, Integer ageRange, Rank rank, BenefitType benefitType) {
        // filter 설정
        List<Query> filters = SearchFilterUtils.getAdminStatisticFilters(gender, ageRange, rank, benefitType);

        // 통계 쿼리
        String fieldName = switch (rankTarget) {
            case BRAND -> "brandName";
            case CATEGORY -> "category";
        };

        Aggregation aggregation = Aggregation.of(a -> a
            .filter(f -> f
                .bool(b -> b.filter(filters))
            )
            .aggregations("rank", Aggregation.of(sub -> sub
                .terms(t -> t
                    .field(fieldName)
                    .size(10)
                    .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
                )
            ))
        );

        // 최종 쿼리 생성
        NativeQuery query = NativeQuery.builder()
            .withAggregation("usage_rank", aggregation)
            .withMaxResults(0)
            .build();

        return (ElasticsearchAggregations) elasticsearchOperations.search(query, UsageHistoryDocument.class).getAggregations();
    }

    @Override
    public ElasticsearchAggregations getLocalRankByFiltering(Gender gender, Integer ageRange, Rank rank, BenefitType benefitType) {
        // filter 설정
        List<Query> filters = SearchFilterUtils.getAdminStatisticFilters(gender, ageRange, rank, benefitType);

        // 통계 쿼리
        Aggregation aggregation = Aggregation.of(a -> a
            .filter(f -> f
                .bool(b -> b.filter(filters))
            )
            .aggregations("rank", Aggregation.of(sub -> sub
                .terms(t -> t
                    .field("storeLocal")
                    .size(25)
                    .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
                )
            ))
        );

        // 최종 쿼리 생성
        NativeQuery query = NativeQuery.builder()
            .withAggregation("local_rank", aggregation)
            .withMaxResults(0)
            .build();

        return (ElasticsearchAggregations) elasticsearchOperations.search(query, UsageHistoryDocument.class).getAggregations();
    }
}
