package com.minji.hi_erp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class ApiKeyController {

    private final String googleApiKey;
    private final RestTemplate restTemplate = new RestTemplate();

    // 한국 공휴일 캘린더 ID (정확히 인코딩된 형태)
    private static final String KO_HOLIDAY_CALENDAR_ID = "ko.south_korea#holiday@group.v.calendar.google.com";

    public ApiKeyController(@Value("${google.api.key}") String googleApiKey) {
        this.googleApiKey = googleApiKey;
    }

    @GetMapping("/api/calendar")
    public ResponseEntity<?> getHolidayApiKey() {
        try {
            String url = "https://www.googleapis.com/calendar/v3/calendars/{calendarId}/events?key={apiKey}";

            Map<String, String> uriVariables = Map.of(
                    "calendarId", KO_HOLIDAY_CALENDAR_ID,
                    "apiKey", googleApiKey
            );

            Map response = restTemplate.getForObject(url, Map.class, uriVariables);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
