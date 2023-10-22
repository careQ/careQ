package com.reve.careQ.global.ApiKeyConfig;

public class ApiKeys {
    private String mapKey;
    private String pharmacyApiKey;
    private String hospitalApiKey;

    public ApiKeys(String mapKey, String pharmacyApiKey, String hospitalApiKey) {
        this.mapKey = mapKey;
        this.pharmacyApiKey = pharmacyApiKey;
        this.hospitalApiKey = hospitalApiKey;
    }

    public String getmapKey() {
        return mapKey;
    }

    public String getPharmacyApiKey() {
        return pharmacyApiKey;
    }

    public String getHospitalApiKey() {
        return hospitalApiKey;
    }

}
