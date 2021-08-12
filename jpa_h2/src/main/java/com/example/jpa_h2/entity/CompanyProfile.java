package com.example.jpa_h2.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyProfile {
    private String country;
    private String currency;
    private String exchange;
    private String ipo;
    private Long marketCapitalization;
    private String name;
    private String phone;
    private double shareOutstanding;
    private String ticker;
    private String weburl;
    private String logo;
    private String finnhubIndustry;
}
