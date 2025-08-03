package com.ureca.uble.domain.search.service;

import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.uble.domain.search.dto.request.CreateSearchLogReq;
import com.ureca.uble.domain.search.dto.response.CreateSearchLogRes;
import com.ureca.uble.domain.search.dto.response.KeywordRankRes;
import com.ureca.uble.domain.search.dto.response.TopKeywordListRes;
import com.ureca.uble.domain.store.repository.SearchLogDocumentRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.User;
import com.ureca.uble.entity.document.SearchLogDocument;
import com.ureca.uble.entity.enums.RankChangeType;
import com.ureca.uble.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ureca.uble.domain.users.exception.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchLogDocumentRepository searchLogDocumentRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String REDIS_KEY_PREFIX = "search:top:";
    private static final Duration KEYWORD_TTL = Duration.ofMinutes(90);

    /**
     * 검색 로그 생성
     */
    public CreateSearchLogRes createSearchLog(Long userId, CreateSearchLogReq req) {
        User user = findUser(userId);
        SearchLogDocument savedDocument = searchLogDocumentRepository.save(SearchLogDocument.of(user, req.getSearchType(), req.getKeyword(), req.getIsResultExists()));
        return new CreateSearchLogRes(savedDocument.getId());
    }

    /**
     * top 10 실시간 인기 검색어 조회
     */
    public TopKeywordListRes getPopularKeywordList() {
        ZonedDateTime now = ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS);
        String key = getRedisKey(now);

        // 캐시 조회
        List<KeywordRankRes> cached = loadKeywordListFromRedis(key);
        if (!cached.isEmpty()) {
            return new TopKeywordListRes(cached);
        }
        return cachePopularKeywordList();
    }

    public TopKeywordListRes cachePopularKeywordList() {
        ZonedDateTime now = ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS);
        ZonedDateTime past = now.minusHours(1);

        String key = getRedisKey(now);
        String pastKey = getRedisKey(past);

        // 과거 정보 조회
        Map<String, Integer> pastRankMap = loadKeywordListFromRedis(pastKey).stream()
            .collect(Collectors.toMap(KeywordRankRes::getKeyword, KeywordRankRes::getRank));

        // 새로운 정보 조회
        List<StringTermsBucket> buckets = searchLogDocumentRepository.getPopularKeywordList().aggregationsAsMap()
            .get("top_keywords").aggregation().getAggregate().sterms().buckets().array();

        List<KeywordRankRes> rankList = IntStream.range(0, buckets.size())
            .mapToObj(i -> {
                StringTermsBucket b = buckets.get(i);
                String keyword = b.key().stringValue();
                int rank = i + 1;
                long count = b.docCount();

                RankChangeType change = RankChangeType.NEW;
                int diff = 0;

                if (pastRankMap.containsKey(keyword)) {
                    int pastRank = pastRankMap.get(keyword);
                    diff = Math.abs(pastRank - rank);

                    change = pastRank > rank ? RankChangeType.UP :
                        pastRank < rank ? RankChangeType.DOWN :
                        RankChangeType.SAME;
                }
                return KeywordRankRes.of(keyword, rank, count, change, diff);
            }).toList();

        saveKeywordListToRedis(key, rankList);
        return new TopKeywordListRes(rankList);
    }

    private void saveKeywordListToRedis(String key, List<KeywordRankRes> list) {
        try {
            String json = objectMapper.writeValueAsString(list);
            redisTemplate.opsForValue().set(key, json, KEYWORD_TTL);
        } catch (Exception e) {
            log.error("Redis 저장 실패 - key: {}, error: {}", key, e.getMessage(), e);
        }
    }

    private List<KeywordRankRes> loadKeywordListFromRedis(String key) {
        try {
            String json = (String) redisTemplate.opsForValue().get(key);
            if (json == null) return List.of();
            return Arrays.asList(objectMapper.readValue(json, KeywordRankRes[].class));
        } catch (Exception e) {
            return List.of();
        }
    }

    private String getRedisKey(ZonedDateTime time) {
        return REDIS_KEY_PREFIX + time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
    }
}
