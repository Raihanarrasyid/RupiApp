package com.team7.rupiapp.api;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationSummaryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

public interface AccountApi {

    @Operation(summary = "Account Detail")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Fetch Account Detail from a currently logged-in User.",
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

    @Operation(summary = "Account Mutation Summary")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Fetch Account Mutation summary from a currently logged-in User.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = AccountMutationSummaryResponseDto.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Success",
                                            value = """
                                            {
                                                "message": "Account Mutations Summary fetched",
                                                "data": {
                                                    "total_income": 50000,
                                                    "total_expense": 150000,
                                                    "total_earnings": -100000,
                                                    "range_start_mutation_date": "2024-07-01T18:44:20.5541844",
                                                    "range_end_mutation_date": "2024-07-31T18:44:20.5541844"
                                                }
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "No data",
                                            value = """
                                            {
                                                "message": "Account Mutations Summary fetched",
                                                "data": {
                                                    "total_income": 0,
                                                    "total_expense": 0,
                                                    "total_earnings": 0,
                                                    "range_start_mutation_date": "2024-07-01T18:44:20.5541844",
                                                    "range_end_mutation_date": "2024-07-31T18:44:20.5541844"
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
    public ResponseEntity<Object> getAccountMutationSummary(@Valid Principal principal);

    public ResponseEntity<Object> getAccountMutation(@Valid Principal principal);

}
