package com.example.web_nhom_5.controller;

import com.example.web_nhom_5.conventer.HotelMapper;
import com.example.web_nhom_5.dto.request.*;
import com.example.web_nhom_5.dto.response.*;
import com.example.web_nhom_5.entity.*;
import com.example.web_nhom_5.enums.BookingStatus;
import com.example.web_nhom_5.exception.ErrorCode;
import com.example.web_nhom_5.exception.WebException;
import com.example.web_nhom_5.repository.BookingRoomRepository;
import com.example.web_nhom_5.repository.HotelRepository;
import com.example.web_nhom_5.repository.UserRepository;
import com.example.web_nhom_5.service.*;
import com.example.web_nhom_5.service.implement.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminPageController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingRoomService bookingRoomService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private HotelMapper hotelMapper;

    @Autowired
    private RefundService refundService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private BookingRoomRepository bookingRoomRepository;

    @GetMapping("/dashboard")
    public String showDashBoard(Model model,@CookieValue(value = "Authorization", required = false) String authToken){

        System.out.println("Auth Token: " + authToken);
        if (authToken == null) {
            return "redirect:/login"; // Chuyển hướng tới trang đăng nhập nếu token không có
        }

        if (!authenticationService.isAdminToken(authToken)) {
            return "redirect:/public/home"; // Nếu không phải admin, chuyển hướng tới trang home
        }

        long numUser = userService.countUser();
        long soDon = bookingRoomService.getAllBookingRooms().size();

        List<BookingRoomEntity> bookings = bookingRoomRepository.findAll();

        // Tính doanh thu theo tháng
        Map<String, Long> revenuePerMonth = bookings.stream()
                .filter(b -> b.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getCreatedAt().getMonth().toString(),
                        Collectors.summingLong(b -> b.getUserPaid() != null ? b.getUserPaid() : 0L)
                ));


        // Tính số lượng đơn theo trạng thái
        Map<String, Long> statusCount = bookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getStatus().toString(),
                        Collectors.counting()
                ));

        model.addAttribute("revenuePerMonth", revenuePerMonth);
        model.addAttribute("statusCount", statusCount);
        long tongThuNhap = bookingRoomService.sumTotalPrice();
        model.addAttribute("soDon", soDon);
        model.addAttribute("tongThuNhap", tongThuNhap);
        model.addAttribute("numUser", numUser);
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("content", "admin/dashboard"); // Tên file fragment
        return "admin/layout";
    }

    @GetMapping("/thong_bao")
    public String showThongBao(Model model){
        List<RefundEntity> refundEntities = refundService.getAllRefund();
        model.addAttribute("refunds", refundEntities);
        model.addAttribute("activePage", "refund");
        model.addAttribute("pageTitle", "Thông Báo");
        model.addAttribute("content", "admin/refund"); // Tên file
        return "admin/layout";
    }

    @PostMapping("/refund/confirm")
    public String confirmRefund(@RequestParam("bookingId") Long bookingId,
                                RedirectAttributes redirectAttributes) {
        try {
            bookingRoomService.updateRefundBookingById(bookingId);
            redirectAttributes.addFlashAttribute("success", "Completed refund and sending email");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/thong_bao";
    }

    @DeleteMapping("/refund/{id}")
    public String deleteRefund(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            refundService.deleteRefund(id);
            redirectAttributes.addFlashAttribute("success", "Deleted refund");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/thong_bao";
    }

    @GetMapping("/room")
    public String showRoom(@RequestParam(value = "locationCode", required = false) String locationCode,
                           @RequestParam(value = "hotelId", required = false) Long hotelId,
                           Model model) {
        List<RoomResponse> rooms;
        if ((locationCode == null || locationCode.isEmpty()) && hotelId == null) {
            rooms = roomService.getAllRooms();
        } else {
            rooms = roomService.getAllRoomsByLocationCodeAndHotelId(locationCode,hotelId);
        }
        model.addAttribute("rooms", rooms);
        model.addAttribute("locations", locationService.getAllLocation());
        model.addAttribute("selectedLocationCode", locationCode); // Giữ giá trị đã chọn
        model.addAttribute("activePage", "room");
        model.addAttribute("pageTitle", "Service");
        model.addAttribute("selectedHotelId", hotelId);
        model.addAttribute("content", "admin/room"); // Tên file fragment
        return "admin/layout";
    }

    // Hiển thị form thêm mới phòng
    @GetMapping("/room/add")
    public String showAddRoomForm(Model model, @RequestParam(value = "hotelId", required = false) Long hotelId) {
        RoomCreateRequest roomCreateRequest = new RoomCreateRequest();
        if(hotelId != null) {
            roomCreateRequest.setHotelId(hotelId);
        }
        model.addAttribute("room", roomCreateRequest);
        return "admin/add-room";
    }

    // Xử lý thêm mới phòng
    @PostMapping("/room/add")
    public String addRoom(@Valid @ModelAttribute("room") RoomCreateRequest roomRequest, Model model , RedirectAttributes redirectAttributes) {
        try {
            RoomResponse newRoom = roomService.addRoom(roomRequest);
            redirectAttributes.addFlashAttribute("success", "Room added successfully: " + newRoom.getRoomName());
        } catch (WebException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred.");
        }

        // Nếu có lỗi, vẫn hiển thị lại form và các lựa chọn địa điểm
        List<LocationResponse> locations = locationService.getAllLocation();
        model.addAttribute("locations", locations);
        return "redirect:/admin/room";
    }

    @GetMapping("/room/delete/{id}")
    public String deleteRoom(@PathVariable("id") Long roomId, RedirectAttributes redirectAttributes) {
        try {
            roomService.deleteRoom(roomId);  // Xóa phòng từ database
            redirectAttributes.addFlashAttribute("success", "Room deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the room.");
        }
        return "redirect:/admin/room";  // Chuyển hướng về trang danh sách phòng
    }

    @GetMapping("/room/update/{id}")
    public String showUpdateRoomForm(@PathVariable Long id, Model model) {
        RoomEntity room = roomService.getRoomById(id);
        RoomUpdateRequest roomUpdateRequest = new RoomUpdateRequest();
        roomUpdateRequest.setRoomName(room.getRoomName());
        roomUpdateRequest.setRoomDetail(room.getRoomDetail());
        roomUpdateRequest.setHotelId(room.getHotel().getId());
        roomUpdateRequest.setRoomPrice(room.getRoomPrice());
        roomUpdateRequest.setRoomType(room.getRoomType());
        model.addAttribute("room", roomUpdateRequest);
        model.addAttribute("id", id);

        return "admin/udt-room"; // Chuyển hướng tới trang cập nhật
    }

    @PostMapping("/room/update/{id}")
    public String updateRoom(@PathVariable Long id,
                             @ModelAttribute("room") RoomUpdateRequest roomUpdateRequest, RedirectAttributes redirectAttributes,
                             Model model) {
        try {
            RoomResponse updatedRoom = roomService.updateRoom(roomUpdateRequest, id);
            redirectAttributes.addFlashAttribute("success", "Room updated successfully!");
            return "redirect:/admin/room"; // Điều hướng trở về trang danh sách phòng sau khi cập nhật thành công
        } catch (WebException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            List<LocationResponse> locations = locationService.getAllLocation();
            redirectAttributes.addFlashAttribute("locations", locations);
            return "redirect:/admin/room/update/" + id; // Hiển thị lại form cập nhật nếu có lỗi
        }
    }

    @GetMapping("/customer")
    public String showUser(Model model){
        List<UserResponse> customers = userService.getAllUser();
        model.addAttribute("customers", customers);
        model.addAttribute("activePage", "customer");
        model.addAttribute("pageTitle", "Customer");
        model.addAttribute("content", "admin/customer"); // Tên file fragment
        return "admin/layout";
    }

    @GetMapping("/customer/update/{id}")
    public String updateCustomer(@PathVariable Long id,Model model){
        UserEntity user = userRepository.findById(id).orElseThrow(()-> new WebException(ErrorCode.USER_NOT_EXISTED));
        model.addAttribute("customer", user);
        return "admin/update-customer";
    }

    @PostMapping("/customer/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") UserUpdateRequest userUpdateRequest,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserByAdmin(id, userUpdateRequest);
            redirectAttributes.addFlashAttribute("success", "User updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred.");
        }
        return "redirect:/admin/customer";
    }
    @GetMapping("/customer/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Account deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the account.");
        }
        return "redirect:/admin/customer";
    }

    @GetMapping("/booking/room")
    public String showBRoom(@RequestParam(value = "status", required = false) BookingStatus status,
                            @RequestParam(value = "isPaid", required = false) Boolean isPaid,
                            Model model){
        List<BookingRoomEntity> bookingRooms = bookingRoomService.filterBookingRooms(status, isPaid);

        // Truyền danh sách trạng thái xuống view
        model.addAttribute("status", BookingStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("isPaid", isPaid);
        model.addAttribute("bookings", bookingRooms);
        model.addAttribute("activePage", "bookingRoom");
        model.addAttribute("pageTitle", "Booking Room");
        model.addAttribute("content", "admin/bookingroom"); // Tên file fragment
        return "admin/layout";
    }

    @GetMapping("/booking/room/delete/{id}")
    public String deleteBookingRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingRoomService.deleteBookingRoomById(id);  // Xóa phòng từ database
            redirectAttributes.addFlashAttribute("success", "Booking Room deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the booking room.");
        }
        return "redirect:/admin/booking/room";  // Chuyển hướng về trang danh sách phòng
    }

    @GetMapping("/booking/room/update/{bookingRoomId}")
    public String updateBookingRoomForm(Model model, @PathVariable Long bookingRoomId) {
        BookingRoomEntity bRoom = bookingRoomService.getBookingRoomById(bookingRoomId);

        model.addAttribute("bookings", bRoom);
        model.addAttribute("statuses", BookingStatus.values());
        return "admin/udt-broom";
    }

    @PostMapping("/booking/room/update/{bookingRoomId}")
    public String updateBookingRoom(
            @PathVariable long bookingRoomId,
            @ModelAttribute("bookingRoom") @Valid BookingRoomUpdateRequest bookingRoomUpdateRequest,
            RedirectAttributes redirectAttributes) {
        try {
            bookingRoomService.updateBookingRoom(bookingRoomId, bookingRoomUpdateRequest);
            redirectAttributes.addFlashAttribute("success", "Status updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update status: " + e.getMessage());
        }
        return "redirect:/admin/booking/room"; // Chuyển hướng về danh sách phòng
    }

    @GetMapping(value = "/booking-rooms/{bookingId}/detail")
    public String getBookingRoomDetail(@PathVariable long bookingId, Model model,@RequestParam(value = "userId") Long userId) {
        UserResponse user = userService.getUser(userId);
        BookingRoomEntity booking = bookingRoomService.getBookingRoomById(bookingId);
        RoomEntity room = roomService.getRoomById(booking.getRoom().getId());
        HotelEntity hotel = room.getHotel();
        model.addAttribute("booking", booking);
        model.addAttribute("room", room);
        model.addAttribute("hotel", hotel);
        model.addAttribute("user", user);
        return "admin/bookingdetail";
    }

    @GetMapping("/hotel")
    public String showHotel(@RequestParam(value = "locationCode", required = false) String locationCode, Model model){
        List<HotelResponse> hotels = new ArrayList<>();
        if (locationCode == null || locationCode.isEmpty()) {
            hotels = hotelService.getAllHotels().stream().map(hotelMapper::hotelToResponse).toList();
        } else {
            hotels = hotelService.hotelFilter(locationCode,null,null,null).stream().map(hotelMapper::hotelToResponse).toList();
        }
        List<LocationResponse> locations = locationService.getAllLocation();
        model.addAttribute("hotels", hotels);
        model.addAttribute("locations", locations);
        model.addAttribute("selectedLocationCode", locationCode); // Giữ giá trị đã chọn
        model.addAttribute("activePage", "hotel");
        model.addAttribute("pageTitle", "Hotel");
        model.addAttribute("content", "admin/hotel"); // Tên file
        return "admin/layout";
    }

    @GetMapping("/hotel/update/{id}")
    public String updateHotelForm(@PathVariable long id,Model model){
        HotelResponse hotelResponse = hotelMapper.hotelToResponse(hotelService.getHotelById(id));
        model.addAttribute("hotel", hotelResponse);
        return "admin/udt-hotel";
    }

    @PostMapping("hotel/update/{id}")
    public String updateHotel(@PathVariable long id,
                              @ModelAttribute("hotel") @Valid HotelUpdateRequest hotelUpdateRequest,
                              RedirectAttributes redirectAttributes) {
        try {
            hotelService.updateHotel(hotelUpdateRequest, id);
            redirectAttributes.addFlashAttribute("success", "Hotel updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while updating the hotel.");
            return "redirect:/admin/hotel/update/" + id;
        }
        return "redirect:/admin/hotel";
    }

    @GetMapping("/hotel/delete/{id}")
    public String Hotel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            hotelService.deleteHotel(id);
            redirectAttributes.addFlashAttribute("success", "Hotel deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the hotel.");
        }
        return "redirect:/admin/hotel";  // Chuyển hướng về trang danh sách phòng
    }

    @GetMapping("/hotel/add")
    public String showAddHotelForm(Model model){
        HotelCreateRequest hotel = new HotelCreateRequest();
        model.addAttribute("hotel", hotel);
        return "admin/add-hotel";
    }

    @PostMapping("/hotel/add")
    public String addHotel(@ModelAttribute("hotel") @Valid HotelCreateRequest hotelCreateRequest,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        try {
            hotelService.addHotel(hotelCreateRequest);
            redirectAttributes.addFlashAttribute("success", "Hotel created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while adding the hotel.");
            return "redirect:/admin/hotel/add";
        }
        return "redirect:/admin/hotel";
    }

    @GetMapping("/voucher")
    public String showVoucher(Model model){
        List<VoucherEntity> voucherEntities = voucherService.getAllVoucher();
        model.addAttribute("vouchers", voucherEntities);
        model.addAttribute("activePage", "voucher");
        model.addAttribute("pageTitle", "Voucher");
        model.addAttribute("content", "admin/voucher"); // Tên file
        return "admin/layout";
    }

    @GetMapping("/voucher/delete/{id}")
    public String deleteVoucher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            voucherService.deleteVoucher(id);
            redirectAttributes.addFlashAttribute("success", "Voucher deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the voucher.");
        }
        return "redirect:/admin/voucher";
    }

    @GetMapping("/voucher/add")
    public String showAddVoucherForm(Model model){
        VoucherEntity voucher = new VoucherEntity();
        model.addAttribute("voucher", voucher);
        return "admin/add-voucher";
    }

    @PostMapping("/voucher/add")
    public String addVoucher(@ModelAttribute("voucher") @Valid VoucherEntity voucher,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        try {
            voucherService.addVoucher(voucher);
            redirectAttributes.addFlashAttribute("success", "Voucher added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while adding the voucher.");
            return "redirect:/admin/voucher/add";
        }
        return "redirect:/admin/voucher";
    }

    @GetMapping("/voucher/update/{id}")
    public String updateVoucherForm(@PathVariable Long id, Model model){
        VoucherEntity voucher = voucherService.getVoucherById(id);
        model.addAttribute("voucher", voucher);
        return "admin/update-voucher";
    }

    @PostMapping("/voucher/update/{id}")
    public String updateVoucherApi(@PathVariable Long id,
                                   @ModelAttribute("voucher") @Valid VoucherEntity voucher,
                                   RedirectAttributes redirectAttributes) {
        try {
            voucherService.updateVoucher(voucher, id);
            redirectAttributes.addFlashAttribute("success", "Voucher updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while updating the voucher.");
            return "redirect:/admin/voucher/update/" + id;
        }
        return "redirect:/admin/voucher";
    }
}
