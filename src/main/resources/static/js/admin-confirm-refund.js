document.addEventListener("DOMContentLoaded", function () {
    const openModalButtons = document.querySelectorAll(".open-refund-modal");
    const refundModal = document.getElementById("refundModal");
    const closeModalButton = refundModal.querySelector(".close-refund-modal");

    // Các phần tử trong modal cần cập nhật
    const bankHolderSpan = document.getElementById("modalBankHolder");
    const bankTypeSpan = document.getElementById("modalBankType");
    const bankCodeSpan = document.getElementById("modalBankCode");
    const reasonSpan = document.getElementById("modalReason");
    const bookingIdInput = document.getElementById("modalBookingId");

    // Khi người dùng bấm nút "Xử lý"
    openModalButtons.forEach(button => {
        button.addEventListener("click", function () {
            const bankHolder = this.dataset.bank;
            const bankType = this.dataset.banktype;
            const bankCode = this.dataset.bankcode;
            const reason = this.dataset.reason;
            const bookingId = this.dataset.bookingid;

            // Gán vào modal
            bankHolderSpan.textContent = bankHolder;
            bankTypeSpan.textContent = bankType;
            bankCodeSpan.textContent = bankCode;
            reasonSpan.textContent = reason;
            bookingIdInput.value = bookingId;

            refundModal.style.display = "flex";
        });
    });

    // Đóng modal khi bấm X
    closeModalButton.addEventListener("click", function () {
        refundModal.style.display = "none";
    });

    // Đóng modal khi click ra ngoài vùng nội dung
    window.addEventListener("click", function (event) {
        if (event.target === refundModal) {
            refundModal.style.display = "none";
        }
    });
});