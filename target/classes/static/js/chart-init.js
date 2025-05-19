const barCtx = document.getElementById("barChart").getContext("2d");
new Chart(barCtx, {
    type: "bar",
    data: {
        labels: ["Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6"],
        datasets: [{
            label: "Doanh thu (triệu ₫)",
            data: [150, 200, 180, 220, 240, 270],
            backgroundColor: "#3498db"
        }]
    },
    options: {
        responsive: true,
        scales: {
            y: {beginAtZero: true}
        }
    }
});

const pieCtx = document.getElementById("pieChart").getContext("2d");
new Chart(pieCtx, {
    type: "doughnut",
    data: {
        labels: ["Khách sạn", "Đặt phòng", "Dịch vụ", "Khác"],
        datasets: [{
            data: [45, 25, 20, 10],
            backgroundColor: ["#f39c12", "#2ecc71", "#9b59b6", "#e74c3c"]
        }]
    },
    options: {
        responsive: true,
        cutout: "50%"
    }
});
