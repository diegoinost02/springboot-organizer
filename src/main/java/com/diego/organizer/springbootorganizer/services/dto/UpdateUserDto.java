package com.diego.organizer.springbootorganizer.services.dto;

import lombok.Data;

@Data
public class UpdateUserDto {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String newPassword;
}
