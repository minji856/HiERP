document.addEventListener('DOMContentLoaded', function() {
    let calendarEl = document.getElementById('calendar');

    fetch('/api/calendar/key')
        .then(response => response.text())
        .then(apiKey => {
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
                googleCalendarApiKey: apiKey,
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