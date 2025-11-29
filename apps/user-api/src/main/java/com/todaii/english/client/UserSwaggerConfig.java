package com.todaii.english.client;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UserSwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:8081/todaii-english/client-side");
        

        return new OpenAPI()
                .info(new Info()
                        .title("Todaii English Client API")
                        .version("v1")
                        .description("Todaii English Client API documentation"))
                .servers(List.of(server));
    }
}
