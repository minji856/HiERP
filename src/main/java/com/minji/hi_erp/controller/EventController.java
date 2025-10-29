package com.minji.hi_erp.controller;

import com.minji.hi_erp.security.dto.EventDto;
import com.minji.hi_erp.security.entity.Event;
import com.minji.hi_erp.security.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calevents")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public Event saveEvent(@RequestBody EventDto eventDto) {
        // end가 null 또는 빈 문자열이면 start로 자동 세팅 (종일 이벤트)
        if (eventDto.getEnd() == null || eventDto.getEnd().isBlank()) {
            eventDto.setEnd(eventDto.getStart());
        }

        return eventService.saveEvent(eventDto);
    }

//    @GetMapping
//    public List<Event> getAllEvents(){
//        return eventService.getAllEvent();
//    }
    @GetMapping
    public List<Map<String, String>> getAllEvents() {
        List<Event> events = eventService.getAllEvent();

        System.out.println("📅이벤트 개수: " + events.size());
        for (Event e : events) {
            System.out.println("➡ " + e.getTitle() + " / " + e.getStartDate() + " ~ " + e.getEndDate());
        }

        return events.stream().map(event -> {
            String start = event.getStartDate();
            String end = event.getEndDate();

            // end가 없거나 start와 같으면 종일 이벤트 → 그대로 전달
            if (end == null || end.equals(start)) {
                return Map.of(
                        "title", event.getTitle(),
                        "start", start
                );
            } else {
                // 기간 이벤트는 exclusive 방지 위해 +1일 처리
                return Map.of(
                        "title", event.getTitle(),
                        "start", start,
                        "end", LocalDate.parse(end).plusDays(1).toString()
                );
            }
        }).toList();
    }
}
