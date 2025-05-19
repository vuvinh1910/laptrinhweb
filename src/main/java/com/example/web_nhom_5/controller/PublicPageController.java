package com.example.web_nhom_5.controller;

import com.example.web_nhom_5.dto.request.AuthenticationRequest;
import com.example.web_nhom_5.dto.request.UserCreationRequest;
import com.example.web_nhom_5.dto.response.LocationResponse;
import com.example.web_nhom_5.dto.response.UserResponse;
import com.example.web_nhom_5.entity.HotelEntity;
import com.example.web_nhom_5.entity.RoomEntity;
import com.example.web_nhom_5.entity.VoucherEntity;
import com.example.web_nhom_5.exception.WebException;
import com.example.web_nhom_5.repository.HotelRepository;
import com.example.web_nhom_5.service.BookingRoomService;
import com.example.web_nhom_5.service.HotelService;
import com.example.web_nhom_5.service.LocationService;
import com.example.web_nhom_5.service.RoomService;
import com.example.web_nhom_5.service.implement.AuthenticationService;
import com.example.web_nhom_5.service.implement.UserService;
import com.example.web_nhom_5.service.implement.UserVoucherService;
import com.example.web_nhom_5.service.implement.VoucherService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;


import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/public")
@Slf4j
public class PublicPageController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingRoomService bookingRoomService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private UserVoucherService userVoucherService;
    @Autowired
    private HotelRepository hotelRepository;

    @GetMapping({"/home", "/home/"})
    public String getHome(Model model) {
        return "/public/home";
    }

    @GetMapping("/voucher")
    public String getVoucher(Model model) {
        List<VoucherEntity> voucher = voucherService.getAllVoucher();
        model.addAttribute("voucher", voucher);
        return "/public/voucher";
    }

    @GetMapping("/hotels")
    public String hotelFilter(
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false, defaultValue = "0") Integer star,
            @RequestParam(required = false, defaultValue = "0.0") Double rate,
            @RequestParam(required = false) Long price,
            Model model) {
        List<HotelEntity> hotels = hotelService.hotelFilter(location, star, rate, price);
        hotelService.updateMinPrice();
        model.addAttribute("hotels", hotels);
        return "/public/hotels";
    }

    @GetMapping("/hotel/{id}")
    public String hotelDetail(@PathVariable Long id, Model model) {
        HotelEntity hotel = hotelService.getHotelById(id);
        model.addAttribute("hotel", hotel);
        return "/public/detail_hotel";
    }


    @GetMapping({"/login", "/login/"})
    public String getLogIn(@RequestParam(value = "next", required = false) String next, Model model) {
        model.addAttribute("authen", new AuthenticationRequest());
        // Thêm tham số 'next' vào model để truyền sang view
        model.addAttribute("next", next);
        return "public/login";
    }


    @GetMapping({"/signup", "/signup/"})
    public String getSignUp(Model model) {
        model.addAttribute("user", new UserCreationRequest());
        return "public/signup";
    }

    @PostMapping("/signup")
    public String registerUser(
            @Valid @ModelAttribute("user") UserCreationRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid input. Please check again.");
            return "redirect:/public/signup"; // Chuyển hướng để tránh lỗi bị lặp lại
        }

        try {
            UserResponse userResponse = userService.creatUser(request);
            redirectAttributes.addFlashAttribute("success", "Account created successfully.");
            return "redirect:/public/login";
        } catch (WebException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/public/signup"; // Chuyển hướng để lỗi không bị lặp lại
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Unexpected error occurred: " + ex.getMessage());
            return "redirect:/public/signup"; // Chuyển hướng để lỗi không bị lặp lại
        }
    }

    @PostMapping("/login")
    public String authenticationResponseApiResponse(
            @ModelAttribute("authen") AuthenticationRequest authenticationRequest,
            @RequestParam(value = "next", required = false) String next, // <<< Thêm tham số next vào đây
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        try {
            var authResponse = authenticationService.authenticate(authenticationRequest);

            if (authResponse != null && authResponse.getToken() != null) {
                // Lưu token vào cookie
                Cookie authCookie = new Cookie("Authorization", authResponse.getToken().trim());
                authCookie.setPath("/");
                authCookie.setHttpOnly(true);
                authCookie.setSecure(false); // Cân nhắc đặt true nếu dùng HTTPS
                authCookie.setMaxAge(3600);
                response.addCookie(authCookie);

                // --- Xử lý chuyển hướng dựa trên 'next' ---
                if (next != null && !next.isEmpty()) {
                    // Chuyển hướng đến URL được chỉ định trong tham số 'next'
                    return "redirect:" + next;
                }
                // --- Kết thúc xử lý 'next' ---

                // Nếu không có 'next', thực hiện chuyển hướng mặc định dựa trên quyền
                if (authenticationService.isAdminToken(authResponse.getToken())) {
                    return "redirect:/admin/dashboard";
                } else {
                    return "redirect:/public/home";
                }
            } else {
                // Xác thực thất bại
                redirectAttributes.addFlashAttribute("error", "Authentication failed. Please try again.");
                // Khi chuyển hướng về trang login do lỗi, truyền lại tham số 'next' nếu có
                if (next != null && !next.isEmpty()) {
                    // Mã hóa tham số 'next' khi thêm vào URL chuyển hướng
                    String encodedNext = UriUtils.encodePathSegment(next, StandardCharsets.UTF_8.name());
                    return "redirect:/public/login?next=" + encodedNext;
                }
                return "redirect:/public/login"; // Không có next, chuyển hướng mặc định về login
            }
        } catch (WebException e) { // Bắt ngoại lệ xác thực tùy chỉnh
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            // Khi chuyển hướng về trang login do lỗi, truyền lại tham số 'next' nếu có
            if (next != null && !next.isEmpty()) {
                String encodedNext = UriUtils.encodePathSegment(next, StandardCharsets.UTF_8.name());
                return "redirect:/public/login?next=" + encodedNext;
            }
            return "redirect:/public/login"; // Không có next, chuyển hướng mặc định về login
        } catch (Exception e) { // Bắt các ngoại lệ chung
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred: " + e.getMessage());
            if (next != null && !next.isEmpty()) {
                String encodedNext = UriUtils.encodePathSegment(next, StandardCharsets.UTF_8.name());
                return "redirect:/public/login?next=" + encodedNext;
            }
            return "redirect:/public/login";
        }
    }


    @GetMapping(value = "/room/{id}")
    public String getRoomById(@PathVariable("id") long id, Model model) {
        List<LocationResponse> locations = locationService.getAllLocation();
        model.addAttribute("locations", locations);
        RoomEntity rooms = roomService.getRoomById(id);
        model.addAttribute("room", rooms);
        return "public/viewroom";
    }


    //hien thi trang thai nut dat phong la het phong hay dat phong khi chon ngay.
    @GetMapping("/room/{roomId}/availability")
    @ResponseBody
    public Object checkRoomAvailability(@PathVariable("roomId") Long roomId,
                                        @RequestParam("checkIn") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate checkIn,
                                        @RequestParam("checkOut") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate checkOut) {
        return bookingRoomService.bookingRoomIsAvailable(roomId, checkIn, checkOut);
    }

    @GetMapping(value = "/isLogin")
    @ResponseBody
    public Object isLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            return false;
        }
        return true;
    }

}