package com.reve.careQ.domain.Hospital.dto;

public class HospitalDto {
    private Long id;

    private String code;

    private String name;

    public HospitalDto(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public Long getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}

