package com.minji.hi_erp.security.service;

import com.minji.hi_erp.security.dto.EventDto;
import com.minji.hi_erp.security.entity.Event;
import com.minji.hi_erp.security.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event saveEvent(EventDto dto){
        Event event = Event.builder()
                .title(dto.getTitle())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        return eventRepository.save(event);
    }

    public List<Event> getAllEvent(){
        return eventRepository.findAll();
    }
}
