package com.team7.rupiapp.api;

import java.security.Principal;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import com.team7.rupiapp.dto.transfer.TransferRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface TransferApi {

    @Operation(summary = "Transfer Intrabank")
    @RequestBody(required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "destination_id": "8afa9b30-cab4-48d4-a2f2-0576355ee639",
                "amount": 100000,
                "description": "Transfer to samsul",
                "type": "TRANSFER",
                "pin": "123456",
                "transaction_purpose": "OTHER"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer success", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "data": {
                            "destination_detail": {
                                "name": "samsul",
                                "account_number": "0813863478"
                            },
                            "mutation_detail": {
                                "amount": 100000.0,
                                "created_at": "2024-07-24T00:24:52.776857"
                            },
                            "user_detail": {
                                "name": "asep",
                                "account_number": "7749261880"
                            }
                        },
                        "message": "Transfer success"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Validation failed",
                        "errors": {
                            "amount": "Amount must not be null",
                            "pin": "PIN must not be blank",
                            "destination_id": "Destination ID must not be null",
                            "type": "Type must not be null",
                            "transaction_purpose": "Transaction Purpose must not be null"
                        }
                    }
                    """)))
    })
    ResponseEntity<Object> transferIntrabank(@Valid TransferRequestDto requestDto, Principal principal);

}
