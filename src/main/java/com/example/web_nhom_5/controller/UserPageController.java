package com.example.web_nhom_5.controller;

import com.example.web_nhom_5.dto.request.*;
import com.example.web_nhom_5.dto.response.*;
import com.example.web_nhom_5.enums.BookingStatus;
import com.example.web_nhom_5.exception.WebException;
import com.example.web_nhom_5.service.BookingRoomService;
import com.example.web_nhom_5.service.BookingServiceService;
import com.example.web_nhom_5.service.LocationService;
import com.example.web_nhom_5.service.implement.AuthenticationService;
import com.example.web_nhom_5.service.implement.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('USER')")
@Slf4j
public class UserPageController {
    @Autowired
    private UserService userService;
    @Autowired
    private BookingRoomService bookingRoomService;
    @Autowired
    private BookingServiceService bookingServiceService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private AuthenticationService authenticationService;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // info
    @GetMapping("/myInfo")
    public String getMyInfo(Model model) {
        UserResponse user = userService.getMyInfo();
        List<LocationResponse> locations = locationService.getAllLocation();
        model.addAttribute("locations", locations);
        model.addAttribute("user", user); // Thêm thông tin user vào model
        return "user/info";
    }

    // api update info
    @PutMapping("/myInfo")
    public String updateMyInfo(@ModelAttribute("user") UserProfileUpdateRequest request, RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserProfile(request);
            redirectAttributes.addFlashAttribute("result", "Cập nhật thành công!");
        } catch (WebException e) {
            redirectAttributes.addFlashAttribute("result", "Lỗi: " + e.getMessage());
        }
        return "redirect:/users/myInfo"; // Chuyển hướng về /myInfo
    }

    // doi mat khau
    @GetMapping(value = {"/myInfo/change-pass","/myInfo/change-pass/"})
    public String changePass(Model model) {
        model.addAttribute("user", new UpdatePasswordRequest()); // Khởi tạo đối tượng user
        List<LocationResponse> locations = locationService.getAllLocation();
        model.addAttribute("locations", locations);
        return "user/change-pass";
    }

    // api doi mat khau
    @PutMapping(value = {"/myInfo/change-pass","/myInfo/change-pass"})
    public String updateUserPassword(@ModelAttribute("user") UpdatePasswordRequest request, RedirectAttributes model) {
        try {
            String result = userService.updatePassword(request);
            model.addFlashAttribute("result", result);
        } catch (WebException e) {
            model.addFlashAttribute("result", "lỗi: " + e.getMessage());
        }
        return "redirect:/users/myInfo/change-pass";
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // dat phong
    @PostMapping(value = {"/booking-rooms/{roomId}","/booking-rooms/{roomId}"})
    public String createBookingRoom(@ModelAttribute("booking") BookingRoomCreateRequest request, RedirectAttributes model, @PathVariable long roomId) {
        try {
            long id = bookingRoomService.addBookingRoom(request).getId();
            model.addFlashAttribute("bookingId", id);
            model.addFlashAttribute("result","Đặt phòng thành công.\n Chuyển đến trang thanh toán ?");
        } catch (WebException e) {
            model.addFlashAttribute("bookingId","no");
            model.addFlashAttribute("result",e.getMessage());
        }
        return "redirect:/public/room/{roomId}";
    }

    // list dat phong
    @GetMapping("/myInfo/booking-rooms")
    public String getAllBookingRooms(Model model) {
        List<LocationResponse> locations = locationService.getAllLocation();
        model.addAttribute("locations", locations);
        List<BookingRoomResponse> bookedRooms = bookingRoomService.getAllBookingRoomsByUser();
//        List<BookingRoomResponse> bookedRooms = bookingRoomService.getAllBookingRooms();
        model.addAttribute("bookedRooms", bookedRooms);
        return "user/listbookingrooms";
    }

    // xoa dat phong
    @DeleteMapping(value = "/myInfo/booking-rooms/{roomId}")
    public String deleteBookingRoom(@PathVariable long roomId, RedirectAttributes model) {
        try {
            bookingRoomService.deleteBookingRoomById(roomId);
            model.addFlashAttribute("result","đã xóa");
        } catch (WebException e) {
            model.addFlashAttribute("result", e.getMessage());
        }
        return "redirect:/users/myInfo/booking-rooms";
    }

    // trang thanh toan phong
    @GetMapping(value = "/booking-rooms/{bookingId}/payment")
    public String getPaymentForm(Model model, @PathVariable long bookingId) {
        model.addAttribute("bookingId", bookingId);
        return "user/paymentroom";
    }

    // api thanh toan phong
    @PostMapping(value = "/booking-rooms/{bookingId}/payment")
    public String doRoomProcessPayment(@PathVariable long bookingId, @RequestParam("amount") Long amount, RedirectAttributes model) {
        try {
            String result = "Thanh toán thành công.\n Tiền thừa: ";
            result += Long.toString(bookingRoomService.processPayment(bookingId, amount).getCashBack());
            model.addFlashAttribute("result", result);
        } catch (WebException e) {
            model.addFlashAttribute("result", e.getMessage());
        }
        return "redirect:/users/myInfo/booking-rooms"; // Đảm bảo rằng đường dẫn này đúng
    }

    // api cancel phong
    @GetMapping(value = "/booking-rooms/{bookingId}/cancel")
    public String cancelBookingRoom(@PathVariable long bookingId, RedirectAttributes model) {
        try {
            bookingRoomService.updateBookingStatusByBookingRoomId(bookingId, BookingStatus.CANCELLED);
            model.addFlashAttribute("result", "Hủy Thành Công");
        } catch (WebException e) {
            model.addFlashAttribute("result", e.getMessage());
        }
        return "redirect:/users/myInfo/booking-rooms"; // Đảm bảo rằng đường dẫn này đúng
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @PostMapping(value = "/booking-services/{serviceId}")
    public String createBookingService(@ModelAttribute("booking") BookingServiceCreateRequest request, RedirectAttributes model, @PathVariable String serviceId) {
        try {
            long id = bookingServiceService.addBookingService(request).getId();
            model.addFlashAttribute("bookingId", id);
            model.addFlashAttribute("result","Đặt dịch vụ thành công.\n Chuyển đến trang thanh toán ?");
        } catch (WebException e) {
            model.addFlashAttribute("bookingId","no");
            model.addFlashAttribute("result",e.getMessage());
        }
        return "redirect:/public/service/{serviceId}";
    }

    // list dat dich vu
    @GetMapping(value = {"/myInfo/booking-services","/myInfo/booking-services/"})
    public String getAllBookingServicesByUser(Model model) {
        List<LocationResponse> locations = locationService.getAllLocation();
        model.addAttribute("locations", locations);
        List<BookingServiceResponse> list = bookingServiceService.getAllBookingServicesByUser();
        model.addAttribute("bookedServices", list);
        return "user/listbookingservice";
    }

    // xoa dich vu
    @DeleteMapping(value = {"/myInfo/booking-services/{id}","/myInfo/booking-services/{id}"})
    public String deleteBookingService(@PathVariable long id, RedirectAttributes model) {
        try {
            bookingServiceService.deleteBookingServiceById(id);
            model.addFlashAttribute("result","đã xóa");
        } catch (WebException e) {
            model.addFlashAttribute("result", e.getMessage());
        }
        return "redirect:/users/myInfo/booking-services";
    }

    // trang thanh toan dich vu
    @GetMapping(value = "/booking-services/{bookingId}/payment")
    public String getPaymentService(@PathVariable long bookingId, Model model) {
        model.addAttribute("bookingId", bookingId);
        return "user/paymentservice";
    }

    // api thanh toan dich vu
    @PostMapping(value = "/booking-services/{bookingId}/payment")
    public String doPaymentService(@PathVariable long bookingId, @RequestParam("amount") Long amount, RedirectAttributes model) {
        try {
            String result = "Thanh toán thành công.\n Tiền thừa: ";
            result += Long.toString(bookingServiceService.processPayment(bookingId, amount).getCashBack());
            model.addFlashAttribute("result", result);
        } catch (WebException e) {
            model.addFlashAttribute("result", e.getMessage());
        }
        return "redirect:/users/myInfo/booking-services";
    }

    @GetMapping(value = "/booking-services/{bookingId}/cancel")
    public String cancelBookingService(@PathVariable long bookingId, RedirectAttributes model) {
        try {
            bookingServiceService.updateBookingStatusById(bookingId, BookingStatus.CANCELLED);
            model.addFlashAttribute("result", "Hủy Thành Công");
        } catch (WebException e) {
            model.addFlashAttribute("result", e.getMessage());
        }
        return "redirect:/users/myInfo/booking-services";
    }


    @PostMapping("/logout")
    public String logout(
            @CookieValue(value = "Authorization", required = false) String token,
            HttpServletResponse response,
            Model model) {
        try {
            // Nếu cookie token tồn tại, thực hiện logout
            if (token != null) {
                LogoutRequest logoutRequest = new LogoutRequest();
                logoutRequest.setToken(token);

                // Gọi dịch vụ logout
                authenticationService.logout(logoutRequest);

                // Xóa cookie token
                Cookie authCookie = new Cookie("Authorization", null);
                authCookie.setPath("/");
                authCookie.setHttpOnly(true);
                authCookie.setSecure(false); // Đặt true nếu dùng HTTPS
                authCookie.setMaxAge(0); // Xóa cookie
                response.addCookie(authCookie);

                // Chuyển hướng tới trang login
                return "redirect:/public/login";
            } else {
                // Nếu không có token, hiển thị thông báo lỗi
                model.addAttribute("errorMessage", "No active session found.");
                return "login";
            }
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Logout failed. Please try again.");
            return "login";
        }
    }

}
