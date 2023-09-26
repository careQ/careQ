package com.reve.careQ.global.compositePKEntity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class CompositePKEntity implements Serializable {

    private Long id;

    private Long hosp_sub_id;

}
