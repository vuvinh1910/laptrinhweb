document.addEventListener('DOMContentLoaded', function() {
    const deleteButtons = document.querySelectorAll('.delete-btn');
    const confirmationPopup = document.getElementById('confirmationPopup');
    const confirmDeleteBtn = document.getElementById('confirmDelete');
    const cancelDeleteBtn = document.getElementById('cancelDelete');
    let deleteUrl = '';

    // Xử lý click nút xóa trong bảng
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            deleteUrl = this.href; // Lưu trữ URL xóa
            confirmationPopup.style.display = 'block';
        });
    });

    // Xác nhận xóa
    confirmDeleteBtn.addEventListener('click', function() {
        if (deleteUrl) {
            window.location.href = deleteUrl; // Chuyển hướng đến URL xóa
            confirmationPopup.style.display = 'none';
        }
    });

    // Hủy xóa
    cancelDeleteBtn.addEventListener('click', function() {
        confirmationPopup.style.display = 'none';
        deleteUrl = '';
    });

    // Đóng popup khi click bên ngoài
    window.addEventListener('click', function(e) {
        if (e.target === confirmationPopup) {
            confirmationPopup.style.display = 'none';
            deleteUrl = '';
        }
    });
});