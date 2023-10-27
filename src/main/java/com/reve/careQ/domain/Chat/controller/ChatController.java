package com.reve.careQ.domain.Chat.controller;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Chat.entity.Chat;
import com.reve.careQ.domain.Chat.service.ChatService;
import com.reve.careQ.global.rq.Rq;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/members/chatrooms")
@RequiredArgsConstructor
public class ChatController {
    private final Rq rq;

    private final AdminService adminService;

    private final ChatService chatService;
    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public ModelAndView showChatrooms() {
        ModelAndView mv = new ModelAndView();

        mv.addObject("chatroomList", chatService.findByMemberId(rq.getMember().getId()));
        mv.setViewName("members/chatrooms");
        return mv;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/new-chatrooms")
    public String showNewchatrooms() {
        return "members/new-chatrooms";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/new-chatrooms")
    public String searchChatrooms(String subjectName, String hospitalName, Long memberId) {
        RsData<Admin> findAdminRs = adminService.findAdmin(subjectName,hospitalName);

        if (findAdminRs.isFail()) {
            return rq.historyBack(findAdminRs);
        }

        RsData<Chat> chatroomRs = chatService.insert(memberId,findAdminRs.getData().getId());

        return "redirect:/members/chatrooms/"+chatroomRs.getData().getId();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("{chatroom-id}")
    public String showChat(@PathVariable("chatroom-id") Long id){
        return "members/chat";
    }


}
