package com.team7.rupiapp.api;

import java.security.Principal;
import java.util.UUID;

import com.team7.rupiapp.dto.transfer.destination.DestinationAddDto;
import com.team7.rupiapp.dto.transfer.destination.DestinationFavoriteDto;
import com.team7.rupiapp.dto.transfer.qris.QrisDto;
import com.team7.rupiapp.dto.transfer.qris.QrisGenerateCPMDto;
import com.team7.rupiapp.dto.transfer.qris.QrisGenerateMPMDto;
import com.team7.rupiapp.dto.transfer.transfer.TransferRequestDto;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
            @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "PIN must not be blank", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "pin": "PIN must not be blank"
                                }
                            }
                            """),
                    @ExampleObject(name = "Amount must not be null", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "amount": "Amount must not be null"
                                }
                            }
                            """),
                    @ExampleObject(name = "Destination ID must not be null", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "destination_id": "Destination ID must not be null"
                                }
                            }
                            """),
                    @ExampleObject(name = "Type must not be null", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "type": "Type must not be null"
                                }
                            }
                            """),
                    @ExampleObject(name = "Transaction Purpose must not be null", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "transaction_purpose": "Transaction Purpose must not be null"
                                }
                            }
                            """)
            }))
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
            @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "PIN must not be blank", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "pin": "PIN must not be blank"
                                }
                            }
                            """),
                    @ExampleObject(name = "QRIS code must not be blank", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "qris": "QRIS code must not be blank"
                                }
                            }
                            """),
                    @ExampleObject(name = "Amount must be greater than zero", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "amount": "Amount must be greater than zero"
                                }
                            }
                            """)
            }))
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
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                                "message": "QRIS format is not suitable"
                            }
                    """))),
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
                                        "merchant": "ZeRo Store",
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
    ResponseEntity<Object> getTransactionDetails(UUID transactionId, Principal principal);

    @Operation(summary = "Generate QRIS MPM Transaction")
    @RequestBody(required = true, content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "With Amount", value = """
                        {
                            "amount": "10000"
                        }
                    """),
            @ExampleObject(name = "Without Amount", value = """
                    {}
                    """)
    }))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "QRIS MPM transaction generated successfully", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "With Amount", value = """
                                     {
                                         "data": {
                                             "qris": "iVBORw0KGgoAAAANSUhEUgAAASwAAAEsCAIAAAD2HxkiAAAfLUlEQVR4Xu3TQY5rRw5EUe9/092zgniBulCI5EuVnWfoCEbqfaP++d91XUf9w/9wXdez7h/hdR12/wiv67D7R3hdh90/wus67P4RXtdh94/wug67f4TXddj9I7yuw+4f4XUddv8Ir+uw+0d4XYfdP8LrOuz+EV7XYfeP8LoOu3+E13XY/SO8rsPuH+F1HXb/CK/rsPtHeF2H3T/C6zrs/hFe12H3j/C6Drt/hNd12P0jvK7D7h/hdR02/0f4z1MG3+1M4dYN3gLb1amypxGfQroH746YH+WvXjP4bmcKt27wFtiuTpU9jfgU0j14d8T8KH/1msF3O1O4dYO3wHZ1quxpxKeQ7sG7I+ZH+avXDL7bmcKtG7wFtqtTZU8jPoV0D94dMT/KX71m8N3OFG7d4C2wXZ0qexrxKaR78O6I+dG9Hx0towxsq+jWy1EKKENUdpgaNPiQTyHt2Fv+MT+696OjZZSBbRXdejlKAWWIyg5TgwYf8imkHXvLP+ZH9350tIwysK2iWy9HKaAMUdlhatDgQz6FtGNv+cf86N6PjpZRBrZVdOvlKAWUISo7TA0afMinkHbsLf+YH9370dEyysC2im69HKWAMkRlh6lBgw/5FNKOveUf86P+o5E6v/XU4TbCLRXdehkpeNnTiE8hjURTXvbU+S3SEfOj/qOROr/11OE2wi0V3XoZKXjZ04hPIY1EU1721Pkt0hHzo/6jkTq/9dThNsItFd16GSl42dOITyGNRFNe9tT5LdIR86P+o5E6v/XU4TbCLRXdehkpeNnTiE8hjURTXvbU+S3SEfOj/qOROr/11OE2wi0V3XoZKXjZ04hPIY1EU1721Pkt0hHzo/6jkTq/9RQ6ZdgrO0wB29V3lsHLSMHLSJ3fIh0xP+o/GqnzW0+hU4a9ssMUsF19Zxm8jBS8jNT5LdIR86P+o5E6v/UUOmXYKztMAdvVd5bBy0jBy0id3yIdMT/qPxqp81tPoVOGvbLDFLBdfWcZvIwUvIzU+S3SEfOj/qOROr/1FDpl2Cs7TAHb1XeWwctIwctInd8iHTE/6j8aqfNbT8HLnkYwBZ1yB6erqBzBMrDd4MtInd8iHTE/6j8aqfNbT8HLnkYwBZ1yB6erqBzBMrDd4MtInd8iHTE/6j8aqfNbT8HLnkYwBZ1yB6erqBzBMrDd4MtInd8iHTE/6j8aqfNbT8HLnkYwBZ1yB6erqBzBMrDd4MtInd8iHTE/6j8aqfNbT8HLnkYwBZ1yB6erqBzBMrDd4MtInd8iHTE/uvej95Yfg0+IcEtFtygD21VUBtwC26pz6/aWf8yP7v3oveXH4BMi3FLRLcrAdhWVAbfAturcur3lH/Ojez96b/kx+IQIt1R0izKwXUVlwC2wrTq3bm/5x/zo3o/eW34MPiHCLRXdogxsV1EZcAtsq86t21v+MT+696P3lh+DT4hwS0W3KAPbVVQG3ALbqnPr9pZ/zI/iR+/xd2/6X0734N0R86P81Wv83Zv+l9M9eHfE/Ch/9Rp/96b/5XQP3h0xP8pfvcbfvel/Od2Dd0fMj/JXr/F3b/pfTvfg3REro9+P/7Rz+FK1VwbcRnwKKaC8hw//Zf+qj3kf/5fO4UvVXhlwG/EppIDyHj78l/2rPuZ9/F86hy9Ve2XAbcSnkALKe/jwX/av+pj38X/pHL5U7ZUBtxGfQgoo7+HDf9m/6mPex/+lc/hStVcG3EZ8CimgvIcP/2XzH8N/rQS3Ki976nALXu6kEJUj0bKXkQLK4OXBFFB2fot0xPwofnSEW5WXPXW4BS93UojKkWjZy0gBZfDyYAooO79FOmJ+FD86wq3Ky5463IKXOylE5Ui07GWkgDJ4eTAFlJ3fIh0xP4ofHeFW5WVPHW7By50UonIkWvYyUkAZvDyYAsrOb5GOmB/Fj45wq/Kypw634OVOClE5Ei17GSmgDF4eTAFl57dIR8yP4kdDpxzxqSh1fruXAsrQKYOXPXV+66nz2yjdMP8GvgE65YhPRanz270UUIZOGbzsqfNbT53fRumG+TfwDdApR3wqSp3f7qWAMnTK4GVPnd966vw2SjfMv4FvgE454lNR6vx2LwWUoVMGL3vq/NZT57dRumH+DXwDdMoRn4pS57d7KaAMnTJ42VPnt546v43SDetv4JOA7YptFd1+STmCZYjKzqeQOtx2cFr5raeA8ob1N/hNFdsV2yq6/ZJyBMsQlZ1PIXW47eC08ltPAeUN62/wmyq2K7ZVdPsl5QiWISo7n0LqcNvBaeW3ngLKG9bf4DdVbFdsq+j2S8oRLENUdj6F1OG2g9PKbz0FlDesv8Fvqtiu2FbR7ZeUI1iGqOx8CqnDbQenld96CihvWH+D31R1ytApR3zKU4jKEV+OUkC5g9OKxxXblZc9fcD6k/hC6JShU474lKcQlSO+HKWAcgenFY8rtisve/qA9SfxhdApQ6cc8SlPISpHfDlKAeUOTiseV2xXXvb0AetP4guhU4ZOOeJTnkJUjvhylALKHZxWPK7Yrrzs6QPWn8QXQqcMnXLEpzyFqBzx5SgFlDs4rXhcsV152dMHrD8ZfSHKjseVl6MUUAYvR2mHL3sKKEOnDF5GClEZ/NbTESujr6JvQNnxuPJylALK4OUo7fBlTwFl6JTBy0ghKoPfejpiZfRV9A0oOx5XXo5SQBm8HKUdvuwpoAydMngZKURl8FtPR6yMvoq+AWXH48rLUQoog5ejtMOXPQWUoVMGLyOFqAx+6+mIldFX0Teg7HhceTlKAWXwcpR2+LKngDJ0yuBlpBCVwW89HbEy+r7OF+J2kD+EFPbKESyDl6N0EB4CLyN10W1U/szK6Ps6X4jbQf4QUtgrR7AMXo7SQXgIvIzURbdR+TMro+/rfCFuB/lDSGGvHMEyeDlKB+Eh8DJSF91G5c+sjL6v84W4HeQPIYW9cgTL4OUoHYSHwMtIXXQblT+zMvq+zhfidpA/hBT2yhEsg5ejdBAeAi8jddFtVP7Myuj78IWD+FLFdsV2xXbF9pzoIZQH+UOdFKKywxRE5Q0HnnyF7x/Elyq2K7Yrtiu250QPoTzIH+qkEJUdpiAqbzjw5Ct8/yC+VLFdsV2xXbE9J3oI5UH+UCeFqOwwBVF5w4EnX+H7B/Gliu2K7Yrtiu050UMoD/KHOilEZYcpiMobDjz5Ct8/iC9VbFdsV2xXbM+JHkJ5kD/USSEqO0xBVN4w/2T0SVHZRVMoQ6fcES1HZfBbpA63EZ+KUvAyUte5fdP8aPSjo7KLplCGTrkjWo7K4LdIHW4jPhWl4GWkrnP7pvnR6EdHZRdNoQydcke0HJXBb5E63EZ8KkrBy0hd5/ZN86PRj47KLppCGTrljmg5KoPfInW4jfhUlIKXkbrO7ZvmR6MfHZVdNIUydMod0XJUBr9F6nAb8akoBS8jdZ3bN82P+o9GCigD21VUdp0p3P4J/IaqU474FFLolCEqj5h/w78BKaAMbFdR2XWmcPsn8BuqTjniU0ihU4aoPGL+Df8GpIAysF1FZdeZwu2fwG+oOuWITyGFThmi8oj5N/wbkALKwHYVlV1nCrd/Ar+h6pQjPoUUOmWIyiPm3/BvQAooA9tVVHadKdz+CfyGqlOO+BRS6JQhKo+Yf2PwG3wKKXgZqfNbpA63wHaCW1VUdj7lKXgZqYtuozLgdsT86OCP9imk4GWkzm+ROtwC2wluVVHZ+ZSn4GWkLrqNyoDbEfOjgz/ap5CCl5E6v0XqcAtsJ7hVRWXnU56Cl5G66DYqA25HzI8O/mifQgpeRur8FqnDLbCd4FYVlZ1PeQpeRuqi26gMuB0xPzr4o30KKXgZqfNbpA63wHaCW1VUdj7lKXgZqYtuozLgdsTK6Cv/BqTQKbvBW/BylLro1sudFDrlCLcqL3sKUfkzK6Ov/BuQQqfsBm/By1Hqolsvd1LolCPcqrzsKUTlz6yMvvJvQAqdshu8BS9HqYtuvdxJoVOOcKvysqcQlT+zMvrKvwEpdMpu8Ba8HKUuuvVyJ4VOOcKtysueQlT+zMroK/8GpNApu8Fb8HKUuujWy50UOuUItyovewpR+TMro1MGvx9T0CkPih7yMlKHW4jKDlPg5cHUdW4/88QbHxv858AUdMqDooe8jNThFqKywxR4eTB1ndvPPPHGxwb/OTAFnfKg6CEvI3W4hajsMAVeHkxd5/YzT7zxscF/DkxBpzwoesjLSB1uISo7TIGXB1PXuf3ME298bPCfA1PQKQ+KHvIyUodbiMoOU+DlwdR1bj8z/wa+AdhWPK7YrtiuOmUYLHvqotvvLDseK7/1FKLyZ+ZH8aOBbcXjiu2K7apThsGypy66/c6y47HyW08hKn9mfhQ/GthWPK7YrtiuOmUYLHvqotvvLDseK7/1FKLyZ+ZH8aOBbcXjiu2K7apThsGypy66/c6y47HyW08hKn9mfhQ/GthWPK7YrtiuOmUYLHvqotvvLDseK7/1FKLyZ1ZGhX8SUkAZ2FbRLcrAdoMvRyl0yhFuVWxXXkYKKAPbqnP7mSfeeOVfiBRQBrZVdIsysN3gy1EKnXKEWxXblZeRAsrAturcfuaJN175FyIFlIFtFd2iDGw3+HKUQqcc4VbFduVlpIAysK06t5954o1X/oVIAWVgW0W3KAPbDb4cpdApR7hVsV15GSmgDGyrzu1nnnjjlX8hUkAZ2FbRLcrAdoMvRyl0yhFuVWxXXkYKKAPbqnP7mfk38A0QlTv8ocfSCKYG8SXlt0gjg1Md+BkQlUfMv4FvgKjc4Q89lkYwNYgvKb9FGhmc6sDPgKg8Yv4NfANE5Q5/6LE0gqlBfEn5LdLI4FQHfgZE5RHzb+AbICp3+EOPpRFMDeJLym+RRganOvAzICqPmH8D3wBRucMfeiyNYGoQX1J+izQyONWBnwFRecT8G/4NSB1uHY+fwt+R8ClPHW6B7UQ0hbLjcRWVAbeOx/vmn/RPQupw63j8FP6OhE956nALbCeiKZQdj6uoDLh1PN43/6R/ElKHW8fjp/B3JHzKU4dbYDsRTaHseFxFZcCt4/G++Sf9k5A63DoeP4W/I+FTnjrcAtuJaAplx+MqKgNuHY/3zT/pn4TU4dbx+Cn8HQmf8tThFthORFMoOx5XURlw63i8b/5J/6THUofbDk4rHiu/jVLwMlIX3aIMg2VPAWWIyp+ZH/Uf/VjqcNvBacVj5bdRCl5G6qJblGGw7CmgDFH5M/Oj/qMfSx1uOziteKz8NkrBy0hddIsyDJY9BZQhKn9mftR/9GOpw20HpxWPld9GKXgZqYtuUYbBsqeAMkTlz8yP+o9+LHW47eC04rHy2ygFLyN10S3KMFj2FFCGqPyZldFX+IYIt1TnFjpTuB3ElxSPKy8jhU454lNI3anbN62MvsI3RLilOrfQmcLtIL6keFx5GSl0yhGfQupO3b5pZfQVviHCLdW5hc4UbgfxJcXjystIoVOO+BRSd+r2TSujr/ANEW6pzi10pnA7iC8pHldeRgqdcsSnkLpTt29aGX2Fb4hwS3VuoTOF20F8SfG48jJS6JQjPoXUnbp90/wofjSwXbHdwOmK7SoqR3zZ0w4sA9vKb5E63H4J/sp980/ymyq2K7YbOF2xXUXliC972oFlYFv5LVKH2y/BX7lv/kl+U8V2xXYDpyu2q6gc8WVPO7AMbCu/Repw+yX4K/fNP8lvqtiu2G7gdMV2FZUjvuxpB5aBbeW3SB1uvwR/5b75J/lNFdsV2w2crtiuonLElz3twDKwrfwWqcPtl+Cv3Lf+pH+hpw63EJUhukXZ+a2ngLLzW08j0RTKkc5UdBuVP7My+sq/wVOHW4jKEN2i7PzWU0DZ+a2nkWgK5UhnKrqNyp9ZGX3l3+Cpwy1EZYhuUXZ+6ymg7PzW00g0hXKkMxXdRuXPrIy+8m/w1OEWojJEtyg7v/UUUHZ+62kkmkI50pmKbqPyZ1ZGX/k3eOpwC1EZoluUnd96Cig7v/U0Ek2hHOlMRbdR+TMrox/DB3f4MlLwsqeAcsSnkHZEy1H5FP+RSB1uNzzxxvv4D9Dgy0jBy54CyhGfQtoRLUflU/xHInW43fDEG+/jP0CDLyMFL3sKKEd8CmlHtByVT/EfidThdsMTb7yP/wANvowUvOwpoBzxKaQd0XJUPsV/JFKH2w1PvPE+/gM0+DJS8LKngHLEp5B2RMtR+RT/kUgdbjfMv9H5BtwC2xXbFdsV2xXb1WNlYDsRTe2VB+Fd8LKngPKI+dHOj8YtsF2xXbFdsV2xXT1WBrYT0dReeRDeBS97CiiPmB/t/GjcAtsV2xXbFdsV29VjZWA7EU3tlQfhXfCyp4DyiPnRzo/GLbBdsV2xXbFdsV09Vga2E9HUXnkQ3gUvewooj5gf7fxo3ALbFdsV2xXbFdvVY2VgOxFN7ZUH4V3wsqeA8oj50ehHf2cZ2E5wq2I7wa3Ky1Ea8SmkEJUjWI4MTv1mfjT60d9ZBrYT3KrYTnCr8nKURnwKKUTlCJYjg1O/mR+NfvR3loHtBLcqthPcqrwcpRGfQgpROYLlyODUb+ZHox/9nWVgO8Gtiu0EtyovR2nEp5BCVI5gOTI49Zv50ehHf2cZ2E5wq2I7wa3Ky1Ea8SmkEJUjWI4MTv1mZfQVvgHYrrzsKaAMbD/Ff4anDreOx5WXo7Tj1DLSDetv4JOA7crLngLKwPZT/Gd46nDreFx5OUo7Ti0j3bD+Bj4J2K687CmgDGw/xX+Gpw63jseVl6O049Qy0g3rb+CTgO3Ky54CysD2U/xneOpw63hceTlKO04tI92w/gY+CdiuvOwpoAxsP8V/hqcOt47HlZejtOPUMtIN82/gG8DLSAHlDk5XXvY04lNIHW6d3yIFlJ3fIgUvI3V+6+kD5p/EJ4GXkQLKHZyuvOxpxKeQOtw6v0UKKDu/RQpeRur81tMHzD+JTwIvIwWUOzhdednTiE8hdbh1fosUUHZ+ixS8jNT5racPmH8SnwReRgood3C68rKnEZ9C6nDr/BYpoOz8Fil4GanzW08fMP8kPgm8jBRQ7uB05WVPIz6F1OHW+S1SQNn5LVLwMlLnt54+YP1JfCGwXXXKwLbisfJbpA634GVPAWXwsqcQlcFvkQLK4GVPN6y/gU8CtqtOGdhWPFZ+i9ThFrzsKaAMXvYUojL4LVJAGbzs6Yb1N/BJwHbVKQPbisfKb5E63IKXPQWUwcueQlQGv0UKKIOXPd2w/gY+CdiuOmVgW/FY+S1Sh1vwsqeAMnjZU4jK4LdIAWXwsqcb1t/AJwHbVacMbCseK79F6nALXvYUUAYvewpRGfwWKaAMXvZ0wxNvCP9gpBGf6qTgZaQQlSO+fCqFqAydW7e3/Jsn3hD+wUgjPtVJwctIISpHfPlUClEZOrdub/k3T7wh/IORRnyqk4KXkUJUjvjyqRSiMnRu3d7yb554Q/gHI434VCcFLyOFqBzx5VMpRGXo3Lq95d888YbwD0Ya8alOCl5GClE54sunUojK0Ll1e8u/mX/Dv6GTuugWZWC7Ylv5LdKITyEFLyMFLyMFlJ3fInW4hai8Yf5J/6RO6qJblIHtim3lt0gjPoUUvIwUvIwUUHZ+i9ThFqLyhvkn/ZM6qYtuUQa2K7aV3yKN+BRS8DJS8DJSQNn5LVKHW4jKG+af9E/qpC66RRnYrthWfos04lNIwctIwctIAWXnt0gdbiEqb5h/0j+pk7roFmVgu2Jb+S3SiE8hBS8jBS8jBZSd3yJ1uIWovGH9ycEvHJyCaHmv7HxqMAWUwctInd8iBS/vpSNWRl8NfsPgFETLe2XnU4MpoAxeRur8Fil4eS8dsTL6avAbBqcgWt4rO58aTAFl8DJS57dIwct76YiV0VeD3zA4BdHyXtn51GAKKIOXkTq/RQpe3ktHrIy+GvyGwSmIlvfKzqcGU0AZvIzU+S1S8PJeOmJ+1H80Uodbx+OK7YrtBk5XURmiWy8jBS8j7cDyY/g7VOf2TfOj/qOROtw6HldsV2w3cLqKyhDdehkpeBlpB5Yfw9+hOrdvmh/1H43U4dbxuGK7YruB01VUhujWy0jBy0g7sPwY/g7VuX3T/Kj/aKQOt47HFdsV2w2crqIyRLdeRgpeRtqB5cfwd6jO7ZvmR/1HI3W4dTyu2K7YbuB0FZUhuvUyUvAy0g4sP4a/Q3Vu37Qy+qrzDbh1PK68jBRQdn6LNIIp6JQjPoV0T+fdzu2G9V/Q+WDcOh5XXkYKKDu/RRrBFHTKEZ9Cuqfzbud2w/ov6Hwwbh2PKy8jBZSd3yKNYAo65YhPId3Tebdzu2H9F3Q+GLeOx5WXkQLKzm+RRjAFnXLEp5Du6bzbud2w/gs6H4xbx+PKy0gBZee3SCOYgk454lNI93Te7dxuWP8F+GDHY8XjqlMGL3sKKIOXPXV7t0hhr7zn+Z+x/gY+yfFY8bjqlMHLngLK4GVP3d4tUtgr73n+Z6y/gU9yPFY8rjpl8LKngDJ42VO3d4sU9sp7nv8Z62/gkxyPFY+rThm87CmgDF721O3dIoW98p7nf8b6G/gkx2PF46pTBi97CiiDlz11e7dIYa+85/mfMf8GvsHxWPG4Gix7GvEpTwfhoQi3qi8pd/hDSDfMv4FvcDxWPK4Gy55GfMrTQXgowq3qS8od/hDSDfNv4BscjxWPq8GypxGf8nQQHopwq/qScoc/hHTD/Bv4BsdjxeNqsOxpxKc8HYSHItyqvqTc4Q8h3TD/Br7B8VjxuBosexrxKU8H4aEIt6ovKXf4Q0g3PPHGM/b+7XwZaSSaQtnxuGK7gdMNnK6iMvgtUofbESujR+z9Y/ky0kg0hbLjccV2A6cbOF1FZfBbpA63I1ZGj9j7x/JlpJFoCmXH44rtBk43cLqKyuC3SB1uR6yMHrH3j+XLSCPRFMqOxxXbDZxu4HQVlcFvkTrcjlgZPWLvH8uXkUaiKZQdjyu2GzjdwOkqKoPfInW4HTE/yl+9hg8nuNXgy0gdbsHLUQpRGXD7GP8ZUeo6t2+aH8WP3sOHE9xq8GWkDrfg5SiFqAy4fYz/jCh1nds3zY/iR+/hwwluNfgyUodb8HKUQlQG3D7Gf0aUus7tm+ZH8aP38OEEtxp8GanDLXg5SiEqA24f4z8jSl3n9k3zo/jRe/hwglsNvozU4Ra8HKUQlQG3j/GfEaWuc/um+dG9Hx0towxsz+FLVVQG3DoeJ3wKqYtuveypi26j8oj5N/a+IVpGGdiew5eqqAy4dTxO+BRSF9162VMX3UblEfNv7H1DtIwysD2HL1VRGXDreJzwKaQuuvWypy66jcoj5t/Y+4ZoGWVgew5fqqIy4NbxOOFTSF1062VPXXQblUfMv7H3DdEyysD2HL5URWXAreNxwqeQuujWy5666DYqj5h/w78BqfNbpICy43HFdgOnK7bnRA95GSmgDF5GCigP8oeQbph/w78BqfNbpICy43HFdgOnK7bnRA95GSmgDF5GCigP8oeQbph/w78BqfNbpICy43HFdgOnK7bnRA95GSmgDF5GCigP8oeQbph/w78BqfNbpICy43HFdgOnK7bnRA95GSmgDF5GCigP8oeQbph/w78BqfNbpICy43HFdgOnK7bnRA95GSmgDF5GCigP8oeQbph/w78BqfNbTwFl+BNlT110izJ42VPXuY3goePmf5B/MFLnt54CyvAnyp666BZl8LKnrnMbwUPHzf8g/2Ckzm89BZThT5Q9ddEtyuBlT13nNoKHjpv/Qf7BSJ3fegoow58oe+qiW5TBy566zm0EDx03/4P8g5E6v/UUUIY/UfbURbcog5c9dZ3bCB46bv4H+QcjdX47mDrcgpc9dX4bpRFM7Yne9TJS6JSB7Qnzo/6jkTq/HUwdbsHLnjq/jdIIpvZE73oZKXTKwPaE+VH/0Uid3w6mDrfgZU+d30ZpBFN7one9jBQ6ZWB7wvyo/2ikzm8HU4db8LKnzm+jNIKpPdG7XkYKnTKwPWF+1H80Uue3g6nDLXjZU+e3URrB1J7oXS8jhU4Z2J4wP7r3o33Z00g0hTJ4Ganz28dSiMoRX0YKXkYKKD9g/sm9T/JlTyPRFMrgZaTObx9LISpHfBkpeBkpoPyA+Sf3PsmXPY1EUyiDl5E6v30shagc8WWk4GWkgPID5p/c+yRf9jQSTaEMXkbq/PaxFKJyxJeRgpeRAsoPmH9y75N82dNINIUyeBmp89vHUojKEV9GCl5GCig/YP5JftMaf9dTQDniU0gjg1OAZWC7YrvysqeRzhRuI9yaMD/KX73G3/UUUI74FNLI4BRgGdiu2K687GmkM4XbCLcmzI/yV6/xdz0FlCM+hTQyOAVYBrYrtisvexrpTOE2wq0J86P81Wv8XU8B5YhPIY0MTgGWge2K7crLnkY6U7iNcGvC/Ch/9Rp/11NAOeJTSCODU4BlYLtiu/Kyp5HOFG4j3JqwMnpd1/vuH+F1HXb/CK/rsPtHeF2H3T/C6zrs/hFe12H3j/C6Drt/hNd12P0jvK7D7h/hdR12/wiv67D7R3hdh90/wus67P4RXtdh94/wug67f4TXddj9I7yuw+4f4XUddv8Ir+uw+0d4XYfdP8LrOuz+EV7XYfeP8LoOu3+E13XY/SO8rsPuH+F1HXb/CK/rsPtHeF2H/R/rvGBjEcZxcwAAAABJRU5ErkJggg==",
                                             "expired_at": "2024-08-18T01:47:59.6610384"
                                         },
                                         "message": "Qris transaction has been created"
                                     }
                            """),
                    @ExampleObject(name = "Without Amount", value = """
                                    {
                                "data": {
                                    "qris": "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",
                                    "expired_at": "2024-08-18T01:47:17.4310958"
                                },
                                "message": "Qris transaction has been created"
                            }
                                """)
            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                        {
                            "message": "Unauthorized"
                        }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed due to invalid amount.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                        {
                            "message": "Validation failed",
                            "errors": {
                                "amount": "Amount must be greater than zero"
                            }
                        }
                    """)))
    })
    public ResponseEntity<Object> createTransactionQrisMPM(QrisGenerateMPMDto qrisDto, Principal principal);

    @Operation(summary = "Generate QRIS CPM Transaction")
    @RequestBody(required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "pin":"123456"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "QRIS CPM transaction generated successfully.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                        {
                            "data": {
                                "qris": "iVBORw0KGgoAAAANSUhEUgAAASwAAAEsCAIAAAD2HxkiAAAiH0lEQVR4Xu3TQY4rMRIk0b7/pWdWARhiaCiPn0mmpptv6TCSkgr1n/9zXden/tOH67rOuv+E1/Wx+094XR+7/4TX9bH7T3hdH7v/hNf1sftPeF0fu/+E1/Wx+094XR+7/4TX9bH7T3hdH7v/hNf1sftPeF0fu/+E1/Wx+094XR+7/4TX9bH7T3hdH7v/hNf1sftPeF0fu/+E1/Wx+094XR+7/4TX9bH/u/CxlZ855bK54AAAAASUVORK5CYII=",
                                "expired_at": "2024-08-17T00:15:41.652267"
                            },
                            "message": "Qris transaction has been created"
                        }
                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Invalid request body", value = """
                                {
                                    "message": "Invalid request body"
                                }
                            """),
                    @ExampleObject(name = "Invalid PIN", value = """
                                {
                                    "message": "Invalid PIN"
                                }
                            """)
            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                        {
                            "message": "Unauthorized"
                        }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "PIN must not be blank", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "pin": "PIN must not be blank"
                                }
                            }
                            """)
            }))
    })
    public ResponseEntity<Object> createTransactionQrisCPM(QrisGenerateCPMDto qrisDto, Principal principal);

}
