package com.minji.hi_erp.controller;

import com.minji.hi_erp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ValidController {

    private final UserService userService;

    @ResponseBody
    public boolean isEmailDuplicated(String email) {
        // true/false만 반환
        return userService.isEmailDuplicate(email);
    }
}
