package com.minji.hi_erp.controller;

import com.minji.hi_erp.entity.Notice;
import com.minji.hi_erp.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final NoticeService noticeService;

    @GetMapping("/notice")
    public String list(Model model, @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Notice> noticeList = noticeService.findAll(pageable);
        model.addAttribute("notices", noticeList);
        return "board/notice";
    }

//    @PostMapping("/notice/write")
//    public String write(NoticeRequestDto requestDto, @AuthenticationPrincipal PrincipalDetails principalDetails, RedirectAttributes rattr) {
//        try {
//            // 로그인한 사용자 엔티티 가져오기
//            Users author = principalDetails.getUser();
//
//            // 엔티티 생성 (빌더 패턴 권장)
//            Notice notice = Notice.builder()
//                    .title(requestDto.getTitle())
//                    .content(requestDto.getContent())
//                    .author(author)
//                    .build();
//
//            noticeService.save(notice);
//            rattr.addFlashAttribute("successMessage", "공지사항이 등록되었습니다.");
//            return "redirect:/notice/list";
//        } catch (Exception e) {
//            rattr.addFlashAttribute("errorMessage", "등록 중 오류가 발생했습니다.");
//            return "redirect:/notice/write";
//        }
//    }

    // 글쓰기 페이지 이동 (GET)
    @GetMapping("/notice/write")
    @PreAuthorize("hasRole('ADMIN')") // 서버 사이드에서도 한 번 더 권한 체크
    public String writeForm() {
        return "board/write";
    }
}
