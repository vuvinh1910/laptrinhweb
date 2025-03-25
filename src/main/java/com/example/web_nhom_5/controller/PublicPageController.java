package com.example.web_nhom_5.controller;

import com.example.web_nhom_5.dto.request.AuthenticationRequest;
import com.example.web_nhom_5.dto.api.ApiResponse;
import com.example.web_nhom_5.dto.request.UserCreationRequest;
import com.example.web_nhom_5.dto.response.LocationResponse;
import com.example.web_nhom_5.dto.response.RoomResponse;
import com.example.web_nhom_5.dto.response.ServiceResponse;
import com.example.web_nhom_5.dto.response.UserResponse;
import com.example.web_nhom_5.entity.RoomEntity;
import com.example.web_nhom_5.entity.RoomEntity;
import com.example.web_nhom_5.entity.ServiceEntity;
import com.example.web_nhom_5.entity.UserEntity;
import com.example.web_nhom_5.exception.WebException;
import com.example.web_nhom_5.service.BookingRoomService;
import com.example.web_nhom_5.service.LocationService;
import com.example.web_nhom_5.service.RoomService;
import com.example.web_nhom_5.service.ServiceService;
import com.example.web_nhom_5.service.implement.AuthenticationService;
import com.example.web_nhom_5.service.implement.SearchRoomService;
import com.example.web_nhom_5.service.implement.UserService;
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


import java.time.LocalDate;
import java.util.HashSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/public")
@Slf4j
public class PublicPageController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingRoomService bookingRoomService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping({"/home","/","/home/"})
    public String getHome(Model model) {
        List<RoomResponse> rooms = roomService.getLimitedRooms(); // Lấy 4 phòng
        List<LocationResponse> locations = locationService.getAllLocation();
        List<ServiceResponse> services = serviceService.getAllServices();
        model.addAttribute("rooms", rooms);
        model.addAttribute("services", services);
        model.addAttribute("locations", locations);
        return "home";
    }


    @GetMapping({"/all-rooms","/","/all-rooms/"})
    public String getRooms(Model model) {

        List<RoomResponse> rooms = roomService.getAllRooms();
        List<LocationResponse> locations = locationService.getAllLocation();
        model.addAttribute("rooms", rooms);
        model.addAttribute("locations", locations);
        return "rooms";
    }

    @GetMapping()
    public String getLocation(Model model) {
        List<LocationResponse> locations = locationService.getAllLocation();
        model.addAttribute("locations", locations);
        return "header";
    }

    @GetMapping("/hotel/{locationCode}")
    public String getRoomsByLocation(@PathVariable("locationCode") String locationCode, Model model) {

        List<RoomResponse> rooms = roomService.getAllRoomsByLocationCode(locationCode);
        model.addAttribute("rooms", rooms);

        List<LocationResponse> locations = locationService.getAllLocation();
        model.addAttribute("locations", locations);

        return "rooms";
    }

    @GetMapping({"/login","/","/login/"})
    public String getLogIn(Model model) {
        model.addAttribute("authen", new AuthenticationRequest());
        return "login";
    }

    @GetMapping({"/signup", "/", "/signup/"})
    public String getSignUp(Model model) {
        model.addAttribute("user", new UserCreationRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@Valid @ModelAttribute("user") UserCreationRequest request, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if (bindingResult.hasErrors()) {
            // Nếu có lỗi validation, trả về trang signup với thông báo lỗi
            return "signup";
        }

        try {
            UserResponse userResponse = userService.creatUser(request);
            redirectAttributes.addFlashAttribute("success", "Account created successfully.");
            return "redirect:/public/login";
        } catch (WebException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        } catch (Exception ex) {
            model.addAttribute("error", "Unexpected error occurred: " + ex.getMessage());
            return "signup";
        }
    }

    @GetMapping("/search-form")
    public String showSearchForm(Model model) {
        // Truyền thêm bất kỳ dữ liệu nào cần thiết vào model nếu cần
        return "search-form"; // Tên file HTML giao diện tìm kiếm (search-form.html)
    }

    @Autowired
    private SearchRoomService searchRoomService;

    @GetMapping("/room/search")
    public String searchRooms(
            @RequestParam("locationName") String locationName,
            @RequestParam(value = "minPrice", required = false) Long minPrice,
            @RequestParam(value = "maxPrice", required = false) Long maxPrice,
            @RequestParam("checkIn") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam("checkOut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            Model model) {

        if (minPrice == null) {
            minPrice = 0L; // Hoặc giá trị mặc định khác, chẳng hạn 0 nếu không có giá trị nhập
        }
        if (maxPrice == null) {
            maxPrice = Long.MAX_VALUE; // Hoặc giá trị lớn nhất nếu không có giá trị nhập
        }

        List<LocationResponse> locations = locationService.getAllLocation();

        // Tìm các phòng có sẵn dựa trên các tham số
        List<RoomEntity> availableRooms = searchRoomService.findAvailableRooms(locationName, minPrice, maxPrice, checkIn, checkOut);
        model.addAttribute("rooms", availableRooms);

        model.addAttribute("locations", locations);
        return "rooms"; // Tên của view (Thymeleaf template)
    }


    @PostMapping("/login")
    public String authenticationResponseApiResponse(
            @ModelAttribute("authen") AuthenticationRequest authenticationRequest,
            HttpServletResponse response,
            Model model) {

        try {
            var authResponse = authenticationService.authenticate(authenticationRequest);

            log.info("Generated Token: {}", authResponse.getToken());
            if (authResponse != null && authResponse.getToken() != null) {
                // Tạo cookie lưu trữ token
                try {
                    String encodedToken = authResponse.getToken();
                    log.info("Encoded Token using Base64 URL-safe: {}", encodedToken);

                    Cookie authCookie = new Cookie("Authorization",encodedToken.trim());
                    authCookie.setPath("/");  // Đảm bảo cookie có phạm vi trên toàn bộ website
                    authCookie.setHttpOnly(true);  // Đảm bảo không thể truy cập qua JavaScript
                    authCookie.setSecure(false);  // Chỉ cần false khi chạy trên HTTP
                    authCookie.setMaxAge(3600);  // Thời gian sống của cookie là 3600 giây = 1 giờ
                    response.addCookie(authCookie);
                    log.info("Token stored in cookie: Bearer {}", encodedToken);

                } catch (Exception e) {
                    log.error("Error storing token in cookie: {}", e.getMessage());
                }

                // Kiểm tra quyền admin từ token
                if (authenticationService.isAdminToken(authResponse.getToken())) {
                    return "redirect:/admin/room";  // Chuyển hướng tới trang admin
                } else {
                    return "redirect:/public/home";  // Chuyển hướng tới trang home
                }
            } else {
                model.addAttribute("errorMessage", "Authentication failed. Please try again.");
                return "login";
            }
        } catch (WebException e) {
            log.error("Authentication error: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Invalid username or password");
            return "login";
        }
    }


    @GetMapping(value = "/room/{id}")
    public String getRoomById(@PathVariable("id") long id, Model model) {
        List<LocationResponse> locations = locationService.getAllLocation();
        model.addAttribute("locations", locations);
        RoomEntity rooms = roomService.getRoomById(id);
        model.addAttribute("room", rooms);
        return "viewroom";
    }

    @GetMapping(value = "/service/{id}")
    public String getServiceById(@PathVariable("id") String id, Model model) {
        List<LocationResponse> locations = locationService.getAllLocation();
        model.addAttribute("locations", locations);
        ServiceEntity service = serviceService.getServiceById(id);
        model.addAttribute("service", service);
        return "viewservice";
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