// Tìm booking-form-container
const bookingFormContainer = document.querySelector('.booking-form-container');

if (bookingFormContainer) {
    // Lấy các input trong booking-form-container
    const checkInField = bookingFormContainer.querySelector('#checkIn');
    const checkOutField = bookingFormContainer.querySelector('#checkOut');
    const numOfPeopleField = bookingFormContainer.querySelector('#numOfPeople');
    const submitButton = bookingFormContainer.querySelector('#submitButton');

    // Hàm kiểm tra tình trạng phòng
    const checkRoomAvailability = async () => {
        const roomId = bookingFormContainer.querySelector('input[name="roomId"]').value;
        const checkIn = checkInField.value;
        const checkOut = checkOutField.value;

        if (checkIn && checkOut) {
            const response = await fetch(`${roomId}/availability?checkIn=${checkIn}&checkOut=${checkOut}`);
            const isAvailable = await response.json();
            return isAvailable;
        }
        return true; // Nếu chưa chọn ngày, coi như phòng có sẵn
    };

    // Hàm kiểm tra form
    const checkForm = async () => {
        const checkIn = checkInField.value;
        const checkOut = checkOutField.value;
        const numOfPeople = numOfPeopleField.value;

        // Kích hoạt nút nếu cả ba trường đã được nhập
        if (checkIn && checkOut && numOfPeople) {
            const isAvailable = await checkRoomAvailability();
            if (isAvailable) {
                submitButton.disabled = false;
                submitButton.textContent = "Đặt Phòng"; // Trở lại trạng thái đặt phòng
            } else {
                submitButton.disabled = true;
                submitButton.textContent = "Hết Phòng"; // Đổi thành chữ "Hết Phòng"
            }
        } else {
            submitButton.disabled = true;
            submitButton.textContent = "Đặt Phòng"; // Đảm bảo nút không bị nhầm lẫn trạng thái
        }
    };

    // Cấu hình Flatpickr cho check-in
    flatpickr(checkInField, {
        dateFormat: "d/m/Y",
        minDate: "today", // Không cho chọn ngày trong quá khứ
        onChange: function (selectedDates) {
            if (selectedDates.length > 0) {
                const nextDay = new Date(selectedDates[0]);
                nextDay.setDate(nextDay.getDate() + 1);

                // Đặt minDate cho ngày trả phòng
                if (checkOutField._flatpickr) {
                    checkOutField._flatpickr.set("minDate", nextDay);

                    // Nếu ngày trả phòng nhỏ hơn ngày nhận phòng + 1, tự động cập nhật lại
                    if (checkOutField.value && new Date(checkOutField.value.split('/').reverse().join('-')) <= nextDay) {
                        checkOutField._flatpickr.setDate(nextDay);
                    }

                    // Mở lịch ngày trả phòng tại tháng và năm của ngày nhận phòng
                    checkOutField._flatpickr.setDate(nextDay, true); // Đặt ngày trả phòng sau ngày nhận phòng
                    checkOutField._flatpickr.open(); // Mở lịch
                }
            }
        },
    });

    // Cấu hình Flatpickr cho check-out
    flatpickr(checkOutField, {
        dateFormat: "d/m/Y",
        minDate: "today", // Không cho chọn ngày trong quá khứ
    });

    // Gắn sự kiện cho các input bên trong booking-form-container
    if (checkInField) checkInField.addEventListener('input', checkForm);
    if (checkOutField) checkOutField.addEventListener('input', checkForm);
    if (numOfPeopleField) numOfPeopleField.addEventListener('input', checkForm);
}
