package com.team7.rupiapp.dto.user;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserChangeProfileDto {
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 15, message = "Name must be between 1 and 15 characters long")
    private String name;

    private MultipartFile avatar;
}
