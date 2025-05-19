document.addEventListener("DOMContentLoaded", async function () {
    const deleteButtons = document.querySelectorAll('.del-button');
    const confirmationPopup = document.getElementById('confirmationPopup');
    const confirmationPopupLogin = document.getElementById('confirmationPopup-login');
    let currentForm;
    let loggedIn = false;

    // Kiểm tra trạng thái đăng nhập
    try {
        const response = await fetch("/lap-trinh-web/public/isLogin");
        if (response.ok) {
            const data = await response.json();
            loggedIn = data;  // Đảm bảo rằng dữ liệu trả về có cấu trúc đúng { loggedIn: true/false }
        }
    } catch (error) {
        console.error("Error checking login status:", error);
    }

    // Gán sự kiện cho các nút xóa
    deleteButtons.forEach(button => {
        button.addEventListener('click', function (event) {
            event.preventDefault();  // Ngừng hành động mặc định (không gửi form ngay lập tức)
            currentForm = this.closest('form');  // Lưu trữ form để submit sau này
            if (loggedIn) {
                currentForm.submit();
            } else {
                confirmationPopupLogin.style.display = 'block';  // Hiển thị popup yêu cầu đăng nhập
            }
        });
    });

    // Sự kiện xác nhận đăng nhập (dành cho người dùng chưa đăng nhập)
    document.getElementById('confirm').addEventListener('click', function () {
        const currentPageUrl = window.location.href;
        window.location.href = '/lap-trinh-web/public/login?next=' + encodeURIComponent(currentPageUrl);
    });

    // Sự kiện hủy đăng nhập (dành cho người dùng chưa đăng nhập)
    document.getElementById('cancel').addEventListener('click', function () {
        confirmationPopupLogin.style.display = 'none';  // Ẩn popup khi người dùng hủy
    });
});
