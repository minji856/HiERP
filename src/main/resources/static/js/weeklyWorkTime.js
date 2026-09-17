document.addEventListener("DOMContentLoaded",function (){
    // 주간 누적 타이머 연동
    const weeklyDataEl = document.getElementById('weekly-attendance-data');
    const weeklyDisplayEl = document.getElementById('weekly-time-display');
    const weeklyProgressBar = document.getElementById('weekly-progress-bar');

    if (weeklyDataEl && weeklyDisplayEl && weeklyProgressBar) {
        // 서버에서 넘어온 초기 주간 누적 분(Minutes)
        let baseWeeklyMinutes = parseInt(weeklyDataEl.getAttribute('data-weekly-minutes')) || 0;

        // 오늘 실시간 근무 시간과 연동하기 위해, 오늘 당일의 시작 시각도 함께 체크
        const dataEl = document.getElementById('attendance-data');
        const clockInStr = dataEl ? dataEl.getAttribute('data-clock-in') : '';
        const isClockedOut = dataEl ? dataEl.getAttribute('data-is-clocked-out') === 'true' : true;

        // 만약 오늘 출근 상태이고 아직 퇴근 전이라면, 1초마다 주간 누적 시간도 함께 갱신
        if (clockInStr && !isClockedOut) {
            const clockInTime = new Date(clockInStr.replace(' ', 'T'));

            setInterval(() => {
                const now = new Date();
                const diffMs = now - clockInTime;
                if (diffMs < 0) return;

                let todayNetMinutes = Math.floor(diffMs / (1000 * 60));
                if (todayNetMinutes >= 240) todayNetMinutes -= 60; // 점심시간 공제

                // 서버에서 가져온 기존 주간 누적 분 + 오늘 실시간 증가분 (정확한 계산을 위해 오늘 몫을 매번 재계산하는 구조 혹은 초단위 반영)
                // 간단하게는 setInterval 안에서 화면 표시만 매끄럽게 처리할 수 있습니다.
            }, 1000);
        }
    }
})