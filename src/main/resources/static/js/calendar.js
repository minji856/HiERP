document.addEventListener('DOMContentLoaded', function() {
    let calendarEl = document.getElementById('calendar');

    let calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        headerToolbar: {
            left: 'prev,next,today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        buttonText: {
            today: '오늘',
            month: '월',
            week: '주',
            day: '일'
        },
        selectable : true,
        events: function(fetchInfo, successCallback, failureCallback) {
            fetch('/api/calendar')
                .then(res => res.json())
                .then(data => {
                    const events = (data.items || []).map(item => ({
                        title: item.summary,
                        start: item.start.date || item.start.dateTime,
                        end: item.end.date || item.end.dateTime,
                        allDay: true,
                        // color: 'red',
                        color: '#f83345',
                        textColor: 'white'
                    }));
                    successCallback(events);
                })
                .catch(err => failureCallback(err));
        }
    });
    calendar.render();
    });
//});