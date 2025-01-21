package com.ecommerce.springbootauthotp.controllers;

import com.ecommerce.springbootauthotp.dtos.request.ResetPasswordDTO;
import com.ecommerce.springbootauthotp.dtos.request.UserDTO;
import com.ecommerce.springbootauthotp.dtos.request.VerifyAccountDTO;
import com.ecommerce.springbootauthotp.dtos.response.ResponseObject;
import com.ecommerce.springbootauthotp.entity.User;
import com.ecommerce.springbootauthotp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    //can we register an "admin" user ?
    public ResponseEntity<ResponseObject> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
    ) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(errorMessages.toString())
                    .build());
        }

        if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            //registerResponse.setMessage();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message("Mật khẩu không khớp")
                    .build());
        }
        User user = userService.createUser(userDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(user)
                .message("Account registration successful")
                .build());
        }

@PostMapping("/verify")
public ResponseEntity<?> verifyUser(@RequestBody VerifyAccountDTO verifyAccountDTO
        , BindingResult result) {
    if (result.hasErrors()) {
        List<String> errorMessages = result.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return ResponseEntity.badRequest().body(ResponseObject.builder()
                .status(HttpStatus.BAD_REQUEST)
                .data(null)
                .message(errorMessages.toString())
                .build());
    }
    userService.verifyUser(verifyAccountDTO);
    return ResponseEntity.ok("Account verified successfully");
}

    @PostMapping("/login")
    //can we register an "admin" user ?
    public ResponseEntity<ResponseObject> loginUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
    ) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(errorMessages.toString())
                    .build());
        }
        User user = userService.findByEmail(userDTO.getEmail());
        if (user == null || !user.getPassword().equals(userDTO.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.builder()
                    .status(HttpStatus.UNAUTHORIZED)
                    .data(null)
                    .message("Invalid credentials")
                    .build());
        }
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(user)
                .message("Login successful")
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseObject> forgotPassword(@RequestParam String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message("Email not found")
                    .build());
        }
        userService.checkMailUser(email);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("OTP sent to your email")
                .build());
    }
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseObject> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        User user = userService.findByEmail(resetPasswordDTO.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message("Email not found")
                    .build());
        }

        if (!user.getVerificationCode().equals(resetPasswordDTO.getVerificationCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Invalid OTP")
                    .build());
        }

        user.setPassword(resetPasswordDTO.getNewPassword());
        userService.save(user);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(user)
                .message("Password reset successful")
                .build());
    }


}
