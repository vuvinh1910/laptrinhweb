// Tìm booking-form-container
const bookingFormContainer = document.querySelector('.booking-form-container');

if (bookingFormContainer) {
    const checkInField = bookingFormContainer.querySelector('#checkIn');
    const checkOutField = bookingFormContainer.querySelector('#checkOut');
    const numOfPeopleField = bookingFormContainer.querySelector('#numOfPeople');
    const phoneField = bookingFormContainer.querySelector('#phone');
    const submitButton = bookingFormContainer.querySelector('#submitButton');

    const checkRoomAvailability = async () => {
        const roomId = bookingFormContainer.querySelector('input[name="roomId"]').value;
        const checkIn = checkInField.value;
        const checkOut = checkOutField.value;

        if (checkIn && checkOut) {
            const response = await fetch(`${roomId}/availability?checkIn=${checkIn}&checkOut=${checkOut}`);
            const isAvailable = await response.json();
            return isAvailable;
        }
        return true;
    };

    const checkForm = async () => {
        const checkIn = checkInField.value;
        const checkOut = checkOutField.value;
        const numOfPeople = parseInt(numOfPeopleField.value, 10);
        const phone = phoneField.value.trim();

        // Giới hạn số người từ 1 đến 5
        if (isNaN(numOfPeople) || numOfPeople < 1) {
            numOfPeopleField.value = 1;
        } else if (numOfPeople > 5) {
            numOfPeopleField.value = 5;
        }

        // Kiểm tra điều kiện đủ để bật nút đặt phòng
        if (checkIn && checkOut && phone && numOfPeople >= 1 && numOfPeople <= 5) {
            const isAvailable = await checkRoomAvailability();
            if (isAvailable) {
                submitButton.disabled = false;
                submitButton.textContent = "Đặt Phòng";
            } else {
                submitButton.disabled = true;
                submitButton.textContent = "Hết Phòng";
            }
        } else {
            submitButton.disabled = true;
            submitButton.textContent = "Đặt Phòng";
        }
    };

    flatpickr(checkInField, {
        dateFormat: "d/m/Y",
        minDate: "today",
        onChange: function (selectedDates) {
            if (selectedDates.length > 0) {
                const nextDay = new Date(selectedDates[0]);
                nextDay.setDate(nextDay.getDate() + 1);
                if (checkOutField._flatpickr) {
                    checkOutField._flatpickr.set("minDate", nextDay);
                    if (checkOutField.value && new Date(checkOutField.value.split('/').reverse().join('-')) <= nextDay) {
                        checkOutField._flatpickr.setDate(nextDay);
                    }
                    checkOutField._flatpickr.setDate(nextDay, true);
                    checkOutField._flatpickr.open();
                }
            }
        },
    });

    flatpickr(checkOutField, {
        dateFormat: "d/m/Y",
        minDate: "today",
    });

    // Gắn sự kiện cho tất cả các trường nhập
    checkInField.addEventListener('input', checkForm);
    checkOutField.addEventListener('input', checkForm);
    numOfPeopleField.addEventListener('input', checkForm);
    phoneField.addEventListener('input', checkForm);
}
