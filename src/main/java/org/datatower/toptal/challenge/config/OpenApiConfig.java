package org.datatower.toptal.challenge.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi portalApi() {
        return GroupedOpenApi.builder()
                .packagesToScan("org.datatower.toptal.challenge.controller")
                .group("Patient Services")
                .build();
    }
}
