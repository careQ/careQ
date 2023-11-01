package com.reve.careQ.domain.Chat.entity;

import java.time.LocalDateTime;

public class ChatDto {
    private Long id;

    private String name;

    private String memberUsername;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    private String hospitalName;

    private String subjectName;

    public ChatDto(Long id, String name, String memberUsername, LocalDateTime createDate, LocalDateTime modifyDate, String hospitalName, String subjectName){
        this.id = id;
        this.name = name;
        this.memberUsername = memberUsername;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.hospitalName = hospitalName;
        this.subjectName = subjectName;
    }

    public Long getId(){
        return id;
    }
    public String getName() {return name;}
    public String getMemberUsername() {return memberUsername;}
    public LocalDateTime getCreateDate(){ return createDate; }
    public LocalDateTime getModifyDate(){
        return modifyDate;
    }
    public String getHospitalName() {return hospitalName;}
    public String getSubjectName() {return subjectName;}
}
