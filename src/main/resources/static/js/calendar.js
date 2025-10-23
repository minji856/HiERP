document.addEventListener('DOMContentLoaded', function() {
    let calendarEl = document.getElementById('calendar');

    let calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
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
                                startDate : dateStr
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
            // 1️⃣ Google API 일정
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
            // 2️⃣ 내가 DB에 저장한 일정
            {
                url: '/api/calevents', // GET /api/calevents 자동 호출됨
                color: '#2e7d32',      // 파란색
                textColor: 'white'
            }
        ]
        // events: function(fetchInfo, successCallback, failureCallback) {
        //     fetch('/api/calendar')
        //         .then(res => res.json())
        //         .then(data => {
        //             const events = (data.items || []).map(item => ({
        //                 title: item.summary,
        //                 start: item.start.date || item.start.dateTime,
        //                 end: item.end.date || item.end.dateTime,
        //                 allDay: true,
        //                 // color: '#f83345',
        //             }));
        //             successCallback(events);
        //         })
        //     .catch(err => failureCallback(err));
        // }
    });
    calendar.render();
});