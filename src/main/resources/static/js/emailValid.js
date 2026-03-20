document.addEventListener("DOMContentLoaded", function (){
    async function checkEmailDuplicate() {
        const email = document.getElementById('email').value;
        const messageElement = document.getElementById('emailMsg');

        if (!email) {
            alert("이메일을 입력해주세요.");
            return;
        }

        try {
            // 서버의 중복 체크 API 호출
            const response = await fetch(`/account/check-email?email=${email}`);
            const isDuplicated = await response.json(); // true면 중복

            if (isDuplicated) {
                messageElement.innerText = "이미 사용 중인 이메일입니다.";
                messageElement.style.color = "red";
            } else {
                messageElement.innerText = "사용 가능한 이메일입니다.";
                messageElement.style.color = "green";
            }
        } catch (error) {
            console.error("에러 발생:", error);
            alert("중복 확인 중 오류가 발생했습니다.");
        }
    }
})