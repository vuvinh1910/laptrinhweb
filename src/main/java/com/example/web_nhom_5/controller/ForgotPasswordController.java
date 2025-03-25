package com.example.web_nhom_5.controller;

import com.example.web_nhom_5.dto.EmailBody;
import com.example.web_nhom_5.dto.request.ChangePassword;
import com.example.web_nhom_5.entity.ForgotPassword;
import com.example.web_nhom_5.entity.UserEntity;
import com.example.web_nhom_5.exception.ErrorCode;
import com.example.web_nhom_5.exception.WebException;
import com.example.web_nhom_5.repository.ForgotPasswordRepository;
import com.example.web_nhom_5.repository.UserRepository;
import com.example.web_nhom_5.service.implement.EmailService;
import com.example.web_nhom_5.service.implement.UserService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/public/forgotPassword")
@PermitAll()
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @PostMapping("/verifyEmail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new WebException(ErrorCode.USER_NOT_EXISTED));
        List<ForgotPassword> fp = forgotPasswordRepository.findAllByUserEntity(user);
        forgotPasswordRepository.deleteAll(fp);

        int otp = otpGeneration();

        EmailBody emailBody = EmailBody.builder()
                .to(email)
                .subject("OTP dat lai mat khau")
                .text("OTP cua ban la : " + otp)
                .build();

        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 5*60*1000))
                .userEntity(user)
                .isVerified(false)
                .build();

        emailService.sendEmail(emailBody);
        forgotPasswordRepository.save(forgotPassword);

        return ResponseEntity.ok("Email sent for verification!");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_EXISTED));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUserEntity(otp, user)
                .orElseThrow(() -> new WebException(ErrorCode.INVALID_KEY));

        // Kiểm tra nếu OTP đã hết hạn
        if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(fp.getForgotPasswordId());
            return new ResponseEntity<>("OTP has expired", HttpStatus.EXPECTATION_FAILED);
        }

        fp.setIsVerified(true);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("OTP verified! Proceed to reset your password.");
    }


    @PostMapping("/setNewPassword/{email}")
    public ResponseEntity<String> setNewPassword(@RequestBody ChangePassword changePassword, @PathVariable String email) {
        userService.changePassword(email, changePassword);
        return ResponseEntity.ok("Password changed!");
    }

    private Integer otpGeneration() {
        Random rand = new Random();
        return rand.nextInt(100_000,999_999);
    }
}
