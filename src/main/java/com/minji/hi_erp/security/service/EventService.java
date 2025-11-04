package com.minji.hi_erp.security.service;

import com.minji.hi_erp.security.dto.EventDto;
import com.minji.hi_erp.security.entity.Event;
import com.minji.hi_erp.security.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event saveEvent(EventDto dto) {
        Event event = Event.builder()
                .title(dto.getTitle())
                .startDate(dto.getStart())
                .endDate(dto.getEnd())
                .build();

        return eventRepository.save(event);
    }

    public List<Event> getAllEvent() {
        return eventRepository.findAll();
    }

    // 일정을 수정하는 메서드 입니다.
    public Event updateEvent(Long id, EventDto dto) {
        Event event = eventRepository.findById(id).
                orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다. ID: " + id));

        event.setTitle(dto.getTitle());
        event.setStartDate(dto.getStart());
        event.setEndDate(dto.getEnd() == null || dto.getEnd().isBlank() ? dto.getStart() : dto.getEnd());

        return eventRepository.save(event);
    }

    // 일정을 삭제하는 메서드 입니다.
    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("삭제할 일정이 존재하지 않습니다. ID: " + id);
        }
        eventRepository.deleteById(id);
    }
}
