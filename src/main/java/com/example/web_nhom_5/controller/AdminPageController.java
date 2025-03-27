package com.example.web_nhom_5.controller;

import com.example.web_nhom_5.dto.request.*;
import com.example.web_nhom_5.dto.response.*;
import com.example.web_nhom_5.entity.*;
import com.example.web_nhom_5.enums.BookingStatus;
import com.example.web_nhom_5.exception.WebException;
import com.example.web_nhom_5.repository.BookingRoomRepository;
import com.example.web_nhom_5.repository.UserRepository;
import com.example.web_nhom_5.service.*;
import com.example.web_nhom_5.service.implement.AuthenticationService;
import com.example.web_nhom_5.service.implement.UserService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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
    private ServiceService serviceService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingRoomService bookingRoomService;

    @Autowired
    private BookingServiceService bookingServiceService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/dashboard")
    public String showDashBoard(Model model,@CookieValue(value = "Authorization", required = false) String authToken){

        System.out.println("Auth Token: " + authToken);
        if (authToken == null) {
            return "redirect:/login"; // Chuyển hướng tới trang đăng nhập nếu token không có
        }

        if (!authenticationService.isAdminToken(authToken)) {
            return "redirect:/public/home"; // Nếu không phải admin, chuyển hướng tới trang home
        }

        long soLuongDangChoTraPhong = bookingRoomService.countPendingComplete();
        long numUser = userService.countUser()-1;
        long soDonDangCho = bookingRoomService.countPending();
        long tongThuNhap = bookingRoomService.sumTotalPrice()+bookingServiceService.sumTotalPrice();
        model.addAttribute("soDonDangCho", soDonDangCho);
        model.addAttribute("tongThuNhap", tongThuNhap);
        model.addAttribute("numUser", numUser);
        model.addAttribute("soDangChoTraPhong",soLuongDangChoTraPhong);
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("content", "admin/test/dashboard"); // Tên file fragment
        return "admin/test/layout";
    }

    @GetMapping("room")
    public String showRoom(@RequestParam(value = "locationCode", required = false) String locationCode, Model model) {
        List<RoomResponse> rooms;
        if (locationCode == null || locationCode.isEmpty()) {
            rooms = roomService.getAllRooms();
        } else {
            rooms = roomService.getAllRoomsByLocationCode(locationCode);
        }
        model.addAttribute("rooms", rooms);
        model.addAttribute("locations", locationService.getAllLocation());
        model.addAttribute("selectedLocationCode", locationCode); // Giữ giá trị đã chọn
        model.addAttribute("activePage", "room");
        model.addAttribute("pageTitle", "Service");
        model.addAttribute("content", "admin/test/room"); // Tên file fragment
        return "admin/test/layout";
    }

    // Hiển thị form thêm mới phòng
    @GetMapping("/room/add")
    public String showAddRoomForm(Model model) {
        List<LocationResponse> locations = locationService.getAllLocation(); // Lấy danh sách các địa điểm
        model.addAttribute("locations", locations);
        model.addAttribute("room", new RoomCreateRequest());
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
        List<LocationResponse> locations = locationService.getAllLocation();

        model.addAttribute("room", room);
        model.addAttribute("locations", locations);

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
            return "admin/udt-room"; // Hiển thị lại form cập nhật nếu có lỗi
        }
    }

    @GetMapping("/service")
    public String showService(Model model){
        List<ServiceResponse> services = serviceService.getAllServices();
        model.addAttribute("services", services);

        model.addAttribute("activePage", "service");
        model.addAttribute("pageTitle", "Service");
        model.addAttribute("content", "admin/test/service"); // Tên file fragment
        return "admin/test/layout";
    }

    // Hiển thị form thêm service
    @GetMapping("/service/add")
    public String showAddServiceForm(Model model) {
        model.addAttribute("service", new ServiceCreateRequest());
        return "admin/add-service"; // Tên file HTML dùng để hiển thị form
    }

    @PostMapping("/service/add")
    public String addService(
            @ModelAttribute("service") @Valid ServiceCreateRequest serviceDTO, RedirectAttributes redirectAttributes,
            Model model) {

        try {
            serviceService.addService(serviceDTO);
            redirectAttributes.addFlashAttribute("success", "Service added successfully!");
        } catch (WebException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/service";
    }

    @GetMapping("/service/delete/{codeName}")
    public String deleteService(@PathVariable String codeName, RedirectAttributes redirectAttributes) {
        try {
            serviceService.deleteService(codeName);
            redirectAttributes.addFlashAttribute("success", "Service deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the service.");
        }
        return "redirect:/admin/service";
    }

    @GetMapping("/service/update/{codeName}")
    public String showUpdateServiceForm(@PathVariable String codeName, Model model) {

        ServiceEntity service = serviceService.getServiceById(codeName);

        model.addAttribute("service", service);

        return "admin/udt-service";
    }

    @PostMapping("/service/update/{codeName}")
    public String updateService(@PathVariable String codeName,
                                @ModelAttribute("service") ServiceUpdateRequest serviceUpdateRequest, RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            ServiceResponse updatedService = serviceService.updateService(serviceUpdateRequest, codeName);
            redirectAttributes.addFlashAttribute("success", "Service updated successfully!");
            return "redirect:/admin/service";
        } catch (WebException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "admin/udt-service";
        }
    }

    @GetMapping("/customer")
    public String showUser(Model model){
        List<UserResponse> customers = userService.getAllUser();
        model.addAttribute("customers", customers);
        model.addAttribute("activePage", "customer");
        model.addAttribute("pageTitle", "Customer");
        model.addAttribute("content", "admin/test/customer"); // Tên file fragment
        return "admin/test/layout";
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
        model.addAttribute("content", "admin/test/bookingroom"); // Tên file fragment
        return "admin/test/layout";
    }

    @GetMapping("/booking/service")
    public String showBService(@RequestParam(value = "status", required = false) BookingStatus status,
                               @RequestParam(value = "isPaid", required = false) Boolean isPaid,
                               Model model){
        List<BookingServiceResponse> bServices;
        bServices = bookingServiceService.filterBookingServices(status, isPaid);

        // Truyền danh sách trạng thái xuống view
        model.addAttribute("status", BookingStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("isPaid", isPaid);
        model.addAttribute("bookings", bServices);
        model.addAttribute("activePage", "bookingService");
        model.addAttribute("pageTitle", "Booking Service");
        model.addAttribute("content", "admin/test/bookingservice"); // Tên file fragment
        return "admin/test/layout";
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

    @GetMapping("/booking/service/delete/{id}")
    public String deleteBookingService(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingServiceService.deleteBookingServiceById(id);  // Xóa phòng từ database
            redirectAttributes.addFlashAttribute("success", "Booking Service deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the booking service.");
        }
        return "redirect:/admin/booking/service";  // Chuyển hướng về trang danh sách phòng
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

    @GetMapping("/booking/service/update/{bookingServiceId}")
    public String updateBookingServiceForm(Model model, @PathVariable Long bookingServiceId) {
        BookingServiceEntity bService = bookingServiceService.getBookingServiceById(bookingServiceId);

        model.addAttribute("bService", bService);
        model.addAttribute("statuses", BookingStatus.values());
        return "admin/udt-bservice";
    }

    @PostMapping("/booking/service/update/{bookingServiceId}")
    public String updateBookingStatusById(
            @PathVariable long bookingServiceId,
            @ModelAttribute("bookingService") BookingServiceUpdateRequest bookingServiceUpdateRequest,
            RedirectAttributes redirectAttributes) {
        try {
            bookingServiceService.updateBookingService(bookingServiceId, bookingServiceUpdateRequest);
            redirectAttributes.addFlashAttribute("success", "Status updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update status: " + e.getMessage());
        }
        return "redirect:/admin/booking/service"; // Chuyển hướng về danh sách phòng
    }
}
