package com.bensiebert.codelib.testapp;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "CodeLib Test Application API",
                version = "1.0",
                description = "This is a test application for the CodeLib library."
        )
)
@SecurityScheme(
        type = SecuritySchemeType.APIKEY,
        name = "token",
        in = SecuritySchemeIn.HEADER,
        paramName = "Authorization",
        description = "API key authentication using the Authorization header"
)
public class OpenAPIConfig {
}
