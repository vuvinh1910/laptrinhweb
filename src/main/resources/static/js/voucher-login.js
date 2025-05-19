document.addEventListener("DOMContentLoaded", async function () {
    const claimButtons = document.querySelectorAll('.btn-claim-voucher');
    const confirmationPopupLogin = document.getElementById('confirmationPopup-login');
    let loggedIn = false;

    // Kiểm tra trạng thái đăng nhập
    try {
        const response = await fetch("/lap-trinh-web/public/isLogin");
        if (response.ok) {
            const data = await response.json();
            loggedIn = data === true || data.loggedIn === true;
        }
    } catch (error) {
        console.error("Lỗi khi kiểm tra trạng thái đăng nhập:", error);
    }

    // Gán sự kiện cho các nút "Lấy Ngay"
    claimButtons.forEach(button => {
        button.addEventListener('click', function (event) {
            event.preventDefault();
            if (loggedIn) {
                // TODO: gọi API để claim voucher, ví dụ:
                const form = button.closest('form');
                if (form) {
                    form.submit(); // Gửi form
                } else {
                    console.error("Nút Lấy Ngay không nằm trong form.");
                }
            } else {
                confirmationPopupLogin.style.display = 'block';
            }
        });
    });

    // Xác nhận đăng nhập
    document.getElementById('confirm').addEventListener('click', function () {
        const currentPageUrl = window.location.href;
        window.location.href = '/lap-trinh-web/public/login?next=' + encodeURIComponent(currentPageUrl);
    });

    // Hủy popup
    document.getElementById('cancel').addEventListener('click', function () {
        confirmationPopupLogin.style.display = 'none';
    });
});
