package com.minji.hi_erp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class ApiKeyController {

    private final String googleApiKey;
    private final String KoHolidayCalendarId = "ko.south_korea#holiday@group.v.calendar.google.com";

    public ApiKeyController(@Value("${google.api.key}") String googleApiKey) {
        this.googleApiKey = googleApiKey;
    }

    @GetMapping("/api/calendar")
    public ResponseEntity<?> getHolidayApiKey() {
        String url = "https://www.googleapis.com/calendar/v3/calendars/"
                + KoHolidayCalendarId
                + "/events?key=" + googleApiKey;
        RestTemplate restTemplate = new RestTemplate();
        Map response = restTemplate.getForObject(url, Map.class);

        return ResponseEntity.ok(response);
    }
}
