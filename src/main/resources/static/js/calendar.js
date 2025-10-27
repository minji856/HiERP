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
        customButtons : {
            addEventButton : {
                text : '일정 추가',
                click : function () {
                    const title = prompt('일정 제목을 입력하세요.');
                    const dateStr = prompt('날짜를 YYYY-MM-DD 형식으로 입력하세요.');

                    if (title && dateStr) {
                        fetch('/api/calevents',{
                            method : 'POST',
                            headers : { 'Content-Type' : 'application/json'},
                            body : JSON.stringify({
                                title : title,
                                start : dateStr
                            })
                        })
                        .then(res => res.json())
                        .then(data => {
                            const date = new Date(dateStr + 'T00:00:00'); //날짜 포맷 변환
                            calendar.addEvent({
                                title: title,
                                start : date,
                                allDay: true
                            });
                            alert('일정이 추가되었습니다.');
                            })
                            .catch(err => alert('저장 실패: ' + err));
                        } else {
                                alert('입력값이 올바르지 않습니다.');
                        }
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
                color: "#81c784",
                failure: function() {
                    alert("이벤트 목록을 가져오다가 문제가 발생했습니다.");
                }
            }
        ]
    });
    calendar.render();
});