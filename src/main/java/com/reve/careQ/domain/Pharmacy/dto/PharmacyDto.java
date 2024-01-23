package com.reve.careQ.domain.Pharmacy.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyDto {

    private Long id;
    private String name;
    private String phone;
    private String address;
    private double wgs84Lat;
    private double wgs84Lon;
    private String state;
    private String city;
}
