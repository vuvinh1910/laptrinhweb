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
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Controller
@RequestMapping("/public/forgotPassword")
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @GetMapping()
    public String forgotPassword() {
        return "/public/forgot-password";
    }

    @GetMapping("/verify-otp")
    public String showVerifyOtpForm(@ModelAttribute("email") String email, Model model) {
        model.addAttribute("email", email);
        return "/public/verify-otp";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(
            @ModelAttribute("email") String email,
            @ModelAttribute("otp") Integer otp,
            Model model) {

        model.addAttribute("email", email);
        model.addAttribute("otp", otp);
        model.addAttribute("changePassword", new ChangePassword());
        return "/public/reset-password";
    }

    @PostMapping("/verifyEmail")
    public String verifyEmail(
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {
        try {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_EXISTED));

            // Xóa các OTP cũ
            List<ForgotPassword> fpList = forgotPasswordRepository.findAllByUserEntity(user);
            forgotPasswordRepository.deleteAll(fpList);

            int otp = otpGeneration();

            // Gửi email
            EmailBody emailBody = EmailBody.builder()
                    .to(email)
                    .subject("OTP Reset Password")
                    .text("Your OTP is: " + otp)
                    .build();

            // Lưu OTP
            ForgotPassword forgotPassword = ForgotPassword.builder()
                    .otp(otp)
                    .expirationTime(new Date(System.currentTimeMillis() + 5*60*1000))
                    .userEntity(user)
                    .isVerified(false)
                    .build();

            emailService.sendEmail(emailBody);
            forgotPasswordRepository.save(forgotPassword);

            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/public/forgotPassword/verify-otp";

        } catch (WebException e) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/public/forgotPassword";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error sending OTP");
            return "redirect:/public/forgotPassword";
        }
    }

    @PostMapping("/verifyOtp")
    public String verifyOtp(
            @RequestParam String email,
            @RequestParam Integer otp,
            RedirectAttributes redirectAttributes) {

        try {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_EXISTED));

            ForgotPassword fp = forgotPasswordRepository.findByOtpAndUserEntity(otp, user)
                    .orElseThrow(() -> new WebException(ErrorCode.INVALID_KEY));

            if (fp.getExpirationTime().before(new Date())) {
                forgotPasswordRepository.deleteById(fp.getForgotPasswordId());
                redirectAttributes.addFlashAttribute("error", "OTP expired");
                return "redirect:/public/forgotPassword/verify-otp";
            }

            fp.setIsVerified(true);
            forgotPasswordRepository.save(fp);

            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("otp", otp);
            return "redirect:/public/forgotPassword/reset-password";

        } catch (WebException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid OTP");
            return "redirect:/public/forgotPassword/verify-otp";
        }
    }


    @PostMapping("/reset-password")
    public String setNewPassword(
            @ModelAttribute @Valid ChangePassword changePassword,
            @RequestParam String email,
            @RequestParam Integer otp,
            RedirectAttributes redirectAttributes) {

        try {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_EXISTED));

            ForgotPassword fp = forgotPasswordRepository.findByOtpAndUserEntity(otp, user)
                    .orElseThrow(() -> new WebException(ErrorCode.INVALID_KEY));

            if (!fp.getIsVerified()) {
                return "redirect:/public/forgotPassword/verify-otp";
            }

            userService.changePassword(email, changePassword);
            forgotPasswordRepository.deleteById(fp.getForgotPasswordId());

            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            return "redirect:/public/login";

        } catch (WebException e) {
            redirectAttributes.addFlashAttribute("error", "Error changing password");
            return "redirect:/public/forgotPassword/reset-password";
        }
    }

    private Integer otpGeneration() {
        Random rand = new Random();
        return rand.nextInt(100_000,999_999);
    }
}
