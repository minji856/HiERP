document.addEventListener("DOMContentLoaded", function () {
    const yearSelect = document.getElementById('birth-year');
    const monthSelect = document.getElementById('birth-month');
    const daySelect = document.getElementById('birth-day');
    const birthdayHidden = document.getElementById('birthday');

    // 초기화: 연도(1950~현재)와 월(1~12) 채우기
    const now = new Date();
    const currentYear = new Date().getFullYear();

    // 연도 옵션 설정 (19살위부터 정년퇴직 나이 65세까지 성인기준 현재년도 -19)
    const maxYear = currentYear - 19;
    const minYear = currentYear - 65;

    /*
    for (let i = currentYear; i >= 1950; i--) {
        yearSelect.add(new Option(i, i));
    }
    */
    for (let i = maxYear; i >= minYear; i--) {
        yearSelect.add(new Option(i, i));
    }

    for (let i = 1; i <= 12; i++) {
        const m = i < 10 ? '0' + i : i;
        monthSelect.add(new Option(i, m));
    }

    // 선택한 연도와 월에 따라 '일' 개수 변경하기
    function updateDays() {
        const year = yearSelect.value;
        const month = monthSelect.value;

        // 기존에 생성된 '일' 옵션들을 싹 비웁니다 (첫 번째 "일" 옵션만 남기고)
        daySelect.options.length = 1;

        if (year && month) {
            // 다음 달의 0번째 날짜를 구하면 '이번 달의 마지막 날'이 나옵니다.
            // 예: new Date(2024, 2, 0) -> 2024년 2월의 마지막 날(29일)
            const lastDay = new Date(year, month, 0).getDate();

            for (let i = 1; i <= lastDay; i++) {
                const d = i < 10 ? '0' + i : i;
                daySelect.add(new Option(i, d));
            }
        }
        // 날짜가 바뀔 때마다 전체 생년월일 값 업데이트
        updateBirthday();
    }

    // 서버로 전송할 최종 날짜 문자열(yyyy-MM-dd) 히든태그 생성
    function updateBirthday() {
        const year = yearSelect.value;
        const month = monthSelect.value;
        const day = daySelect.value;

        if (year && month && day) {
            birthdayHidden.value = `${year}-${month}-${day}`;
        } else {
            birthdayHidden.value = ""; // 하나라도 선택 안 되면 비움
        }
    }

    // 4. 이벤트 연결
    // 연도나 월이 바뀌면 '일'의 개수를 다시 계산합니다.
    yearSelect.addEventListener('change', updateDays);
    monthSelect.addEventListener('change', updateDays);
    // 일만 바뀌면 최종 값만 업데이트합니다.
    daySelect.addEventListener('change', updateBirthday);
});