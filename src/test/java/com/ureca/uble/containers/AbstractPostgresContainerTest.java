package com.ureca.uble.containers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class AbstractPostgresContainerTest {

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
        new PostgreSQLContainer<>(DockerImageName.parse("postgis/postgis:15-3.4-alpine")
            .asCompatibleSubstituteFor("postgres"))
            .withDatabaseName("test-db")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("sql/init_extensions.sql");

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }
}
