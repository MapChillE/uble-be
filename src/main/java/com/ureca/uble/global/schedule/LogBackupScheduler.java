package com.ureca.uble.global.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogBackupScheduler {

    private final WebClient elasticWebClient;

    @Value("${spring.elasticsearch.username}")
    private String elasticUser;

    @Value("${spring.elasticsearch.password}")
    private String elasticPassword;

    @Value("${elasticsearch.backup.bucket-name}")
    private String bucketName;

    @Scheduled(cron = "0 0 3 * * *")
    public void backupSearchLog() {
        log.info("[Elasticsearch] 로그 백업 시작: {}", LocalDateTime.now());

        String[] indexList = {
            "search-log",
            "brand-click-log",
            "store-click-log",
            "usage-history-log",
            "business-logs-*",
            "slow-logs-*",
            "error-logs-*"
        };

        for (String index : indexList) {
            try {
                backUpIndex(index);
            } catch (Exception e) {
                log.error("[Elasticsearch] 인덱스 {} 백업 실패: {}", index, e.getMessage(), e);
            }
        }
    }

    private void backUpIndex(String originalIndex) {
        String index = originalIndex.replace("-*", "");
        String repoName = "s3_repo_" + index;
        String basePath = "elasticsearch-snapshots/" + index;

        // 리포지토리 등록 (실패 시 다음 index 진행)
        if(!registerRepository(repoName, basePath)) return;

        // 스냅샷 생성 및 저장
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String snapshotName = index + "-snapshot-" + timestamp;
        String snapshotUrl = "/_snapshot/" + repoName + "/" + snapshotName + "?wait_for_completion=true";

        String snapshotBody = """
            {
              "indices": "%s",
              "include_global_state": false
            }
            """.formatted(index);

        try {
            elasticWebClient.put()
                .uri(snapshotUrl)
                .headers(headers -> headers.setBasicAuth(elasticUser, elasticPassword))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(snapshotBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.info("[Elasticsearch] 인덱스 {} 스냅샷 생성 성공", index);
        } catch (Exception e) {
            log.error("[Elasticsearch] 인덱스 {} 스냅샷 생성 실패: {}", index, e.getMessage());
        }
    }

    private boolean registerRepository(String repoName, String basePath) {
        log.info("[Elasticsearch] 레포지토리 등록 시작: {}", repoName);

        String repoUrl = "/_snapshot/" + repoName;
        String repoBody = """
            {
              "type": "s3",
              "settings": {
                "bucket": "%s",
                "region": "ap-northeast-2",
                "base_path": "%s",
                "compress": true
              }
            }
            """.formatted(bucketName, basePath);

        try {
            elasticWebClient.put()
                .uri(repoUrl)
                .headers(headers -> headers.setBasicAuth(elasticUser, elasticPassword))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(repoBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.info("[Elasticsearch] 레포지토리 {} 등록 성공", repoName);
        } catch (Exception e) {
            log.error("[Elasticsearch] 레포지토리 {} 등록 실패: {}", repoName, e.getMessage());
            return false;
        }
        return true;
    }
}
