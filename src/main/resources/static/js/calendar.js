document.addEventListener('DOMContentLoaded', function() {
    let calendarEl = document.getElementById('calendar');

    fetch('/api/calendar')
        .then(response => response.text())
        .then(googleApiKey => {
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
                googleCalendarApiKey: googleApiKey,
                eventSources: [
                    {
                        googleCalendarId: 'ko.south_korea#holiday@group.v.calendar.google.com',
                        className: 'ko-event'
                    }
                ],
            });
        calendar.render();
    });
});