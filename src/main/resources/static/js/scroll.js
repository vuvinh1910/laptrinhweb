function scrollToChonPhong(event) {
    event.preventDefault(); // Ngăn hành vi mặc định của thẻ <a>
    const chonPhongDiv = document.getElementById('danhsachphong');
    if (chonPhongDiv) {
        const offset = 59; // Khoảng cách cần dịch lên trên (59px)
        const elementPosition = chonPhongDiv.getBoundingClientRect().top + window.pageYOffset;
        const offsetPosition = elementPosition - offset;

        window.scrollTo({
            top: offsetPosition,
            behavior: 'smooth'
        });
    }
}