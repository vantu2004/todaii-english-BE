package com.todaii.english.server;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminSwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
    	Server server = new Server();
        server.setUrl("http://localhost:8082/todaii-english/server-side");
        
        return new OpenAPI()
                .info(new Info()
                        .title("Todaii English Admin API")
                        .version("v1")
                        .description("Todaii English Admin API documentation"))
                .servers(List.of(server));
    }
}
