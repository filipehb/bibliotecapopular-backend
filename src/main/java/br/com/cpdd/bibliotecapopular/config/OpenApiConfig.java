package br.com.cpdd.bibliotecapopular.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bibliotecaPopularOpenAPI(
            @Value("${bibliotecapopular.openapi.server-url:}") String serverUrl) {
        final String bearerAuth = "bearerAuth";

        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Biblioteca Popular API")
                        .description("API da Biblioteca Popular — catálogo, exemplares e empréstimos")
                        .version("v1"))
                .addSecurityItem(new SecurityRequirement().addList(bearerAuth))
                .components(new Components()
                        .addSecuritySchemes(bearerAuth, new SecurityScheme()
                                .name(bearerAuth)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));

        if (StringUtils.hasText(serverUrl)) {
            String normalizedUrl = serverUrl.startsWith("http") ? serverUrl : "https://" + serverUrl;
            openAPI.addServersItem(new Server().url(normalizedUrl).description("Production"));
        } else {
            openAPI.addServersItem(new Server().url("/").description("Current host"));
        }

        return openAPI;
    }
}
