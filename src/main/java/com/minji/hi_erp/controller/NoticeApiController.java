package com.minji.hi_erp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeApiController {

    private final StringRedisTemplate redisTemplate;

    @GetMapping("/view-counts")
    public Map<Long, Integer> getLatestViewCounts(@RequestParam List<Long> ids){
        Map<Long, Integer> viewCounts = new HashMap<>();

        for (Long id : ids) {
            String viewKey = "notice:viewCount:" + id;
            String countStr = redisTemplate.opsForValue().get(viewKey);

            // 만약 Redis에 데이터가 없으면 우선 0 혹은 필요시 DB 기본값을 반환하도록 세팅
            int count = (countStr == null) ? 0 : Integer.parseInt(countStr);
            viewCounts.put(id, count);
        }

        return viewCounts; // 예: { 5: 120, 6: 45 } 형태의 JSON 반환
    }
}

