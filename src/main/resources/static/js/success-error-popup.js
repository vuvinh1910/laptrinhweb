var success = "[[${success}]]";
var error = "[[${error}]]";

document.addEventListener("DOMContentLoaded", function() {
    if (success && success !== "null") {
        alert(success);
    }
    if (error && error !== "null") {
        alert(error);
    }
});