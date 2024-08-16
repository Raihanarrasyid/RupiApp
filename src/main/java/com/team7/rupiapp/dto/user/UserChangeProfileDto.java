package com.team7.rupiapp.dto.user;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserChangeProfileDto {
    @Size(min = 1, max = 15, message = "Name must be between 1 and 15 characters long")
    private String name;

    @Parameter(description = "User avatar image", schema = @Schema(type = "string", format = "binary"))
    private MultipartFile avatar;
}
