package com.team7.rupiapp.api;

import com.team7.rupiapp.dto.demo.DemoQrisCPMDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public interface DemoApi {
    @Operation(summary = "Demo QRIS CPM Transaction")
    @RequestBody(required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "qris": "hQVDUFYwMWEaTw5BMDAwMDAwMDg4ODg4OFAIUVJJU0NQTVpij1oQMTIzNDU2Nzg5MTAxMTEyMV8gB1JVUElBUFBfLQJpZF9QClJVUElBUFAuTUVkX58QDjA4MDEwWjAzMDAwMDAwnxkkYTRiZjUzNTctZmRkZi00NjIxLWFiNGUtZDkyMzcwZTQ3YWNmn04kNjZiMDczZWMtY2VkYi00OWQ0LTk1ZDUtZDlkMWMxNTFiYWNl",
                "merchant": "Zero Store",
                "amount": 10000
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Demo Qris CPM success", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Demo Qris CPM success"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request due to invalid QRIS, insufficient balance, or expired QRIS.", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Invalid QRIS", value = """
                            {
                                "message": "Qris not valid"
                            }
                            """),
                    @ExampleObject(name = "Insufficient Balance", value = """
                            {
                                "message": "Insufficient balance"
                            }
                            """),
                    @ExampleObject(name = "QRIS Expired", value = """
                            {
                                "message": "Qris is expired"
                            }
                            """),
                    @ExampleObject(name = "Invalid request body", value = """
                            {
                                "message": "Invalid request body"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation Failed", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Amount is required", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "amount": "Amount is required"
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
                            """),
                    @ExampleObject(name = "Merchant is required", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "merchant": "Merchant is required"
                                }
                            }
                            """),
                    @ExampleObject(name = "Qris is required", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "qris": "Qris is required"
                                }
                            }
                            """)
            }))
    })
    public ResponseEntity<Object> demoQrisCPM(@Valid @RequestBody DemoQrisCPMDto demoQrisCPMDto);
}
