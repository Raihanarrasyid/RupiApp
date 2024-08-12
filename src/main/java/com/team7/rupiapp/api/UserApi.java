package com.team7.rupiapp.api;

import com.team7.rupiapp.dto.user.UserChangeEmailDto;
import com.team7.rupiapp.dto.user.UserChangePhoneDto;
import com.team7.rupiapp.dto.user.UserChangeProfileDto;
import com.team7.rupiapp.dto.user.UserVerifyOtpDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

public interface UserApi {
    @Operation(summary = "Get User Profile")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Fetch user profile information for the currently logged-in user.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                 "data": {
                                                     "avatar": "uploads/6abcca75-1c18-4459-aecb-28330522c0ab.jpg",
                                                     "name": "samsul",
                                                     "email": "samsul@gmail.com",
                                                     "phone": "6288899994444"
                                                 },
                                                 "message": "User profile"
                                             }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Unauthorized"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<Object> getUserProfile(Principal principal);

    @Operation(summary = "Change User Profile")
    @RequestBody(required = true, content = @Content(mediaType = "multipart/form-data"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Update user profile information including name and/or avatar.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Profile has been changed"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request due to invalid input or failed file upload.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
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
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Unauthorized"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<Object> changeProfile(Principal principal, @ModelAttribute UserChangeProfileDto userChangeProfileDto);

    @Operation(summary = "Change User Email")
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            value = """
                                    {
                                        "email": "asep@gmail.com"
                                    }
                                    """
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Email change request sent successfully.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Email change request has been sent"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples =
                            @ExampleObject(value = """
                                    {
                                        "message": "Email Already Registered"
                                    }
                                    """)

                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Unauthorized"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation failed due to invalid input.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
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

                    )
            )
    })
    public ResponseEntity<Object> changeEmail(Principal principal, @RequestBody UserChangeEmailDto userChangeEmailDto);

    @Operation(summary = "Verify Email OTP")
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            value = """
                                    {
                                        "otp": "123456"
                                    }
                                    """
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Email verified successfully.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Email has been verified"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request due to invalid or expired OTP.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
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
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Unauthorized"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation failed due to missing or invalid OTP.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Validation failed",
                                                "errors": {
                                                    "otp": "OTP is required"
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<Object> verifyEmail(Principal principal, @RequestBody UserVerifyOtpDto userVerifyOtpDto);

    @Operation(summary = "Change User Phone Number")
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            value = """
                                    {
                                        "phone": "6281234567890"
                                    }
                                    """
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Phone number change request sent successfully.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Number change request has been sent"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request due to invalid input.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
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
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Unauthorized"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation failed due to invalid input.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
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
                            }
                    )
            )
    })
    public ResponseEntity<Object> changeNumber(Principal principal, @RequestBody UserChangePhoneDto userChangePhoneDto);

    @Operation(summary = "Verify Phone Number OTP")
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(
                            value = """
                                    {
                                        "otp": "123456"
                                    }
                                    """
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Phone number verified successfully.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Number has been verified"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request due to invalid or expired OTP.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
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
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Unauthorized"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation failed due to missing or invalid OTP.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Validation failed",
                                                "errors": {
                                                    "otp": "OTP is required"
                                                }
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<Object> verifyNumber(Principal principal, @RequestBody UserVerifyOtpDto userVerifyOtpDto);

}
