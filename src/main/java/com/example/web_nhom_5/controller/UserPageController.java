package com.example.web_nhom_5.controller;

import com.example.web_nhom_5.dto.request.*;
import com.example.web_nhom_5.dto.response.*;
import com.example.web_nhom_5.entity.*;
import com.example.web_nhom_5.enums.BookingStatus;
import com.example.web_nhom_5.exception.WebException;
import com.example.web_nhom_5.service.BookingRoomService;
import com.example.web_nhom_5.service.LocationService;
import com.example.web_nhom_5.service.RoomService;
import com.example.web_nhom_5.service.implement.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
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
    private LocationService locationService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private UserVoucherService userVoucherService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private VoucherService voucherService;

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

    @GetMapping("/cart")
    public String getCart(Model model) {
        UserResponse user = userService.getMyInfo();
        List<BookingRoomResponse> bookingRooms = bookingRoomService.getAllBookingRoomsByUserAndStatusAndPaid(BookingStatus.PENDING, false);
        model.addAttribute("bookedRooms", bookingRooms);
        model.addAttribute("user", user);
        return "user/cart";
    }

    @DeleteMapping(value = "/cart/{id}")
    public String deleteCart(@PathVariable long id, RedirectAttributes model) {
        try {
            bookingRoomService.deleteBookingRoomById(id);
            model.addFlashAttribute("result", "đã xóa");
        } catch (WebException e) {
            model.addFlashAttribute("result", e.getMessage());
        }
        return "redirect:/users/cart";
    }

    // doi mat khau
    @GetMapping(value = {"/myInfo/change-pass", "/myInfo/change-pass/"})
    public String changePass(Model model) {
        model.addAttribute("user", new UpdatePasswordRequest()); // Khởi tạo đối tượng user
        List<LocationResponse> locations = locationService.getAllLocation();
        model.addAttribute("locations", locations);
        return "user/change-pass";
    }

    // api doi mat khau
    @PutMapping(value = {"/myInfo/change-pass", "/myInfo/change-pass"})
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

    // api dat phong
    @PostMapping(value = {"/booking-rooms/{roomId}", "/booking-rooms/{roomId}"})
    public String createBookingRoom(@ModelAttribute("booking") BookingRoomCreateRequest request, RedirectAttributes model, @PathVariable long roomId) {
        try {
            long id = bookingRoomService.addBookingRoom(request).getId();
            model.addFlashAttribute("bookingId", id);
            model.addFlashAttribute("result", "Đặt phòng thành công.\n Chuyển đến trang thanh toán ?");
        } catch (WebException e) {
            model.addFlashAttribute("bookingId", "no");
            model.addFlashAttribute("result", e.getMessage());
        }
        return "redirect:/public/room/{roomId}";
    }

    @PostMapping("/voucher/{id}")
    public String claimVoucher(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userVoucherService.addUserVoucher(id);
            redirectAttributes.addFlashAttribute("result", "Voucher claimed");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("result", e.getMessage());
        }
        return "redirect:/public/voucher";
    }

    // list dat phong
    @GetMapping("/myInfo/booking-rooms")
    public String getAllBookingRooms(Model model) {
        List<LocationResponse> locations = locationService.getAllLocation();
        model.addAttribute("locations", locations);
        List<BookingRoomResponse> bookedRooms = new ArrayList<>(
                bookingRoomService.getAllBookingRoomsByUserAndStatusAndPaid(BookingStatus.CONFIRMED, null)
        );
        bookedRooms.addAll(bookingRoomService.getAllBookingRoomsByUserAndStatusAndPaid(BookingStatus.COMPLETED, true));
        model.addAttribute("bookedRooms", bookedRooms);
        return "user/listbookingrooms";
    }

    // xoa lich su dat phong
    @DeleteMapping(value = "/myInfo/booking-rooms/{roomId}")
    public String deleteBookingRoom(@PathVariable long roomId, RedirectAttributes model) {
        try {
            bookingRoomService.deleteBookingRoomById(roomId);
            model.addFlashAttribute("result", "đã xóa");
        } catch (WebException e) {
            model.addFlashAttribute("result", e.getMessage());
        }
        return "redirect:/users/myInfo/booking-rooms";
    }

    @GetMapping(value = "/booking-rooms/{bookingId}/detail")
    public String getBookingRoomDetail(@PathVariable long bookingId, Model model) {
        UserResponse user = userService.getMyInfo();
        BookingRoomEntity booking = bookingRoomService.getBookingRoomById(bookingId);
        RoomEntity room = roomService.getRoomById(booking.getRoom().getId());
        HotelEntity hotel = room.getHotel();
        model.addAttribute("booking", booking);
        model.addAttribute("room", room);
        model.addAttribute("hotel", hotel);
        model.addAttribute("user", user);
        return "user/bookingdetail";
    }

    // trang thanh toan phong
    @GetMapping(value = "/booking-rooms/{bookingId}/payment")
    public String getPaymentForm(Model model,
                                 @PathVariable long bookingId,
                                 @RequestParam(value = "voucherCode", required = false) String voucherCode,
                                 @RequestParam(value = "price", required = false) Long price) {
        BookingRoomEntity booking = bookingRoomService.getBookingRoomById(bookingId);
        if (price == null) {
            price = booking.getTotalPrice(); // nếu chưa giảm
        }
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("price", price);
        model.addAttribute("voucherCode", voucherCode);

        return "/user/pay-room";
    }


    // api apply voucher button
    @PostMapping("/booking-rooms/{bookingId}/applyVoucher")
    public String applyVoucher(@PathVariable long bookingId,
                               @RequestParam(value = "voucherCode", required = false) String voucherCode,
                               RedirectAttributes redirectAttributes) {
        BookingRoomEntity booking = bookingRoomService.getBookingRoomById(bookingId);
        long price = booking.getTotalPrice();
        int sale = userVoucherService.checkAndGetValueValidVoucherUser(voucherCode);

        if (sale == 0) {
            redirectAttributes.addFlashAttribute("error", "Voucher không hợp lệ");
            return "redirect:/users/booking-rooms/" + bookingId + "/payment";
        }

        price = price - price * sale / 100;
        // Redirect có kèm mã và giá
        redirectAttributes.addFlashAttribute("success","voucher applied");
        return "redirect:/users/booking-rooms/" + bookingId + "/payment?voucherCode=" + voucherCode + "&price=" + price;
    }


    // api thanh toan phong
    @PostMapping(value = "/booking-rooms/{bookingId}/payment")
    public String doRoomProcessPayment(@PathVariable long bookingId,
                                       @RequestParam(value = "voucherCode", required = false) String voucherCode,
                                       RedirectAttributes model) {
        try {
            bookingRoomService.processPayment(bookingId, voucherCode);
            String result = "Thanh toán thành công.";
            model.addFlashAttribute("result", result);
        } catch (WebException e) {
            model.addFlashAttribute("result", e.getMessage());
        }
        return "redirect:/users/booking-rooms/" + bookingId +"/detail"; // Đảm bảo rằng đường dẫn này đúng
    }

    // api cancel phong
    @PostMapping("/booking-rooms/{id}/cancel")
    public String createRefund(@ModelAttribute("refund") @Valid RefundEntity refundEntity,
                               @PathVariable long id,
                               RedirectAttributes redirectAttributes) {
        try {
            refundService.createRefund(refundEntity, id);
            redirectAttributes.addFlashAttribute("result", "Đã Gửi Yêu Cầu Đến Quản Trị Viên, Vui lòng chờ thông báo từ Email");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("result", e.getMessage());
        }
        return "redirect:/users/myInfo/booking-rooms";
    }

    @GetMapping("/myVoucher")
    public String getMyVoucher(Model model) {
        List<UserVoucherEntity> voucherEntities = userVoucherService.getAllUserVoucherValid();
        model.addAttribute("vouchers", voucherEntities);
        return "user/my-voucher";
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
                return "public/login";
            }
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Logout failed. Please try again.");
            return "public/login";
        }
    }

}
