document.addEventListener("DOMContentLoaded", function ()
    {
        const passwordInput = document.getElementById("password");
        const pwCheckInput = document.getElementById("passwordcheck");
        const message = document.getElementById("passwordMessage");
        const pwRegexMessage = document.getElementById("passwordRegex");
        // 비밀번호 정규식 객체
        const pwRegex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!#$%^&*]).{8,16}$/;

        // 비밀번호 문자열을 검사하는 함수입니다.
        // return true는 올바른 양식
        function checkpwRegex() {
            const pw = passwordInput.value;

            if(pw.length === 0){
                pwRegexMessage.innerText = "";
            }else if (!pwRegex.test(pw)) {
                pwRegexMessage.innerText = "8~16자 영문 대소문자, 숫자, 특수문자를 포함해야 합니다."
                pwRegexMessage.style.color = "red";
            }else {
                pwRegexMessage.innerText = "사용가능합니다."
                pwRegexMessage.style.color = "green";
            }
            // 굳이 해야하는지
            checkPasswordMatch();
        }


        function checkPasswordMatch() {
            const pw = passwordInput.value;
            const pwCheck = pwCheckInput.value;

            if (pwCheck.length === 0){
                message.innerText = "";
                return;
            }

            if (pwCheck.length > 0 && pw !== pwCheck){
                message.innerText = "비밀번호가 일치하지 않습니다.";
                message.style.color = "red";
            } else if (pwCheck.length > 0 && pw === pwCheck) {
                message.innerText = "비밀번호가 일치합니다.";
                message.style.color = "green";
            } else {
                message.innerText = "";
            }
/*
            if (pwCheck.length === 0){
                message.innerText = "";
                return;
            }
            if (pw === pwCheck){
                message.innerText = "비밀번호가 일치합니다.";
                message.style.color = "green";
            } else {
                message.innerText = "비밀번호가 일치하지 않습니다.";
                message.style.color = "red";
            }
 */
        }

        // 값이 입력될 때마다 실시간으로 체크
        passwordInput.addEventListener("keyup", checkpwRegex);
        pwCheckInput.addEventListener("keyup", checkPasswordMatch);
    }
)