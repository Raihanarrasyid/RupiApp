package com.team7.rupiapp.api;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface AccountApi {

    @Operation(summary = "Account Detail")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Fetch detailed information about an Account from a currently logged-in User.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = AccountDetailResponseDto.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Success",
                                            value = """
                                            {
                                                "message": "Account detail fetched",
                                                "data": {
                                                    "full_name": "John Doe",
                                                    "email": "john.doe@example.com",
                                                    "account_number": "0774014731",
                                                    "balance": 5000000
                                                }
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. Required: /auth/signin or /auth/refresh",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(
                                            name = "Unauthorized",
                                            value = """
                                            {
                                                "message": "Unauthorized"
                                            }
                                            """
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<Object> getAccountDetail(@Valid Principal principal);

    public ResponseEntity<Object> getAccountMutation(@Valid Principal principal);

}
