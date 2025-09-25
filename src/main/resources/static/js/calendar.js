document.addEventListener('DOMContentLoaded', function() {
    let calendarEl = document.getElementById('calendar');

    let calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale : 'ko',
        headerToolbar: {
            // customButton 은 left 또는 right 안에 넣으면 적용 된다
            left: 'prev,next,today',  // today는 , 와 띄어쓰기에 따라서 위치가 바뀐다
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        buttonText: {
            today: '오늘',
            month: '월',
            week: '주',
            day: '일'
        },
        googleCalendarApiKey: '<YOUR API KEY>',
            eventSources: [
                {
                    googleCalendarId: 'efgh5678@group.calendar.google.com',
                    className: 'ko-event'
                }
            ],
    });
    calendar.render();
});