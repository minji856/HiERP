document.addEventListener('DOMContentLoaded', function() {
    let calendarEl = document.getElementById('calendar');

    let calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        headerToolbar: {
            // customButton 은 left 또는 right 안에 넣으면 적용 된다
            left: 'prev,next,today',  // today는 , 와 띄어쓰기에 따라서 위치가 바뀐다
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay',
        },
        googleCalendarApiKey: '<YOUR API KEY>',
            eventSources: [
                {
                    googleCalendarId: 'efgh5678@group.calendar.google.com',
                    className: 'ko-event'
                }
            ],
        locale : 'ko'
    });
    calendar.render();
});