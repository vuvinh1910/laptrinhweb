package com.example.web_nhom_5.controller.api;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {
    // truyen boolean da dang nhap hay chua vao tat ca html
    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        // Lấy trạng thái đăng nhập từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"));

        // Truyền biến vào model cho tất cả các view
        model.addAttribute("loggedIn", isLoggedIn);
        model.addAttribute("notLoggedIn", !isLoggedIn);
    }
}
