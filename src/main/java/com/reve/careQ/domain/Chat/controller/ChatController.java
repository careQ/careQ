package com.reve.careQ.domain.Chat.controller;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Chat.entity.Chat;
import com.reve.careQ.domain.Chat.dto.ChatDto;
import com.reve.careQ.domain.Chat.service.ChatService;
import com.reve.careQ.domain.Message.service.MessageService;
import com.reve.careQ.global.rq.AdminRq;
import com.reve.careQ.global.rq.Rq;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/{user-type}/chatrooms")
@RequiredArgsConstructor
public class ChatController {
    private final Rq rq;

    private final AdminRq adminRq;

    private final AdminService adminService;

    private final ChatService chatService;

    private final MessageService messageService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public String showChatrooms(@PathVariable("user-type") String userType) {
        return userType+"/chatrooms";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(params = {"name"})
    @ResponseBody
    public List<ChatDto> getChatrooms(@PathVariable("user-type") String userType, @RequestParam(name="name",required=false,defaultValue="") String name) {
        List<Chat> chats;

        if (userType.equals("members")){
            chats = chatService.findByMemberId(rq.getMember().getId());
        }else{
            chats = chatService.selectChatByAdminIdAndMemberName(adminRq.getAdmin().getId(), name);
        }

        List<ChatDto> chatDto = chats.stream()
                .map(chat -> new ChatDto(chat.getId(), chat.getName(),chat.getMember().getUsername(), chat.getCreateDate(),
                        messageService.findLastMessage(chat.getId()).isPresent()?messageService.findLastMessage(chat.getId()).get():chat.getCreateDate(),
                        chat.getAdmin().getHospital().getName(),chat.getAdmin().getSubject().getName()))
                .collect(Collectors.toList());
        return chatDto;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/new-chatrooms")
    public String showNewchatrooms(@PathVariable("user-type") String userType) {
        return userType+"/new-chatrooms";
    }

    //채팅방 생성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/new-chatrooms")
    public String searchChatrooms(@PathVariable("user-type") String userType, String subjectName, String hospitalName, Long memberId) {
        RsData<Admin> findAdminRs = adminService.findAdmin(subjectName,hospitalName);

        if (findAdminRs.isFail()) {
            return rq.historyBack(findAdminRs);
        }

        RsData<Chat> chatroomRs = chatService.insert(memberId,findAdminRs.getData().getId());

        return "redirect:/"+userType+"/chatrooms/"+chatroomRs.getData().getId();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("{chatroom-id}")
    public ModelAndView showChat(@PathVariable("user-type") String userType, @PathVariable("chatroom-id") Long id){
        ModelAndView mv = new ModelAndView();

        mv.addObject("lastMessage",messageService.findLastMessage(id));
        mv.addObject("messages", messageService.findByChatId(id));
        mv.addObject("chatroom", chatService.findById(id).get());
        mv.setViewName(userType+"/chat");

        return mv;
    }

}