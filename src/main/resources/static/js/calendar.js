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
        // 날짜 클릭시 그 날짜로 자동 입력 되면서 일정 추가 모달창 활성화
        dateClick : function(info){
           openEventModal(info.dateStr)
        },
        eventClick: function(info) {
            const event = info.event;
            const detailModal = new bootstrap.Modal(document.getElementById('eventDetailModal'));

            // 날짜 포맷 (YYYY-MM-DD 형태로)
            const start = event.start ? event.start.toISOString().slice(0, 10) : '';
            const end = event.end ? event.end.toISOString().slice(0, 10) : start;

            // 모달 내용 채우기
            document.getElementById('detailTitle').textContent = event.title;
            document.getElementById('detailStart').textContent = start;
            document.getElementById('detailEnd').textContent = end;

            // 모달 표시
            detailModal.show();

            // 캘린더 클릭 이벤트의 기본 동작 방지 (페이지 이동 등)
            info.jsEvent.preventDefault();
        },
        customButtons: {
            addEventButton : {
                text : '일정 추가',
                click: function() {
                    openEventModal(); // 날짜 미선택
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

    function openEventModal(dateStr = '') {
        // 폼 초기화 및 기본값으로 설정
        document.getElementById('eventForm').reset();
        allDayCheck.checked = false;
        endGroup.style.display = 'block';

        if (dateStr) {
            document.getElementById('eventStart').value = dateStr;
            document.getElementById('eventEnd').value = dateStr;
        }

        const modal = new bootstrap.Modal(document.getElementById('eventModal'));
        modal.show();
    }

    const allDayCheck = document.getElementById('eventAllDay');
    const endGroup = document.getElementById('endDateGroup');

    // 종일 체크시 종료일 입력 칸 비활성화
    allDayCheck.addEventListener('change', function() {
        endGroup.style.display = allDayCheck.checked ? 'none' : 'block';
    });

    // 저장 버튼 클릭 시 이벤트 등록
    document.getElementById('saveEventBtn').addEventListener('click', function() {
        const title = document.getElementById('eventTitle').value.trim();
        const start = document.getElementById('eventStart').value;
        let end = document.getElementById('eventEnd').value;

        // 유효성 검사
        if (!title || !start) {
            alert('제목과 시작일은 필수입니다.');
            return;
        }
        if (!allDayCheck.checked && end && end < start) {
            alert('종료일은 시작일보다 이후여야 합니다.');
            return;
        }
        // 종일이면 end = start
        if (allDayCheck.checked || !end) end = start;

        // 서버로 저장 EventController saveEvent 메서드
        fetch('/api/calevents', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title, start, end })
        })
            .then(res => res.json())
            .then(data => {
                // reset()으로 입력 내용 초기화 + 모달 닫기
                document.getElementById('eventForm').reset();
                allDayCheck.checked = false;
                endGroup.style.display = 'block';

                const eventModal = bootstrap.Modal.getInstance(document.getElementById('eventModal'));
                eventModal.hide();

                // 서버에서 다시 이벤트 목록 새로 불러오기
                calendar.refetchEvents();
                alert('일정이 추가되었습니다!');
            })
            .catch(err => alert('저장 실패: ' + err));
    });
});