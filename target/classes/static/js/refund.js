var refundModal = document.getElementById("cancelRequestModal"); // Vẫn dùng ID để lấy trực tiếp
// Đã cập nhật selector để tìm nút đóng với class mới 'refund-close-button'
var closeButton = refundModal.querySelector(".refund-close-button");
var modalBookingIdInput = document.getElementById("modalBookingId");
var cancelRefundForm = document.getElementById("cancelRefundForm");

// Lấy tất cả các nút "Hủy" có class 'open-cancel-modal' (các nút này vẫn nằm trong danh sách booking)
// Class này không nằm trong modal HTML nên giữ nguyên
var openModalButtons = document.querySelectorAll(".open-cancel-modal");

// Khi click vào nút "Hủy" (có class open-cancel-modal)
openModalButtons.forEach(function(button) {
    button.onclick = function() {
        var bookingId = this.getAttribute('data-booking-id'); // Lấy booking ID từ data attribute
        modalBookingIdInput.value = bookingId; // Đặt booking ID vào input hidden trong form
        // Cập nhật action của form với bookingId
        cancelRefundForm.action = "/lap-trinh-web/users/booking-rooms/" + bookingId + "/cancel";
        refundModal.style.display = "flex"; // Hiển thị modal refund
    }
});

// Khi click vào nút đóng (x) bên trong modal refund
closeButton.onclick = function() {
    refundModal.style.display = "none"; // Ẩn modal refund
}

// Khi click vào bất kỳ đâu bên ngoài modal refund, đóng modal đó
window.onclick = function(event) {
    // Kiểm tra nếu click vào chính modal refund (vùng nền mờ)
    if (event.target == refundModal) {
        refundModal.style.display = "none"; // Ẩn modal refund
    }
}