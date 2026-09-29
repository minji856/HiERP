document.addEventListener("DOMContentLoaded", function () {
        const pwInput = document.getElementById("newPassword") || document.getElementById("password");
        const pwCheckInput = document.getElementById("newPasswordChk") || document.getElementById("passwordcheck");
        const message = document.getElementById("passwordMessage");
        // 정규식 메세지 출력
        const pwRegexMessage = document.getElementById("passwordRegex");

        // 해당 페이지에 비밀번호 입력창이 없으면 스크립트를 실행하지 않고 종료 (에러 방지)
        if (!pwInput || !pwCheckInput) return;

        // 비밀번호 정규식 객체 (대문자, 소문자, 숫자, 특수문자 각각 최소 1개 포함 + 8~16자)
        const pwRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!#$%^&*]).{8,16}$/;

        // 비밀번호 문자열을 검사하는 함수입니다.
        // return true는 올바른 양식
        function checkPwRegex() {
            const pw = pwInput.value;

            if (pw.length === 0) {
                pwRegexMessage.innerText = "";
            } else if (!pwRegex.test(pw)) {
                pwRegexMessage.innerText = "8~16자 영문 대소문자, 숫자, 특수문자를 포함해야 합니다."
                pwRegexMessage.style.color = "red";
            } else {
                pwRegexMessage.innerText = "사용가능합니다."
                pwRegexMessage.style.color = "green";
            }
            // 첫 번째 비번을 수정했을 때 두 번째 비번과 다르게변했는지 실시간으로 재검증한다.
            checkPwMatch();
        }

        // 비밀번호 일치 검사
        function checkPwMatch() {
            const pw = pwInput.value;
            const pwCheck = pwCheckInput.value;

            if (pwCheck.length === 0) {
                message.innerText = "";
                return;
            }

            if (pwCheck.length > 0 && pw !== pwCheck) {
                message.innerText = "비밀번호가 일치하지 않습니다.";
                message.style.color = "red";
            } else if (pwCheck.length > 0 && pw === pwCheck) {
                message.innerText = "비밀번호가 일치합니다.";
                message.style.color = "green";
            } else {
                message.innerText = "";
            }
        }

        // 값이 입력될 때마다 실시간으로 체크
        pwInput.addEventListener("input", checkPwRegex);
        pwCheckInput.addEventListener("input", checkPwMatch);

        // 폼 제출 시(submit) 최종 검증
        // 폼 ID가 달라도 입력창 스스로 찾아서 이벤트 걸어준다.
        const form = pwInput.closest("form");

        if (form) {
            form.addEventListener("submit", function (e) {
                const pw = pwInput.value;
                const pwCheck = pwCheckInput.value;

                // 브라우저 기본 필수값(required) 입력이 비어있으면 폼 전송을 넘김 (HTML5가 처리)
                if (pw.length === 0 || pwCheck.length === 0) {
                    return;
                }

                // 정규식 불일치 시
                if (!pwRegex.test(pw)) {
                    alert("비밀번호 양식을 확인해 주세요.");
                    pwInput.focus();
                    e.preventDefault(); // 서버 전송 중단
                    return;
                }

                // 비밀번호 불일치 시
                if (pw !== pwCheck) {
                    alert("비밀번호가 일치하지 않습니다.");
                    pwCheckInput.focus();
                    e.preventDefault(); // 서버 전송 중단
                    return;
                }

                // 모든 검증을 통과하면 폼은 알아서 서버로 submit 됩니다.
            });
        }
    }
)