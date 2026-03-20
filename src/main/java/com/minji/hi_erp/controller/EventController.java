package com.minji.hi_erp.controller;

import com.minji.hi_erp.dto.EventDto;
import com.minji.hi_erp.entity.Event;
import com.minji.hi_erp.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calevents")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * 새로운 이벤트를 저장하는 메서드 입니다.
     *
     * 전달받은 {@link EventDto} 객체를 기반으로 {@link Event} 엔티티를 생성하고
     * DB에 저장합니다. 만약 종료일(end)이 null이거나 빈 문자열이면
     * 시작일(start)과 동일하게 설정하여 종일 이벤트로 처리합니다.
     *
     * @param eventDto 저장할 이벤트 정보를 담은 DTO 객체
     * @return 저장된 {@link Event} 엔티티
     */
    @PostMapping
    public Event saveEvent(@RequestBody EventDto eventDto) {
        // end가 null 또는 빈 문자열이면 start로 자동 세팅 (종일 이벤트)
        if (eventDto.getEnd() == null || eventDto.getEnd().isBlank()) {
            eventDto.setEnd(eventDto.getStart());
        }

        return eventService.saveEvent(eventDto);
    }

    /**
     * DB에 저장된 모든 이벤트를 조회하여 FullCalendar에서 사용할 수 있는 형태로 반환합니다.
     * 각 이벤트는 {@link EventService}를 통해 DB에서 조회됩니다.
     *
     * 종료일(end)이 null이거나 시작일과 동일하면 종일 이벤트로 처리하며,
     * 기간 이벤트의 경우 FullCalendar의 exclusive 특성을 고려하여 종료일에 1일을 더해 반환합니다.
     *
     * @return FullCalendar에서 사용할 수 있는 이벤트 리스트
     */
    @GetMapping
    public List<Map<String, String>> getAllEvents() {
        List<Event> events = eventService.getAllEvent();

        System.out.println("이벤트 개수: " + events.size());
        for (Event e : events) {
            System.out.println("➡ " + e.getTitle() + " / " + e.getStartDate() + " ~ " + e.getEndDate());
        }

        return events.stream().map(event -> {
            String start = event.getStartDate();
            String end = event.getEndDate();

            // end가 없거나 start와 같으면 종일 이벤트 → 그대로 전달
            if (end == null || end.equals(start)) {
                return Map.of(
                        "id", String.valueOf(event.getId()),
                        "title", event.getTitle(),
                        "start", start
                );
            } else {
                // 기간 이벤트는 exclusive 방지 위해 +1일 처리
                return Map.of(
                        "id", String.valueOf(event.getId()),
                        "title", event.getTitle(),
                        "start", start,
                        "end", LocalDate.parse(end).plusDays(1).toString()
                );
            }
        }).toList();
    }

    /**
     * 일정 수정
     * @param id 수정할 일정의 ID
     * @param eventDto 수정할 일정 정보
     * @return 수정된 일정 객체
     */
    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody EventDto eventDto) {
        System.out.println(eventDto + "updateEvent Put 메서드 실행");
        return eventService.updateEvent(id, eventDto);
    }

    /**
     * 일정 삭제
     * @param id 삭제할 일정의 ID
     * @return 삭제 완료 메시지
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id) {
        System.out.println("DELETE 요청 들어옴 : " + id);
        eventService.deleteEvent(id);
        return ResponseEntity.ok("Deleted");
    }
}
