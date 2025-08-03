package com.ureca.uble.global.schedule;

import com.ureca.uble.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PopularKeywordScheduler {

    private final SearchService searchService;

    /**
     * 인기 검색어 캐싱 갱신
     */
    @Scheduled(cron = "0 0 * * * *")
    public void refreshKeywordRank() {
        searchService.cachePopularKeywordList();
        log.info("인기 검색어 Caching 완료");
    }
}
