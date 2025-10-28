package com.minji.hi_erp.controller;

import com.minji.hi_erp.security.dto.EventDto;
import com.minji.hi_erp.security.entity.Event;
import com.minji.hi_erp.security.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calevents")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public Event saveEvent(@RequestBody EventDto eventDto) {
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

        return events.stream().map(event -> Map.of(
                "title", event.getTitle(),
                "start", event.getStartDate(),
                "end", event.getEndDate()
        )).toList();
    }
}
