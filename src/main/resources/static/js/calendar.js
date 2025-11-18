document.addEventListener('DOMContentLoaded', function () {
    let calendarEl = document.getElementById('calendar');

    // 수정과 추가 구별하기 위한 EditMode 추가
    let isEditMode = false;
    let editingEventId = null;

    // 종일 일정과 종료일 전역 변수 설정
    const allDayCheck = document.getElementById('eventAllDay');
    const endGroup = document.getElementById('endDateGroup');

    // 종일 체크시 종료일 입력 칸 비활성화
    allDayCheck.addEventListener('change', function () {
        endGroup.style.display = allDayCheck.checked ? 'none' : 'block';
    });

    // 모달 함수들 선언
    let eventModal = new bootstrap.Modal(document.getElementById('eventModal'));
    let eventDetailModal = new bootstrap.Modal(document.getElementById('eventDetailModal'));

    let calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        // 기본 옵션인 날짜에 숫자+일에서 '일' 글자 지우는 옵션
        dayCellContent: arg => ({html: arg.dayNumberText.replace('일', '')}),
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
        // 날짜 클릭 시 그 날짜로 자동 입력 되면서 일정 추가 모달창 활성화
        dateClick: (info) => openEventModal(info.dateStr),
        // 일정 클릭 시 일정 상세보기 표시 와 수정, 삭제버튼
        eventClick: function (info) {
            const event = info.event;
            const updateBtn = document.getElementById('updateEventBtn');
            const deleteBtn = document.getElementById('deleteEventBtn');
            // 일정 상세보기 모달 표시
            eventDetailModal.show();

            // 날짜 포맷 (YYYY-MM-DD 형태로)
            // const start = event.start ? event.start.toISOString().slice(0, 10) : '';
            // const end = event.end ? event.end.toISOString().slice(0, 10) : start;

            const start = event.startStr ?? event.start.toISOString().slice(0, 10);
            const end = event.end ? event.end.toISOString().slice(0, 10) : start;

            // const end = event.endStr ?? (event.end ? event.end.toISOString().slice(0, 10) : start);
            // const start = event.startStr;
            // const end = event.endStr || start;

            // 모달창에 DB 내용 채우기
            document.getElementById('detailEventTitle').textContent = event.title;
            document.getElementById('detailEventStart').textContent = start;
            document.getElementById('detailEventEnd').textContent = end;

            // 모달 내부 데이터 채우기
            //     document.getElementById('detailEventTitle').textContent = event.title;
            //     document.getElementById('detailEventStart').textContent = event.startStr;
            //     document.getElementById('detailEventEnd').textContent = event.endStr || event.startStr;

            // 공휴일일 경우
            if (event.extendedProps.readOnly) {
                // 수정/삭제 버튼 숨김
                updateBtn.style.display = 'none';
                deleteBtn.style.display = 'none';
            } else {
                // 일반 일정일 경우 버튼 보이기
                updateBtn.style.display = 'inline-block';
                deleteBtn.style.display = 'inline-block';

                // 수정 버튼 동작 -- 없어도 작동 ok
                // updateBtn.onclick = function () {
                //     detailModal.hide();
                //     openEventModal(event);
                // };
            }

            // 일정 수정버튼 눌렀을 때
            document.getElementById('updateEventBtn').onclick = function () {
                isEditMode = true;
                editingEventId = event.id;

                // 상세 모달 닫기
                eventDetailModal.hide();
                // 일정 추가/수정 모달 열기
                eventModal.show();

                // 수정 모달에서는 삭제 버튼 표시
                document.getElementById('deleteEventInEditBtn').classList.remove('d-none');

                // 기존 데이터 세팅 value로 가져옴
                document.getElementById('eventTitle').value = event.title;
                document.getElementById('eventStart').value = start;
                document.getElementById('eventEnd').value = end;

                // 수정 모드에서 저장 버튼 클릭 시 PUT 요청
                // document.getElementById('saveEventBtn').onclick = function () {
                //     const updatedTitle = document.getElementById('eventTitle').value.trim();
                //     const updatedStart = document.getElementById('eventStart').value;
                //     const updatedEnd = document.getElementById('eventEnd').value;
                //
                //     if (!updatedTitle || !updatedStart || !updatedEnd) {
                //         alert("모든 정보를 입력해주세요.");
                //         return;
                //     }
                //
                //     fetch(`/api/calevents/${event.id}`, {
                //         method: 'PUT',
                //         headers: {'Content-Type': 'application/json'},
                //         body: JSON.stringify({
                //             title: updatedTitle,
                //             start: updatedStart,
                //             end: updatedEnd
                //         })
                //     })
                //         .then(res => {
                //             if (!res.ok) throw new Error('수정에 실패하였습니다.');
                //             return res.json();
                //         })
                //         .then(() => {
                //             detailModal.hide();
                //             alert('일정이 수정되었습니다.');
                //             calendar.refetchEvents();
                //         })
                //         .catch(err => alert(err));
                // };
            };

            // 일정을 삭제하는 코드
            document.getElementById('deleteEventInEditBtn').onclick = function () {
                if (!confirm('정말 삭제하시겠습니까?')) return;
                console.log('프론트에서 삭제할 ID:' + info.event.id);

                fetch(`/api/calevents/${editingEventId}`, {method: 'DELETE'})
                    .then(res => {
                        if (!res.ok) throw new Error('일정 삭제에 실패하였습니다.');
                        return res.text();
                    })
                    .then(() => {
                        ///////////////// hide() 실행 X
                        eventModal.hide();
                        alert('일정이 삭제되었습니다.');
                        calendar.refetchEvents(); // 서버 동기화
                    })
                    .catch(err => alert(err));
            }
            // 캘린더 클릭 이벤트의 기본 동작 방지 (페이지 이동 등)
            info.jsEvent.preventDefault()
        },
        customButtons: {
            addEventButton: {
                text: '일정 추가', click: () => {
                    openEventModal(); // 날짜 미선택
                }
            }
        },
        eventSources: [
            // Google API 공휴일 일정불러오는 코드
            {
                events: function (fetchInfo, successCallback, failureCallback) {
                    fetch('/api/calendar')
                        .then(res => res.json())
                        .then(data => {
                            const events = (data.items || []).map(item => ({
                                title: item.summary,
                                start: item.start.date || item.start.dateTime,
                                end: item.end.date || item.end.dateTime,
                                allDay: true,
                                extendedProps: {
                                    readOnly: true  // 읽기 전용 플래그
                                }
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
                failure: function () {
                    alert("이벤트 목록을 가져오다가 문제가 발생했습니다.");
                }
            }
        ]
    });
    calendar.render();

    // 일정 입력 모달창을 여는 함수
    function openEventModal(dateStr = '') {
        isEditMode = false;
        editingEventId = null;

        // 폼 초기화 및 종일일정 토글 기본값으로 설정
        document.getElementById('eventForm').reset();
        allDayCheck.checked = false;
        endGroup.style.display = 'block';
        // 일정 추가 모달에서는 삭제 버튼 숨김
        document.getElementById('deleteEventInEditBtn').classList.add('d-none');

        if (dateStr) {
            document.getElementById('eventStart').value = dateStr;
            document.getElementById('eventEnd').value = dateStr;
        }

        // 일정 입력 모달창 띄우기
        eventModal.show();
    }
    // // 현재 이부분이 중복?
    // const allDayCheck = document.getElementById('eventAllDay');
    // const endGroup = document.getElementById('endDateGroup');
    //
    // // 종일 체크시 종료일 입력 칸 비활성화
    // allDayCheck.addEventListener('change', function () {
    //     endGroup.style.display = allDayCheck.checked ? 'none' : 'block';
    // });

    // 저장 버튼 클릭 시 이벤트 등록
    document.getElementById('saveEventBtn').onclick = function () {
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

        // ---- 여기서 모드에 따라 다르게 요청 ----
        const url = isEditMode ? `/api/calevents/${editingEventId}` : '/api/calevents';
        const method = isEditMode ? 'PUT' : 'POST';

        fetch(url, {
            method: method,
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({title, start, end})
        })
            .then(res => {
                if (!res.ok) throw new Error('저장 실패');
                return res.json();
            })
            .then(() => {
                eventModal.hide();
                calendar.refetchEvents();

                alert(isEditMode ? '일정이 수정되었습니다!' : '일정이 추가되었습니다!');
            })
            .catch(err => alert(err));
        };
    });

        // 서버로 저장 EventController saveEvent 메서드
    //     fetch('/api/calevents', {
    //         method: 'POST',
    //         headers: {'Content-Type': 'application/json'},
    //         body: JSON.stringify({title, start, end})
    //     })
    //         .then(res => res.json())
    //         .then(data => {
    //             // reset()으로 입력 내용 초기화 + 모달 닫기
    //             document.getElementById('eventForm').reset();
    //             allDayCheck.checked = false;
    //             endGroup.style.display = 'block';
    //
    //             const eventModal = bootstrap.Modal.getInstance(document.getElementById('eventModal'));
    //             eventModal.hide();
    //
    //             // 서버에서 다시 이벤트 목록 새로 불러오기
    //             calendar.refetchEvents();
    //             alert('일정이 추가되었습니다!');
    //         })
    //         .catch(err => alert('저장 실패: ' + err));
    // });


/*
document.addEventListener('DOMContentLoaded', function () {
    const calendarEl = document.getElementById('calendar');
    if (!calendarEl) return;

    // 전역에서 쓰기 위해 window.calendar로 설정
    window.calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        // 기본 옵션인 날짜에 숫자+일에서 '일' 글자 지우는 옵션
        dayCellContent: arg => ({html: arg.dayNumberText.replace('일', '')}),
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
        // 날짜 클릭 시 그 날짜로 자동 입력 되면서 일정 추가 모달창 활성화
        dateClick: function (info) {
            openCreateModal(info.dateStr);
        },
        // 일정 클릭 시 일정 상세보기 표시 와 수정, 삭제버튼
        eventClick: function (info) {
            openDetailModal(info.event);
        },
        customButtons: {
            addEventButton: {
                text: '일정 추가', click: () => {
                    openCreateModal(); // 날짜 미선택
                }
            }
        },
        eventSources: [
            // Google API 공휴일 일정불러오는 코드
            {
                events: function (fetchInfo, successCallback, failureCallback) {
                    fetch('/api/calendar')
                        .then(res => res.json())
                        .then(data => {
                            const events = (data.items || []).map(item => ({
                                title: item.summary,
                                start: item.start.date || item.start.dateTime,
                                end: item.end.date || item.end.dateTime,
                                allDay: true,
                                extendedProps: {
                                    readOnly: true  // 읽기 전용 플래그
                                }
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
                failure: function () {
                    alert("이벤트 목록을 가져오다가 문제가 발생했습니다.");
                }
            }
        ]
    });
    calendar.render();
});
*/