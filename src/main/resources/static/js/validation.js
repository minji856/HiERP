document.addEventListener("DOMContentLoaded", function () {

    // 텍스트 입력 필드
    document.addEventListener("input", function (event) {

        const input = event.target;

        if (!input.matches("input, textarea")) {
            return;
        }

        hideValidationError(input);
    });

    // select, radio 등
    document.addEventListener("change", function (event) {

        const input = event.target;

        if (!input.matches("select, input[type='radio']")) {
            return;
        }

        // 생년월일은 birthValid.js에서 별도로 처리
        if (
            input.id === "birth-year" ||
            input.id === "birth-month" ||
            input.id === "birth-day"
        ) {
            return;
        }

        hideValidationError(input);
    });

    function hideValidationError(input) {

        const container = input.closest(".input-style-1, .mb-3");

        if (!container) {
            return;
        }

        const errorMessage =
            container.querySelector(".validation-error");

        if (errorMessage) {
            errorMessage.style.display = "none";
        }
    }
});