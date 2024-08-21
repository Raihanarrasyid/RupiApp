package com.team7.rupiapp.api;

import java.security.Principal;

import com.team7.rupiapp.dto.auth.forgot.ForgotUsernameDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

import com.team7.rupiapp.dto.auth.forgot.ForgotPasswordDto;
import com.team7.rupiapp.dto.auth.pin.SetPinDto;
import com.team7.rupiapp.dto.auth.refresh.RefreshTokenDto;
import com.team7.rupiapp.dto.auth.signin.SigninDto;
import com.team7.rupiapp.dto.auth.signup.ResendVerificationDto;
import com.team7.rupiapp.dto.auth.signup.SetPasswordDto;
import com.team7.rupiapp.dto.auth.signup.SignupDto;
import com.team7.rupiapp.dto.auth.verify.VerificationDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface AuthApi {
    @Operation(summary = "Register")
    @RequestBody(required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "full_name": "Samsul Bahri",
                "username": "samsul",
                "email": "samsul@rupiapp.me",
                "phone": "628123456789"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Signup success", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "No Password", value = """
                            {
                                "message": "Signup success",
                                "data": {
                                    "username": "samsul",
                                    "email": "samsul@rupiapp.me",
                                    "phone": "628123456789",
                                    "password": "TUkpvX6u"
                                }
                            }
                            """),
                    @ExampleObject(name = "With Password", value = """
                            {
                                "message": "Signup success",
                                "data": {
                                    "username": "samsul",
                                    "email": "samsul@rupiapp.me",
                                    "phone": "628123456789",
                                    "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiMmFkMzhkMy0wY2VhLTQyZjUtOWUyZi0yNTVmZDMwNzFjN2YiLCJzdWIiOiJhc2VwMiIsImlhdCI6MTcyMTU4NzgwNCwiZXhwIjoxNzIxNjc0MjA0fQ.CSxzotNDl1DT1dNHTyNwLKOc8P3hJEtan1bMFwzTGTo",
                                    "refresh_token": "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI5Njk4NmRiOC1lNDBjLTRkMTUtOGJmMi04M2JiM2RlZjQyNjIiLCJzdWIiOiJhc2VwMiIsImlhdCI6MTcyMTU4NzgwNCwiZXhwIjoxNzI0MTc5ODA0fQ.AwG4S71QM060VaslpejUoc5yJBVi76rCZl0nTKwOjfU"
                                }
                            }
                            """)
            })),
            @ApiResponse(responseCode = "422", description = "Validation failed", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Required", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "phone": "Phone is required",
                                    "email": "Email is required"
                                }
                            }
                            """),
                    @ExampleObject(name = "Already been taken", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "username": "Username already been taken"
                                }
                            }
                            """)
            }))
    })
    public ResponseEntity<Object> signup(SignupDto signupDto);

    @Operation(summary = "Login")
    @RequestBody(required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "username": "samsul",
                "password": "TUkpvX6u"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signin success", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Signin success",
                        "data": {
                            "username": "samsul",
                            "email": "samsul@rupiapp.me",
                            "phone": "628123456789",
                            "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIwZDEzMDQ4OC1jN2Q2LTRhZjItODU3OC1kYTk3NWNiODM2MDIiLCJzdWIiOiJzYW1zdWwiLCJpYXQiOjE3MjE1ODAxNTUsImV4cCI6MTcyMTY2NjU1NX0.-s3S1DsM9eL8OA0XwxyYq5sNGBD2WoeS-2BrkpFV5lU",
                            "refresh_token": "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3NzI3OGJkMC1iYjI5LTQ2OGMtYTdmOS1kMjEyN2IwNmY0NDgiLCJzdWIiOiJzYW1zdWwiLCJpYXQiOjE3MjE1ODAxNTUsImV4cCI6MTcyNDE3MjE1NX0.hvnGA7sZIIpZVuwk_noF2CY3Qs9rWd6Ma7xGYB-pino"
                        }
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Invalid username or password"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Validation failed",
                        "errors": {
                            "password": "Password is required",
                            "username": "Username is required"
                        }
                    }
                    """)))
    })
    public ResponseEntity<Object> signin(SigninDto signinDto);

    @Operation(summary = "Resend verification otp")
    @RequestBody(required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "username": "samsul"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success resend verification otp", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Resend verification success"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "OTP already sent, please wait for 59 seconds"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "User not registered"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Validation failed",
                        "errors": {
                            "username": "Username is required"
                        }
                    }
                    """)))
    })
    public ResponseEntity<Object> resendVerificationEmail(ResendVerificationDto resendVerificationDto);

    @Operation(summary = "Verification")
    @RequestBody(required = true, content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "Verify Login", value = """
                        {
                            "type": "LOGIN",
                            "otp": "975226"
                        }
                    """),
            @ExampleObject(name = "Verify Forgot Password", value = """
                    {
                        "type": "FORGOT_PASSWORD",
                        "otp": "231004",
                        "username": "samsul",
                        "password": "321#4a567U8",
                        "confirm_password": "321#4a567U8"
                    }
                    """)
    }))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "LOGIN", value = """
                            {
                                "message": "Login verified"
                            }
                            """),
                    @ExampleObject(name = "FORGOT_PASSWORD", value = """
                            {
                                "message": "Password changed"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", description = "OTP Validate", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Invalid", value = """
                            {
                                "message": "Invalid OTP"
                            }
                            """),
                    @ExampleObject(name = "Expired", value = """
                            {
                                "message": "OTP expired"
                            }
                            """),
                    @ExampleObject(name = "Different password and confirm password", value = """
                            {
                                "message": "Password and confirm password must be the same"
                            }
                            """)

            })),
            @ApiResponse(responseCode = "422", description = "Validation failed", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Password character requirements", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "password": "Password must contain at least uppercase letters, special characters."
                                }
                            }
                            """),
                    @ExampleObject(name = "OTP type is required", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "type": "OTP type is required"
                                }
                            }
                            """),
                    @ExampleObject(name = "OTP Type", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "type": "otp type must be one of LOGIN, FORGOT_PASSWORD"
                                }
                            }
                            """),
                    @ExampleObject(name = "Username is required", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "username": "Username is required when type is FORGOT_PASSWORD"
                                }
                            }
                            """),
                    @ExampleObject(name = "Password is required", value = """
                            {
                                 "message": "Validation failed",
                                 "errors": {
                                     "password": "Password is required when type is FORGOT_PASSWORD"
                                 }
                             }
                            """),
                    @ExampleObject(name = "Confirm password is required", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "confirm_password": "Confirm password is required when type is FORGOT_PASSWORD"
                                }
                            }
                            """),
                    @ExampleObject(name = "OTP is required", value = """
                            {
                                "message": "Validation failed",
                                "errors": {
                                    "otp": "OTP is required"
                                }
                            }
                            """)
            })),
    })
    public ResponseEntity<Object> verify(@RequestHeader(value = "User-Agent") String userAgent,
                                         VerificationDto verificationDto,
                                         Principal principal);

    @Operation(summary = "Forgot Password")
    @RequestBody(required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "username": "samsul"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "OTP for forgot password has been sent."
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "User not registered"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Validation failed",
                        "errors": {
                            "username": "Username is required"
                        }
                    }
                    """)))
    })
    public ResponseEntity<Object> forgotPasswordRequest(ForgotPasswordDto forgotPasswordDto);

    @Operation(summary = "Login with PIN")
    @RequestBody(required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "refresh_token": "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3NzI3OGJkMC1iYjI5LTQ2OGMtYTdmOS1kMjEyN2IwNmY0NDgiLCJzdWIiOiJzYW1zdWwiLCJpYXQiOjE3MjE1ODAxNTUsImV4cCI6MTcyNDE3MjE1NX0.hvnGA7sZIIpZVuwk_noF2CY3Qs9rWd6Ma7xGYB-pino",
                "pin": "123456"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signin success", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Successfully refreshed token",
                        "data": {
                            "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3YzQxYmVjZS0zZDI5LTQ4MWYtOTczMS0xMTJmYmU0NzU4ZTIiLCJzdWIiOiJzYW1zdWwiLCJpYXQiOjE3MjE1OTczOTQsImV4cCI6MTcyMTY4Mzc5NH0.HXEoKENW2WSfGE5YaLuZPIOUhzFJGqlibbWkZyvLp80"
                        }
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Invalid pin"
                    }
                    """))),
            @ApiResponse(responseCode = "422", description = "Validation failed", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Validation failed",
                        "errors": {
                            "refresh_token": "Refresh token is required",
                            "pin": "PIN is required"
                        }
                    }
                    """)))
    })
    public ResponseEntity<Object> refreshToken(RefreshTokenDto refreshTokenDto);

    @Operation(summary = "Set New Password")
    @RequestBody(required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "password": "123#4a567U8",
                "confirm_password": "123#4a567U8"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success set Password", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Password has been successfully updated."
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "403", description = "Auth Not verified", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Login is not verified"
                    }
                    """)))
    })
    public ResponseEntity<Object> setPassword(SetPasswordDto setPasswordDto, Principal principal);

    @Operation(summary = "Set PIN")
    @RequestBody(required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
                "pin": "123456",
                "confirm_pin": "123456"
            }
            """)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success set PIN", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "PIN has been successfully set."
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """))),
            @ApiResponse(responseCode = "403", description = "Auth Not verified ", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Login is not verified"
                    }
                    """)))
    })
    public ResponseEntity<Object> setPin(SetPinDto setPinDto, Principal principal);

    @Operation(summary = "Logout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout success", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Signout success"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Unauthorized"
                    }
                    """)))
    })
    public ResponseEntity<Object> signOut();

    @Operation(summary = "Forgot Username")
    @RequestBody(
            required = true, content = @Content(
            mediaType = "application/json", examples = {
            @ExampleObject(
                    name = "Forgot Username by Email", value = """
                    {
                        "destination": "samsul@rupiapp.me"
                    }
                    """
            ),
            @ExampleObject(
                    name = "Forgot Username by Phone", value = """
                    {
                        "destination": "628123456789"
                    }
                    """
            )
    }
    )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Forgot username success", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Via Email", value = """
                            {
                                "message": "Username has been sent to your email"
                            }
                            """),
                    @ExampleObject(name = "Via Phone", value = """
                            {
                                "message": "Username has been sent to your phone"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Invalid Request Body", value = """
                            {
                                "message": "Invalid request body"
                            }
                            """),
                    @ExampleObject(name = "User Not Registered", value = """
                            {
                                "message": "User not registered"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "422", description = "Validation failed", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                        "message": "Validation failed",
                        "errors": {
                            "destination": "Destination is required"
                        }
                    }
                    """)))
    })
    public ResponseEntity<Object> forgotUsernameRequest(@Valid @RequestBody ForgotUsernameDto forgotUsernameDto);

}
