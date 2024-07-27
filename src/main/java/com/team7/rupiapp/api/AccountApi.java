package com.team7.rupiapp.api;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationSummaryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
                                                            "account_number": "7484357077",
                                                            "balance": "Rp39.000.000,00"
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
                                            name = "Success with data",
                                            value = """
                                                    {
                                                        "message": "Account Mutations Summary fetched",
                                                        "data": {
                                                            "total_income": "Rp50.000,00",
                                                            "total_expense": "Rp150.000,00",
                                                            "total_income_percentage": 25,
                                                            "total_expense_percentage": 75,
                                                            "total_earnings": "-Rp100.000,00",
                                                            "range_start_mutation_date": "2024-07-01T00:00:00",
                                                            "range_end_mutation_date": "2024-07-27T14:56:47.6634487"
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Success no data",
                                            value = """
                                                    {
                                                        "message": "Account Mutations Summary fetched",
                                                        "data": {
                                                            "total_income": "Rp0,00",
                                                            "total_expense": "Rp0,00",
                                                            "total_income_percentage": 0.0,
                                                            "total_expense_percentage": 0.0,
                                                            "total_earnings": "Rp0,00",
                                                            "range_start_mutation_date": "2024-06-01T00:00:00",
                                                            "range_end_mutation_date": "2024-06-30T23:59:59.999999999"
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
    public ResponseEntity<Object> getAccountMutationSummary(
            @Valid Principal principal,
            @Parameter(
                    description = "Year for which the summary is required (1900...2100)",
                    example = "2024",
                    schema = @Schema(implementation = Integer.class, minimum = "1900", maximum = "2100")
            )
            @NotNull @Min(1900) @Max(2100) Integer year,
            @Parameter(
                    description = "Month for which the summary is required (1...12)",
                    example = "06",
                    schema = @Schema(implementation = Integer.class, minimum = "1", maximum = "12")
            )
            @NotNull @Min(1) @Max(12) Integer month);

    public ResponseEntity<Object> getAccountMutation(@Valid Principal principal);

}
