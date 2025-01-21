package com.ecommerce.springbootauthotp.service;

import com.ecommerce.springbootauthotp.dtos.request.UserDTO;
import com.ecommerce.springbootauthotp.dtos.request.VerifyAccountDTO;
import com.ecommerce.springbootauthotp.entity.User;
import com.ecommerce.springbootauthotp.mapper.IUserMapper;
import com.ecommerce.springbootauthotp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private IUserMapper userMapper;

    public User createUser(UserDTO userDTO){
        User newUser = userMapper.toUser(userDTO);
        newUser.setVerificationCode(generateVerificationCode());
        newUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        newUser.setEnabled(false);

        sendVerificationEmail(newUser);
        return userRepository.save(newUser);
    }

    public User verifyUser(VerifyAccountDTO input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                return userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void sendVerificationEmail(User user) { //TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
    }
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }
    public User checkMailUser(String email){
        Optional<User> user = userRepository.findByEmail(email);
        User u = user.get();
        u.setVerificationCode(generateVerificationCode());
        u.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        sendVerificationEmail(u);
        return userRepository.save(u);
    }
    public User save(User user){
        return userRepository.save(user);
    }


}
