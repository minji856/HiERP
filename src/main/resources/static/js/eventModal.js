/*
// 일정 상세보기 모달 (읽기만 가능)
const detailModalEl = document.getElementById('eventDetailModal');
const detailModal = new bootstrap.Modal(detailModalEl);

// 일정 입력 모달
const eventModalEl = document.getElementById('eventModal');
const eventModal = new bootstrap.Modal(eventModalEl);

// 수정, 삭제 버튼 요소
const detailUpdateBtn = document.getElementById('detailUpdateBtn');
const detailDeleteBtn = document.getElementById('detailDeleteBtn');

const idInput = document.getElementById('modalEventId');
const titleInput = document.getElementById('eventTitle');
const startInput = document.getElementById('eventStart');
const endInput = document.getElementById('eventEnd');
const endGroup = document.getElementById('endDateGroup');
const allDayCheck = document.getElementById('eventAllDay');
// 수정 중 삭제버튼
const deleteBtnInEdit = document.getElementById('deleteEventInEditBtn');

let modalMode = 'create';
let currentEvent = null;


// 상세 모달 열기
function openDetailModal(event) {
    currentEvent = event;

    const start = event.startStr ?? event.start.toISOString().slice(0, 10);
    const end = event.endStr ?? (event.end ? event.end.toISOString().slice(0, 10) : start);

    document.getElementById('detailEventTitle').textContent = event.title;
    document.getElementById('detailEventStart').textContent = start;
    document.getElementById('detailEventEnd').textContent = end;

    // Google API readOnly 수정,삭제 버튼 숨기기
    if (event.extendedProps.readOnly) {
        detailUpdateBtn.style.display = 'none';
        detailDeleteBtn.style.display = 'none';
    } else {
        detailUpdateBtn.style.display = 'inline-block';
        detailDeleteBtn.style.display = 'inline-block';
    }

    detailModal.show();
}


// 등록 모달 열기
function openCreateModal(dateStr = '') {
    modalMode = 'create';

    idInput.value = '';
    titleInput.value = '';
    startInput.value = dateStr;
    endInput.value = dateStr;
    allDayCheck.checked = false;

    deleteBtnInEdit.classList.add('d-none');

    document.getElementById("eventModalLabel").textContent = "일정 등록";
    eventModal.show();
}


// 수정 모달 열기
function openUpdateModal(event) {
    modalMode = 'update';
    currentEvent = event;

    const start = event.startStr ?? event.start.toISOString().slice(0, 10);
    const end = event.endStr ?? (event.end ? event.end.toISOString().slice(0, 10) : start);

    idInput.value = event.id;
    titleInput.value = event.title;
    startInput.value = start;
    endInput.value = end;

    allDayCheck.checked = (start === end);
    deleteBtnInEdit.classList.remove('d-none');

    document.getElementById("eventModalLabel").textContent = "일정 수정";
    eventModal.show();
}


// 처음 일정 저장은 POST, 일정 수정 저장은 PUT (POST/PUT)
document.getElementById('saveEventBtn').addEventListener('click', async () => {
    const dto = {
        title: titleInput.value.trim(),
        start: startInput.value,
        end: allDayCheck.checked ? startInput.value : endInput.value
    };

    if (!dto.title || !dto.start) {
        alert("필수 값을 입력하세요!");
        return;
    }

    try {
        if (modalMode === 'create') {
            await fetch('/api/calevents', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(dto)
            });
        } else {
            await fetch(`/api/calevents/${idInput.value}`, {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(dto)
            });
        }

        eventModal.hide();
        calendar.refetchEvents();

    } catch (err) {
        alert("저장 중 오류 발생");
    }
});

/*
// 삭제 (상세보기)
detailDeleteBtn.addEventListener('click', async () => {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    await fetch(`/api/calevents/${currentEvent.id}`, {method: 'DELETE'});
    detailModal.hide();
    calendar.refetchEvents();
});


// 삭제 (수정 모달)
deleteBtnInEdit.addEventListener('click', async () => {
    if (!confirm("삭제할까요?")) return;

    await fetch(`/api/calevents/${idInput.value}`, {method: 'DELETE'});
    eventModal.hide();
    calendar.refetchEvents();
});

// 삭제 (상세보기 + 수정 모달 공통 처리)
async function deleteEventById(eventId) {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    await fetch(`/api/calevents/${eventId}`, {method: 'DELETE'});

    detailModal.hide();
    eventModal.hide();

    window.calendar.refetchEvents();
}

    detailDeleteBtn.addEventListener('click', () => deleteEventById(currentEvent.id));
    deleteBtnInEdit.addEventListener('click', () => deleteEventById(idInput.value));

    // 종일 일정 토글 처리
    allDayCheck.addEventListener('change', function () {
        endGroup.style.display = allDayCheck.checked ? 'none' : 'block';
    });

window.openCreateModal = openCreateModal;
window.openDetailModal = openDetailModal;
window.openUpdateModal = openUpdateModal;
*/