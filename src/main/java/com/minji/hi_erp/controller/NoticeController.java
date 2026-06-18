package com.minji.hi_erp.controller;

import com.minji.hi_erp.dto.NoticeRequestDto;
import com.minji.hi_erp.dto.NoticeResponseDto;
import com.minji.hi_erp.entity.Notice;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.service.CustomUserDetails;
import com.minji.hi_erp.service.NoticeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("")
    public String list(Model model, @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Notice> noticeList = noticeService.findAll(pageable);
        model.addAttribute("notices", noticeList);
        return "board/notice";
    }

    // 글쓰기 페이지 이동 (GET)
    @GetMapping("/write")
    //@PreAuthorize("hasRole('ADMIN')") // 서버 사이드에서도 한 번 더 권한 체크
    public String writeForm() {
        return "board/write";
    }

    // 글쓰기 저장
    @PostMapping("/write")
    public String write(@Valid NoticeRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails customUserDetails, RedirectAttributes rattr) {
        try {
            // 로그인한 사용자 엔티티 가져오기
            Users author = customUserDetails.getUsers();
            noticeService.save(requestDto, author);

            rattr.addFlashAttribute("successMessage", "게시글이 등록되었습니다.");
            return "redirect:/notice";
        } catch (Exception e) {
            rattr.addFlashAttribute("errorMessage", "등록 중 오류가 발생했습니다.");
            return "redirect:/write";
        }
    }

    /**
     * 공지사항 상세보기
     *
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpServletRequest  request, HttpServletResponse response, Model model){
        // 브라우저가 보낸 쿠키들 중에서 "noticeView"라는 이름의 쿠키가 있는지 찾는다.
        Cookie[] cookies = request.getCookies();
        System.out.println(Arrays.toString(cookies));

        // 쿠키 이름 저장을 위한 빈 객체 생성
        Cookie oldCookie = null;

        long secondsUntilMidnight = getSecondsUntilMidnight();

        if (cookies != null) {
            for (Cookie cookie : cookies){
                if (cookie.getName().equals("noticeView")){
                    oldCookie = cookie;
                    break;
                }
            }
        }

        // noticeView 쿠키가 존재 할 때 (이미 읽었을 때)
        if (oldCookie != null){
            // 현재 게시글 ID가 쿠키 값에 포함되어 있지 않은지 확인합니다. (예: "[1][3][5]")
            if (!oldCookie.getValue().contains("[" + id + "]")){
                // 조회수 증가
                //noticeService.increaseViewCount(id);
                noticeService.increasViewCountatDB(id);
                // 쿠키 값 뒤에 현재 글 ID를 이어 붙입니다. (예: "[1][3]" -> "[1][3][5]")
                oldCookie.setValue(oldCookie.getValue() + "[" + id + "]");
                oldCookie.setPath("/");
                //oldCookie.setMaxAge(60 * 60 * 24);
                oldCookie.setMaxAge((int) secondsUntilMidnight);
                response.addCookie(oldCookie);
            }
        }

        // 게시판을 처음 보는 사람
        else {
            //noticeService.increaseViewCount(id);
            noticeService.increasViewCountatDB(id);
            Cookie newCookie = new Cookie("noticeView", "[" + id + "]");
            newCookie.setPath("/"); // 모든 경로에서 접근 가능
            // newCookie.setMaxAge(60 * 60 * 24);
            newCookie.setMaxAge((int) secondsUntilMidnight);
            response.addCookie(newCookie);
        }

        NoticeResponseDto responseDto = noticeService.findById(id);
        model.addAttribute("notice", responseDto);

        return "board/detail";
   }

    /**
     * 현재 시간부터 오늘 밤 12시 직전까지 남은 시간을 초(Seconds)로 계산하는 메서드
     */
    private long getSecondsUntilMidnight() {
        // LocalDateTime now = LocalDateTime.now();
        // 서버 위치와 상관없이 강제로 한국 시간대(Asia/Seoul)의 현재 시간을 가져옴
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        // 오늘 날짜의 맨 마지막 시간 (23시 59분 59초)
        LocalDateTime endOfDay = now.toLocalDate().atTime(LocalTime.MAX);

        return Duration.between(now, endOfDay).getSeconds();
    }

    @GetMapping("/delete/{id}") // 경로 변수(PathVariable) 방식 추천
    public String delete(@PathVariable Long id, RedirectAttributes rattr) {
        try {
            noticeService.delete(id);
            rattr.addFlashAttribute("successMessage", "공지사항이 삭제되었습니다.");
        } catch (Exception e) {
            rattr.addFlashAttribute("errorMessage", "삭제 중 오류가 발생했습니다.");
        }
        // 삭제 후에는 목록 페이지로 돌아가야겠죠?
        return "redirect:/notice";
    }
}
