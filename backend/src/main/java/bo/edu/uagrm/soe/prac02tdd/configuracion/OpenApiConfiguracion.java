package bo.edu.uagrm.soe.prac02tdd.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfiguracion {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Practica 02 TDD API")
                        .version("1.0")
                        .description("API para gestión de ventas aplicando TDD"));
    }
}