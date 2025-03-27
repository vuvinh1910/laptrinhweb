function filterByLocation(locationCode) {
    let url = new URL(window.location.href);
    if (locationCode) {
        url.searchParams.set("locationCode", locationCode);
    } else {
        url.searchParams.delete("locationCode");
    }
    window.location.href = url.toString();
}
