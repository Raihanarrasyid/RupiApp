package com.team7.rupiapp.dto.user;

import lombok.Data;

@Data
public class UserProfileResponseDto {
    private String avatar;

    private String name;

    private String email;

    private String phone;
}
