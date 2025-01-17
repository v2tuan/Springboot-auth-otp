package com.ecommerce.springbootauthotp.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyAccountDTO {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String verificationCode;
}
