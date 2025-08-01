package com.ureca.uble.containers;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.time.Duration;

@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
public class ElasticsearchTestContainer {

    private static final String ELASTICSEARCH_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:8.17.4";
    private static final String SYNONYM_PATH = new File("src/test/resources/synonyms/synonyms.txt").getAbsolutePath();

    @Container
    public static final ElasticsearchContainer container =
        new ElasticsearchContainer(DockerImageName.parse(ELASTICSEARCH_IMAGE))
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("xpack.security.http.ssl.enabled", "false")
            .withCommand("sh", "-c", "elasticsearch-plugin install analysis-nori && elasticsearch")
            .withFileSystemBind(SYNONYM_PATH, "/usr/share/elasticsearch/config/analysis/brand-synonyms.txt", BindMode.READ_ONLY)
            .withFileSystemBind(SYNONYM_PATH, "/usr/share/elasticsearch/config/analysis/category-synonyms.txt", BindMode.READ_ONLY)
            .withFileSystemBind(SYNONYM_PATH, "/usr/share/elasticsearch/config/analysis/season-synonyms.txt", BindMode.READ_ONLY)
            .withFileSystemBind(SYNONYM_PATH, "/usr/share/elasticsearch/config/analysis/address-synonyms.txt", BindMode.READ_ONLY)
            .waitingFor(Wait.forLogMessage(".*started.*\\n", 1)
                .withStartupTimeout(Duration.ofMinutes(5)));

    static {
        container.start();
    }

    @DynamicPropertySource
    static void setElasticsearchProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", container::getHttpHostAddress);
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder(HttpHost.create(container.getHttpHostAddress())).build();
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        return new ElasticsearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
    }
}
