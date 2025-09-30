package com.minji.hi_erp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiKeyController {

    private final String googleApiKey;

    public ApiKeyController(@Value("${google.api.key}") String googleApiKey) {
        this.googleApiKey = googleApiKey;
    }

    @GetMapping("/api/calendar")
    public String getGoogleApiKey() {
        return googleApiKey;
    }
}
