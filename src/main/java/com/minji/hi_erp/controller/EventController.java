package com.minji.hi_erp.controller;

import com.minji.hi_erp.security.dto.EventDto;
import com.minji.hi_erp.security.entity.Event;
import com.minji.hi_erp.security.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calevents")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public Event saveEvent(@RequestBody EventDto eventDto) {
        return eventService.saveEvent(eventDto);
    }

    @GetMapping
    public List<Event> getAllEvents(){
        return eventService.getAllEvent();
    }
}
