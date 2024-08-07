package com.team7.rupiapp.api;

import java.security.Principal;
import java.util.UUID;

import com.team7.rupiapp.dto.destination.DestinationAddDto;
import com.team7.rupiapp.dto.destination.DestinationFavoriteDto;
import com.team7.rupiapp.dto.qris.QrisDto;
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
                            },
                            "description": "thr untuk samsul",
                            "transaction_purpose": "OTHER"
                        },
                        "message": "Transfer success"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request due to invalid input", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Invalid UUID Format", value = """
                            {
                                "message": "Invalid UUID format for field 'destination_id'"
                            }
                            """),
                    @ExampleObject(name = "Destination Not Found", value = """
                            {
                                "message": "Destination not found"
                            }
                            """),
                    @ExampleObject(name = "Destination does not belong to the sender", value = """
                            {
                                "message": "Destination does not belong to the sender"
                            }
                            """),
                    @ExampleObject(name = "Invalid PIN", value = """
                            {
                                "message": "Invalid PIN"
                            }
                            """),
                    @ExampleObject(name = "Insufficient balance", value = """
                            {
                                "message": "Insufficient balance"
                            }
                            """)
            })),
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
    ResponseEntity<Object> transferIntrabank(TransferRequestDto requestDto, Principal principal);

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
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Unauthorized", value = """
                            {
                                "message": "Unauthorized"
                            }
                            """),
                    @ExampleObject(name = "Invalid", value = """
                            {
                                "message": "Invalid token signature"
                            }
                            """)
            }))
    })
    public ResponseEntity<Object> getDestinations(Principal principal);

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
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Unauthorized", value = """
                            {
                                "message": "Unauthorized"
                            }
                            """),
                    @ExampleObject(name = "Invalid", value = """
                            {
                                "message": "Invalid token signature"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "422", description = "Validation failed", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Failed parsing id to UUID"
                    }
                    """)))
    })
    public ResponseEntity<Object> addFavorites(UUID id, DestinationFavoriteDto requestDto);

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
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Unauthorized", value = """
                            {
                                "message": "Unauthorized"
                            }
                            """),
                    @ExampleObject(name = "Invalid", value = """
                            {
                                "message": "Invalid token signature"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "422", description = "Validation failed", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Failed parsing id to UUID"
                    }
                    """)))
    })
    public ResponseEntity<Object> getDetail(UUID id);

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
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Unauthorized", value = """
                            {
                                "message": "Unauthorized"
                            }
                            """),
                    @ExampleObject(name = "Invalid", value = """
                            {
                                "message": "Invalid token signature"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "422", description = "Validation failed", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Validation failed",
                        "errors": {
                            "account_number": "must not be null"
                        }
                    }
                    """)))
    })
    public ResponseEntity<Object> addDestination(DestinationAddDto requestDto, Principal principal);

    @Operation(summary = "QRIS Transaction")
    @RequestBody(required = true, content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "Static QRIS Transaction", value = """
                        {
                            "qris": "00020101021126570011ID.DANA.WWW011893600915310714782702091071478270303UMI51440014ID.CO.QRIS.WWW0215ID10210611835340303UMI5204594553033605802ID5910ZeRo Store6013Kab. Sidoarjo61056127563048170",
                            "amount": "10000",
                            "description": "jajan",
                            "pin": "123456"
                        }
                    """),
            @ExampleObject(name = "Dynamic QRIS Transaction", value = """
                        {
                            "qris": "00020101021226590016ID.CO.SHOPEE.WWW011893600918000093289502069328950303UME51440014ID.CO.QRIS.WWW0215ID20200175965170303UME520453995303360540811100.005802ID5908Exabytes6015KOTA JAKARTA SE61051295062250521669117d81e5a1-2223304630411F5",
                            "description": "lala",
                            "pin": "123456"
                        }
                    """)
    }))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "QRIS Transaction Created", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Static QRIS Transaction Response", value = """
                                {
                                    "data": {
                                        "merchant": "ZeRo Store",
                                        "amount": "10000.0",
                                        "description": "jajan"
                                    },
                                    "message": "Qris transaction has been created"
                                }
                            """),
                    @ExampleObject(name = "Dynamic QRIS Transaction Response", value = """
                                {
                                    "data": {
                                        "transaction_id": "0521669117d81e5a1-2223304",
                                        "merchant": "Exabytes",
                                        "amount": "11100.0",
                                        "description": "lala"
                                    },
                                    "message": "Qris transaction has been created"
                                }
                            """)
            })),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Invalid PIN", value = """
                            {
                                "message": "Invalid PIN"
                            }
                            """),
                    @ExampleObject(name = "Insufficient Balance", value = """
                            {
                                "message": "Insufficient balance"
                            }
                            """),
                    @ExampleObject(name = "Transaction Already Exists", value = """
                            {
                                "message": "Transaction already exists"
                            }
                            """),
                    @ExampleObject(name = "Amount is required", value = """
                            {
                                "message": "Amount is required"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Validation failed",
                        "errors": {
                            "pin": "PIN must not be blank",
                            "qris": "QRIS code must not be blank"
                        }
                    }
                    """)))
    })
    ResponseEntity<Object> createTransactionQris(QrisDto qrisDto, Principal principal);

    @Operation(summary = "Get QRIS Detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "QRIS Detail Retrieved", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Static QRIS Detail", value = """
                                {
                                    "data": {
                                        "type": "static",
                                        "merchant": "ZeRo Store"
                                    },
                                    "message": "Qris detail has been sent"
                                }
                            """),
                    @ExampleObject(name = "Dynamic QRIS Detail", value = """
                                {
                                    "data": {
                                        "type": "dynamic",
                                        "transaction_id": "0521669117d81e5a1-2223396",
                                        "merchant": "Exabytes",
                                        "amount": "11100.00"
                                    },
                                    "message": "Qris detail has been sent"
                                }
                            """)
            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                                "message": "Unauthorized"
                            }
                    """)))
    })
    ResponseEntity<Object> getDetailQris(String qris);

    @Operation(summary = "Get Transaction Detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction Detail Retrieved", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "QRIS Transaction Detail", value = """
                                {
                                    "data": {
                                        "transaction_id": "3a83dc93-9f62-4d8a-995a-885d9d3a0353",
                                        "amount": "10000.0",
                                        "description": "jajan baru"
                                    },
                                    "message": "Transaction details retrieved"
                                }
                            """),
                    @ExampleObject(name = "Non-QRIS Transaction Detail", value = """
                                {
                                    "data": {
                                        "receiver_detail": {
                                            "name": "user1",
                                            "account_number": "9785232178"
                                        },
                                        "mutation_detail": {
                                            "amount": 50000.0,
                                            "created_at": "2024-08-06T23:42:39.226771"
                                        },
                                        "sender_detail": {
                                            "name": "user3",
                                            "account_number": "3141971266"
                                        },
                                        "description": "test",
                                        "transaction_purpose": "OTHER"
                                    },
                                    "message": "Transaction details retrieved"
                                }
                            """)
            })),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Invalid Argument Type", value = """
                                {
                                    "message": "Invalid argument type"
                                }
                            """),
                    @ExampleObject(name = "Transaction Not Found", value = """
                                {
                                    "message": "Transaction not found"
                                }
                            """)
            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                                "message": "Unauthorized"
                            }
                    """)))
    })
    ResponseEntity<Object> getTransactionDetails(UUID transactionId);
}
