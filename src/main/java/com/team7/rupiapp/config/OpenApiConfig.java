package com.team7.rupiapp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "RupiApp API Support",
                        email = "dev@rupiapp.me",
                        url = "https://rupiapp.me"
                ),
                title = "RupiApp API",
                version = "1.0",
                description = "OpenApi documentation for RupiApp API"
        ),
        servers = {
                @Server(
                        url = "https://{host}",
                        variables = {
                                @ServerVariable(
                                        name = "host",
                                        defaultValue = "api.rupiapp.me"
                                )
                        }
                ),
                @Server(
                        url = "http://{host}",
                        variables = {
                                @ServerVariable(
                                        name = "host",
                                        defaultValue = "localhost:8080"
                                )
                        }
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

}
