package com.minji.hi_erp.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDto {
    private String title;
    private String startDate; // yyyy-mm-dd 형식
    private String endDate;
}
