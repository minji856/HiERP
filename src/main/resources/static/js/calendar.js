document.addEventListener('DOMContentLoaded', function() {
    let calendarEl = document.getElementById('calendar');

    let calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        // 달력 숫자에서 '일' 지우는 옵션
        dayCellContent: function(arg) {
            let text = arg.dayNumberText.replace('일', '');
            return { html: text };
        },
        headerToolbar: {
            left: 'prev,next,today',
            center: 'title',
            right: 'addEventButton,dayGridMonth,timeGridWeek,timeGridDay'
        },
        buttonText: {
            today: '오늘',
            month: '월',
            week: '주',
            day: '일'
        },
        customButtons: {
            addEventButton : {
                text : '일정 추가',
                click: function() {
                    // 모달 열기
                    const eventModal = new bootstrap.Modal(document.getElementById('eventModal'));
                    eventModal.show();
                    }
                }
            },
        eventSources: [
            // Google API 공휴일 일정불러오는 코드
            {
                events: function(fetchInfo, successCallback, failureCallback) {
                    fetch('/api/calendar')
                        .then(res => res.json())
                        .then(data => {
                            const events = (data.items || []).map(item => ({
                                title: item.summary,
                                start: item.start.date || item.start.dateTime,
                                end: item.end.date || item.end.dateTime,
                                allDay: true
                            }));
                            successCallback(events);
                        })
                        .catch(err => failureCallback(err));
                },
            },
            // 사용자가 직접 추가한 일정 불러오는 코드
            {
                url: '/api/calevents',
                color: '#81c784',
                failure: function() {
                    alert("이벤트 목록을 가져오다가 문제가 발생했습니다.");
                }
            }
        ]
    });
    calendar.render();

    // 저장 버튼 클릭 시 이벤트 등록
    document.getElementById('saveEventBtn').addEventListener('click', function() {
        const title = document.getElementById('eventTitle').value.trim();
        const start = document.getElementById('eventStart').value;
        const end = document.getElementById('eventEnd').value;

        // 유효성 검사
        if (!title || !start) {
            alert('제목과 시작일은 필수입니다.');
            return;
        }
        if (end && end < start) {
            alert('종료일은 시작일보다 이후여야 합니다.');
            return;
        }

        // 서버로 저장 EventController saveEvent 메서드
        fetch('/api/calevents', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title, start, end })
        })
            .then(res => res.json())
            .then(data => {
                // 폼 초기화 + 모달 닫기
                document.getElementById('eventForm').reset();
                const eventModal = bootstrap.Modal.getInstance(document.getElementById('eventModal'));
                eventModal.hide();
                alert('일정이 추가되었습니다!');

                // 서버에서 다시 이벤트 목록 새로 불러오기
                calendar.refetchEvents();
            })
            .catch(err => alert('저장 실패: ' + err));
    });
});