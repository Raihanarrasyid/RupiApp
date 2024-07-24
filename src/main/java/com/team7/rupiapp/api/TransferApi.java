package com.team7.rupiapp.api;

import java.security.Principal;
import java.util.UUID;

import com.team7.rupiapp.dto.destination.DestinationAddDto;
import com.team7.rupiapp.dto.destination.DestinationDetailDto;
import com.team7.rupiapp.dto.destination.DestinationFavoriteDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import com.team7.rupiapp.dto.transfer.TransferRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;

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

    @Operation(summary = "List Destination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success getting list of destination", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "data": [
                            {
                                "id": "809036d4-36fb-4a26-9879-aeba24959c60",
                                "fullname": "Destination 3",
                                "account_number": "3456789012",
                                "favorites": false
                            },
                            {
                                "id": "e9b68002-d99d-40e2-88e8-8dd0d6ad8c80",
                                "fullname": "Destination 4",
                                "account_number": "4567890123",
                                "favorites": true
                            },
                            {
                                "id": "6c8dc735-4127-4ca4-b33a-65054aae9636",
                                "fullname": "Destination 1",
                                "account_number": "1234567890",
                                "favorites": false
                            },
                            {
                                "id": "2289fd9a-d5c6-40fb-ba69-e269012b60be",
                                "fullname": "Destination 2",
                                "account_number": "2345678901",
                                "favorites": false
                            },
                            {
                                "id": "62da90b8-f19c-477e-8602-4356a2272f10",
                                "fullname": "asdfjkl1",
                                "account_number": "1234567893",
                                "favorites": false
                            }
                        ],
                        "message": "List of destinations"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """)))
    })
    public ResponseEntity<Object> getDestinations(@Valid Principal principal);

    @Operation(summary = "Add to Favorites")
    @RequestBody(required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "is_favorites":true
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "transaction added to favorites"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Wrong id destination", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                          "message": "Destination not found"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                          "message": "Failed parsing id to UUID"
                    }
                    """)))
    })
    public ResponseEntity<Object> addFavorites(@PathVariable("id") UUID id, @Valid @org.springframework.web.bind.annotation.RequestBody DestinationFavoriteDto requestDto);

    @Operation(summary = "Destination Detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "data": {
                            "fullname": "Destination 3",
                            "account_number": "3456789012",
                            "bank_name": "Rupi App"
                        },
                        "message": "transaction detail has been sent"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Wrong id destination", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                          "message": "Destination not found"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                          "message": "Failed parsing id to UUID"
                    }
                    """)))
    })
    public ResponseEntity<Object> getDetail(@PathVariable("id") UUID id);

    @Operation(summary = "Add Destination")
    @RequestBody(required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "account_number":"1234567893"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "data": {
                            "fullname": "asdfjkl1",
                            "account_number": "1234567893"
                        },
                        "message": "transaction has been added"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "User didn't found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Account number not found"
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
                            "account_number": "must not be null"
                        }
                    }
                    """)))
    })
    public ResponseEntity<Object> addDestination(@Valid @org.springframework.web.bind.annotation.RequestBody DestinationAddDto requestDto, Principal principal);

}
