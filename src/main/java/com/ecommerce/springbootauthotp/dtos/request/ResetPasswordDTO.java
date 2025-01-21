package com.ecommerce.springbootauthotp.dtos.request;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
@Data
public class ResetPasswordDTO {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String verificationCode;
    @NotBlank
    private String newPassword;
}
