document.addEventListener("DOMContentLoaded", function () {

    const emailInput = document.getElementById("email");
    const messageElement = document.getElementById("emailMsg");

    // 이메일을 다시 입력하기 시작하면 기존 메시지 삭제
    emailInput.addEventListener("input", function () {
        messageElement.innerText = "";
        // 이전 서버 에러 색상 제거
        messageElement.classList.remove("text-danger");
        messageElement.classList.remove("text-success");
    });

    // 입력을 끝내고 다른 곳으로 이동하면 중복 확인
    emailInput.addEventListener("blur", async function () {

        const email = emailInput.value.trim();

        // 이메일이 비어있으면 검사하지 않음
        if (!email) {
            return;
        }

        // 이메일 형식이 올바르지 않으면 검사하지 않음
        if (!emailInput.validity.valid) {
            messageElement.innerText =
                "올바른 이메일 형식을 입력해주세요.";

            messageElement.classList.remove("text-success");
            messageElement.classList.add("text-danger");

            return;
        }

        try {
            const response = await fetch(
                `/account/check-email?email=${encodeURIComponent(email)}`
            );

            const isDuplicated = await response.json();

            if (isDuplicated) {
                messageElement.innerText =
                    "이미 사용 중인 이메일입니다.";
                messageElement.classList.remove("text-success");
                messageElement.classList.add("text-danger");
            } else {
                messageElement.innerText =
                    "사용 가능한 이메일입니다.";
                messageElement.classList.remove("text-danger");
                messageElement.classList.add("text-success");
            }

        } catch (error) {
            console.error("이메일 중복 확인 중 오류:", error);

            messageElement.innerText =
                "중복 확인 중 오류가 발생했습니다.";

            messageElement.classList.remove("text-success");
            messageElement.classList.add("text-danger");
        }
    });
});