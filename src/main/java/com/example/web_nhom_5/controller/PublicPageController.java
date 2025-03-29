package com.example.web_nhom_5.controller;

import com.example.web_nhom_5.dto.request.AuthenticationRequest;
import com.example.web_nhom_5.dto.request.UserCreationRequest;
import com.example.web_nhom_5.dto.response.LocationResponse;
import com.example.web_nhom_5.dto.response.RoomResponse;
import com.example.web_nhom_5.dto.response.ServiceResponse;
import com.example.web_nhom_5.dto.response.UserResponse;
import com.example.web_nhom_5.entity.RoomEntity;
import com.example.web_nhom_5.entity.ServiceEntity;
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
    private ServiceService serviceService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingRoomService bookingRoomService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping({"/home", "/home/"})
    public String getHome(Model model) {
        return "/public/home";
    }

    @GetMapping({"/rooms/", "/rooms"})
    public String roomFilter(
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate checkOut,
            @RequestParam(required = false, defaultValue = "1") Integer numOfPeople,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false, defaultValue = "") String roomType,
            Model model) {

        List<RoomResponse> rooms = roomService.filterRooms(location, checkIn, checkOut, numOfPeople, maxPrice, roomType);
        model.addAttribute("rooms", rooms);
        return "/public/rooms";
    }

    @GetMapping({"/services","/services/"})
    public String serviceFilter(@RequestParam(required = false) Long minPrice,
                                @RequestParam(required = false) Long maxPrice,
                                @RequestParam(required = false, defaultValue = "") String serviceType,
                                Model model) {
        List<ServiceResponse> services = serviceService.filterService(minPrice, maxPrice,serviceType);
        model.addAttribute("services", services);
        return "/public/services";
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

    @GetMapping({"/login", "/login/"})
    public String getLogIn(Model model) {
        model.addAttribute("authen", new AuthenticationRequest());
        return "login";
    }

    @GetMapping({"/signup", "/signup/"})
    public String getSignUp(Model model) {
        model.addAttribute("user", new UserCreationRequest());
        return "signup";
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
            RedirectAttributes redirectAttributes) {

        try {
            var authResponse = authenticationService.authenticate(authenticationRequest);

            if (authResponse != null && authResponse.getToken() != null) {
                // Lưu token vào cookie
                Cookie authCookie = new Cookie("Authorization", authResponse.getToken().trim());
                authCookie.setPath("/");
                authCookie.setHttpOnly(true);
                authCookie.setSecure(false);
                authCookie.setMaxAge(3600);
                response.addCookie(authCookie);

                // Kiểm tra quyền admin
                if (authenticationService.isAdminToken(authResponse.getToken())) {
                    return "redirect:/admin/dashboard";
                } else {
                    return "redirect:/public/home";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Authentication failed. Please try again.");
                return "redirect:/public/login";  // Chuyển hướng để tránh lỗi lặp lại
            }
        } catch (WebException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/public/login";  // Chuyển hướng để lỗi không bị lặp lại
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