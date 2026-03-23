package com.minji.hi_erp;

import com.minji.hi_erp.entity.Event;
import com.minji.hi_erp.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EventDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    private Long saveEventId;

    @BeforeEach
    void setUp(){
        // 테스트용 이벤트 저장
        Event event = Event.builder()
                .title("삭제될 일정")
                .startDate("2025-10-31")
                .endDate("2025-10-31")
                .build();
        saveEventId = eventRepository.save(event).getId();
    }

    @Test
    @DisplayName("이벤트 삭제 성공 테스트")
    void DeleteEventTest() throws Exception {
        mockMvc.perform(delete("/api/calevents/{id}", saveEventId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        boolean exist = eventRepository.existsById(saveEventId);
        assertThat(exist).isFalse();
    }

    @Test
    @DisplayName("없는 이벤트 삭제 시 404 반환")
    void deleteEvent_notFound() throws Exception {
        Long fakeId = 999999L;

        mockMvc.perform(delete("/api/calevents/{id}", fakeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
