package com.reve.careQ.domain.Pharmacy.entity;

import com.reve.careQ.domain.Pharmacy.dto.PharmacyDto;
import com.reve.careQ.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Pharmacy extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "wgs84Lat")
    private double wgs84Lat;

    @Column(name = "wgs84Lon")
    private double wgs84Lon;

    @Column(name = "state")
    private String state;

    @Column(name = "city")
    private String city;

    @Column(name = "phone")
    private String phone;

    public PharmacyDto toResponse() {
        return PharmacyDto.builder()
                .id(getId())
                .name(name)
                .address(address)
                .wgs84Lat(wgs84Lat)
                .wgs84Lon(wgs84Lon)
                .state(state)
                .city(city)
                .phone(phone)
                .build();
    }
}
