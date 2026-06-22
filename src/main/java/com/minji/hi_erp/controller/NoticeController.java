package com.minji.hi_erp.controller;

import com.minji.hi_erp.dto.NoticeRequestDto;
import com.minji.hi_erp.dto.NoticeResponseDto;
import com.minji.hi_erp.entity.Users;
import com.minji.hi_erp.service.CustomUserDetails;
import com.minji.hi_erp.service.NoticeService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("")
    public String list(Model model, @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        //Page<Notice> noticeList = noticeService.findAll(pageable);
        Page<NoticeResponseDto> noticeList = noticeService.getNoticeList(pageable);

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
    public String detail(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model){
        // NoticeResponseDto responseDto = noticeService.findById(id);
        // 유저 엔티티나 세션에서 id를 꺼냅니다.
        Long userId = userDetails.getUsers().getId();

        NoticeResponseDto responseDto = noticeService.getNotice(id, userId);

        model.addAttribute("notice", responseDto);

        return "board/detail";
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
