package com.minji.hi_erp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiKeyController {

    @Value("${google.api.key}")
    private String googleApiKey;
}
