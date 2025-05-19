document.addEventListener('DOMContentLoaded', function () {
    const deleteButtons = document.querySelectorAll('.delete-btn');
    const confirmationPopup = document.getElementById('confirmationPopup');
    const confirmDeleteBtn = document.getElementById('confirmDelete');
    const cancelDeleteBtn = document.getElementById('cancelDelete');

    let formToSubmit = null;
    let deleteUrl = null;

    deleteButtons.forEach(button => {
        button.addEventListener('click', function (e) {
            e.preventDefault();

            // Nếu là nút trong form
            const parentForm = this.closest('form');
            if (parentForm) {
                formToSubmit = parentForm;
                deleteUrl = null;
            } else {
                formToSubmit = null;
                deleteUrl = this.getAttribute('href');
            }

            confirmationPopup.style.display = 'block';
        });
    });

    // Xác nhận xóa
    confirmDeleteBtn.addEventListener('click', function () {
        if (formToSubmit) {
            formToSubmit.submit();
        } else if (deleteUrl) {
            window.location.href = deleteUrl;
        }
        confirmationPopup.style.display = 'none';
    });

    // Hủy xóa
    cancelDeleteBtn.addEventListener('click', function () {
        confirmationPopup.style.display = 'none';
        formToSubmit = null;
        deleteUrl = null;
    });

    // Click ngoài popup sẽ đóng
    window.addEventListener('click', function (e) {
        if (e.target === confirmationPopup) {
            confirmationPopup.style.display = 'none';
            formToSubmit = null;
            deleteUrl = null;
        }
    });
});
