package com.ecommerce.springbootauthotp.entity;

import com.ecommerce.springbootauthotp.Enum.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Users")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(unique = true)
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String avatar;
    private boolean enabled;
    private String fullname;
    private String address;
    private String phone;
    private String gender;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "verification_code")
    private String verificationCode;
    @Column(name = "verification_expiration")
    private LocalDateTime verificationCodeExpiresAt;

    @CreationTimestamp // Tự động gán thời gian tạo bản ghi.
    private LocalDateTime createdAt;

    @UpdateTimestamp // Tự động cập nhật thời gian khi bản ghi bị sửa đổi.
    private LocalDateTime updatedAt;

    // Hàm callback, tự động được gọi trước khi lưu một bản ghi mới vào cơ sở dữ liệu.
    @PrePersist
    protected void onCreate() {
        if (role == null) {
            role = Role.CUSTOMER;
        }
    }
}
