package com.reve.careQ.global.compositePKEntity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
@Embeddable
public class CompositePKEntity implements Serializable {
    private Long memberId;
    private Long adminId;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}