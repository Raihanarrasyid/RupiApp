package com.team7.rupiapp.api;

import com.team7.rupiapp.dto.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import java.security.Principal;

public interface UserApi {
    @Operation(summary = "Get User Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetch user profile information for the currently logged-in user.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "data": {
                            "avatar": "uploads/6abcca75-1c18-4459-aecb-28330522c0ab.jpg",
                            "name": "samsul",
                            "email": "samsul@gmail.com",
                            "phone": "6288899994444"
                        },
                        "message": "User profile"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """)))
    })
    public ResponseEntity<Object> getUserProfile(Principal principal);

    @Operation(summary = "Change User Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update user profile information including name and/or avatar.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Profile has been changed"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request due to invalid input or failed file upload.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                    @ExampleObject(name = "Invalid Image Type", value = """
                            {
                                "message": "Only image files are allowed"
                            }
                            """),
                    @ExampleObject(name = "Failed Upload", value = """
                            {
                                "message": "Failed to upload avatar"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """)))
    })
    public ResponseEntity<Object> changeProfile(Principal principal,
            @ModelAttribute UserChangeProfileDto userChangeProfileDto);

    @Operation(summary = "Change User Email")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
            {
                "email": "asep@gmail.com"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email change request sent successfully.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Email change request has been sent"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Email Already Registered"
                    }
                    """)

            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed due to invalid input.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                    @ExampleObject(name = "Email is required", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "email": "Email is required"
                                }
                            }
                            """),
                    @ExampleObject(name = "Invalid Email Format", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "email": "Invalid Email Format"
                                }
                            }
                            """),
                    @ExampleObject(name = "Email already been taken", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "email": "Email already been taken"
                                }
                            }
                            """)
            }

            ))
    })
    public ResponseEntity<Object> changeEmail(Principal principal,
            @RequestBody UserChangeEmailDto userChangeEmailDto);

    @Operation(summary = "Verify Email OTP")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
            {
                "otp": "123456"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Email has been verified"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request due to invalid or expired OTP.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                    @ExampleObject(name = "Invalid OTP", value = """
                            {
                                "message": "Invalid OTP"
                            }
                            """),
                    @ExampleObject(name = "OTP Expired", value = """
                            {
                                "message": "OTP expired"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed due to missing or invalid OTP.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Validation failed",
                        "errors": {
                            "otp": "OTP is required"
                        }
                    }
                    """)))
    })
    public ResponseEntity<Object> verifyEmail(Principal principal, @RequestBody UserVerifyOtpDto userVerifyOtpDto);

    @Operation(summary = "Change User Phone Number")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
            {
                "phone": "6281234567890"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Phone number change request sent successfully.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Number change request has been sent"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request due to invalid input.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                    @ExampleObject(name = "Phone Already Registered", value = """
                            {
                                "message": "Phone number already registered"
                            }
                            """),
                    @ExampleObject(name = "Invalid Phone Number", value = """
                            {
                                "message": "Number is not valid"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed due to invalid input.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                    @ExampleObject(name = "Phone is required", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "phone": "Phone is required"
                                }
                            }
                            """),
                    @ExampleObject(name = "Invalid Phone Format", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "phone": "Phone must be a valid phone number"
                                }
                            }
                            """),
                    @ExampleObject(name = "Phone already been taken", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "phone": "Phone already been taken"
                                }
                            }
                            """)
            }))
    })
    public ResponseEntity<Object> changeNumber(Principal principal,
            @RequestBody UserChangePhoneDto userChangePhoneDto);

    @Operation(summary = "Verify Phone Number OTP")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
            {
                "otp": "123456"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Phone number verified successfully.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Number has been verified"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request due to invalid or expired OTP.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                    @ExampleObject(name = "Invalid OTP", value = """
                            {
                                "message": "Invalid OTP"
                            }
                            """),
                    @ExampleObject(name = "OTP Expired", value = """
                            {
                                "message": "OTP expired"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed due to missing or invalid OTP.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Validation failed",
                        "errors": {
                            "otp": "OTP is required"
                        }
                    }
                    """)))
    })
    public ResponseEntity<Object> verifyNumber(Principal principal, @RequestBody UserVerifyOtpDto userVerifyOtpDto);

    @Operation(summary = "Verify User Password")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
            {
                "password": "currentPassword123"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password verified successfully.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                        {
                            "data": {
                                "signature": "W5ju0bLMfb1Dk7Y2GL7cf0fJFNMNkeoPJ0xvrZHxgDYj3iZfr8EPidcBHhWVdygE"
                            },
                            "message": "Password has been verified"
                        }
                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                    @ExampleObject(name = "Invalid password", value = """
                            {
                                "message": "Invalid password"
                            }
                            """),
                    @ExampleObject(name = "Invalid request body", value = """
                            {
                                "message": "Invalid request body"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed due to missing or invalid input.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Validation failed",
                        "errors": {
                            "password": "Password is required"
                        }
                    }
                    """)))
    })
    public ResponseEntity<Object> verifyPassword(Principal principal,
            @RequestBody UserVerifyPasswordDto userVerifyPasswordDto);

    @Operation(summary = "Change User Password")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
            {
                "password": "newPassword123",
                "confirmPassword": "newPassword123"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Password has been changed"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request due to invalid signature or other validation errors.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                    @ExampleObject(name = "Invalid Signature", value = """
                            {
                                "message": "Invalid or missing signature"
                            }
                            """),
                    @ExampleObject(name = "Passwords Do Not Match", value = """
                            {
                                "message": "Password and confirm password must be the same"
                            }
                            """),
                    @ExampleObject(name = "New Password Same as Old", value = """
                            {
                                "message": "New password cannot be the same as the old password"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed due to invalid input.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                    @ExampleObject(name = "Password Too Short", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "password": "Password must be at least 8 characters long."
                                }
                            }
                            """),
                    @ExampleObject(name = "Missing Special Characters", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "password": "Password must contain at least one special character."
                                }
                            }
                            """),
                    @ExampleObject(name = "Weak Password", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "password": "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
                                }
                            }
                            """),
                    @ExampleObject(name = "Confirm Password Missing", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "confirmPassword": "Confirm password is required."
                                }
                            }
                            """)
            }))
    })
    public ResponseEntity<Object> changePassword(Principal principal,
            @RequestHeader("X-SIGNATURE") String signature,
            @RequestBody UserChangePasswordDto userChangePasswordDto);

    @Operation(summary = "Verify User PIN")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
            {
                "pin": "123456"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PIN verified successfully.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                         "data": {
                             "signature": "Rsp2RlWVbq/6/fouhKQnf+LUya6g9zd2xUmyFTRnGcE6orWVhMmWJqA0Pj+gjQCF"
                         },
                         "message": "Pin has been verified"
                     }
                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                    @ExampleObject(name = "Invalid PIN", value = """
                            {
                                "message": "Invalid PIN"
                            }
                            """),
                    @ExampleObject(name = "Invalid Request Body", value = """
                            {
                                "message": "Invalid request body"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed due to missing or invalid input.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Validation failed",
                        "errors": {
                            "pin": "PIN is required"
                        }
                    }
                    """)))
    })
    public ResponseEntity<Object> verifyPin(Principal principal, @RequestBody UserVerifyPinDto userVerifyPinDto);

    @Operation(summary = "Change User PIN")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
            {
                "pin": "654321",
                "confirmPin": "654321"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PIN changed successfully.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "PIN has been changed"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                    @ExampleObject(name = "Invalid Signature", value = """
                            {
                                "message": "Invalid or missing signature"
                            }
                            """),
                    @ExampleObject(name = "PINs Do Not Match", value = """
                            {
                                "message": "PIN and confirm PIN must be the same"
                            }
                            """),
                    @ExampleObject(name = "New PIN Same as Old", value = """
                            {
                                "message": "New PIN cannot be the same as the old PIN"
                            }
                            """),
                    @ExampleObject(name = "Invalid Request Body", value = """
                            {
                                "message": "Invalid request body"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed due to invalid input.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                    @ExampleObject(name = "PIN is required", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "pin": "PIN is required"
                                }
                            }
                            """),
                    @ExampleObject(name = "Confirm PIN is required", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "confirmPin": "Confirm PIN is required"
                                }
                            }
                            """),
                    @ExampleObject(name = "PIN & Confirm PIN is required", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "pin": "PIN is required",
                                    "confirm_pin": "Confirm PIN is required"
                                }
                            }
                            """),
                    @ExampleObject(name = "Invalid PIN Format", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "pin": "PIN must be 6 characters long."
                                }
                            }
                            """)
            }))
    })
    public ResponseEntity<Object> changePin(Principal principal, @RequestHeader("X-SIGNATURE") String signature,
            @RequestBody UserChangePinDto userChangePinDto);

}
