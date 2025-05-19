let bank = {
    BANK_ID: "MB",
    ACCOUNT_NO: "60999992077",
    ACCOUNT_NAME: "LE VU THANH VINH"
}
const paymentContainer = document.querySelector('.payment-container');
const AMOUNT = parseInt(paymentContainer.dataset.price) || 0;
const BOOKING_ID = parseInt(paymentContainer.dataset.bookingId) || 0;
const DESCRIPTION = `TT ID ${BOOKING_ID} BOOKING`;
let VOUCHER_CODE = paymentContainer.dataset.voucherCode || '';

document.querySelector('form').addEventListener('submit', () => {
    VOUCHER_CODE = document.getElementById('voucherCode').value;
});

// Tạo URL cho ảnh QR
let qrImageUrl = `https://img.vietqr.io/image/${bank.BANK_ID}-${bank.ACCOUNT_NO}-compact2.png?amount=${AMOUNT}&addInfo=${DESCRIPTION}&accountName=${bank.ACCOUNT_NAME}`;
console.log("Generated QR URL:", qrImageUrl);

// Lấy thẻ img và cập nhật thuộc tính src
document.getElementById('vietqr-image').src = qrImageUrl;

let paid = false;
let isChecking = false; // Biến để ngăn chặn chồng chéo
async function checkPaid(amount, description) {
    if (paid || isChecking) return; // Thoát nếu đã thanh toán hoặc đang kiểm tra
    isChecking = true; // Đánh dấu đang kiểm tra

    try {
        const response = await fetch('https://script.google.com/macros/s/AKfycbyqEmT9S4Wa-WEhIfxxJ6yj-vchLLZGHH02hVkxipeT6WmLAa-n_LtJRTrQVCgpIP_M/exec');
        const data = await response.json();
        const lastPaid = data.data[1];
        let lastAmount = lastPaid["Giá trị"];
        let lastDescription = lastPaid["Mô tả"];

        if (lastAmount >= amount && lastDescription.includes(description)) {
            paid = true;
            clearInterval(paymentInterval); // Dừng setInterval
            try {
                document.getElementById('hiddenVoucherCode').value = VOUCHER_CODE;
                document.getElementById('payment-form').submit();
            } catch (apiError) {
                console.error('Lỗi khi gọi API thanh toán:', apiError);
                document.getElementById('payment-status').innerText = "❌ Lỗi xử lý thanh toán!";
                document.getElementById('payment-status').className = "mt-3 text-danger fw-bold";
            }
        } else {
            console.log('Chưa thanh toán');
        }
    } catch (error) {
        console.log('Lỗi khi lấy JSON từ Google Sheets:', error);
    } finally {
        isChecking = false; // Đặt lại trạng thái kiểm tra
    }
}

const paymentInterval = setInterval(() => checkPaid(AMOUNT, DESCRIPTION), 1000);