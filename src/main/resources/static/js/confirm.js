document.addEventListener("DOMContentLoaded", async function () {
    const deleteButtons = document.querySelectorAll('.del-button');
    const confirmationPopup = document.getElementById('confirmationPopup');
    const confirmationPopupLogin = document.getElementById('confirmationPopup-login');
    let currentForm;
    let loggedIn = false;

    // Kiểm tra trạng thái đăng nhập
    try {
        const response = await fetch("/web-nhom-5/public/isLogin");
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
                confirmationPopup.style.display = 'block';  // Hiển thị popup xác nhận xóa
            } else {
                confirmationPopupLogin.style.display = 'block';  // Hiển thị popup yêu cầu đăng nhập
            }
        });
    });

    // Sự kiện xác nhận xóa (dành cho người dùng đã đăng nhập)
    document.getElementById('confirmDelete').addEventListener('click', function () {
        if (currentForm) {
            currentForm.submit();  // Gửi form để xóa
            confirmationPopup.style.display = 'none';  // Ẩn popup sau khi xác nhận
        }
    });

    // Sự kiện hủy xóa (dành cho người dùng đã đăng nhập)
    document.getElementById('cancelDelete').addEventListener('click', function () {
        confirmationPopup.style.display = 'none';  // Ẩn popup khi người dùng hủy
    });

    // Sự kiện xác nhận đăng nhập (dành cho người dùng chưa đăng nhập)
    document.getElementById('confirm').addEventListener('click', function () {
        window.location.href = '/web-nhom-5/public/login';  // Chuyển hướng người dùng đến trang đăng nhập
    });

    // Sự kiện hủy đăng nhập (dành cho người dùng chưa đăng nhập)
    document.getElementById('cancel').addEventListener('click', function () {
        confirmationPopupLogin.style.display = 'none';  // Ẩn popup khi người dùng hủy
    });
});
