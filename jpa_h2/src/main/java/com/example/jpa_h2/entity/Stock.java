package com.example.jpa_h2.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "STOCKS")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Symbol", length = 10, nullable = false, unique = true)
    private String symbol;

    @Column(name = "longName", length = 100)
    private String longName;

    @Column(name = "Regular_Market_Price", nullable = false)
    private double regularMarketPrice;

    @Column(name = "Tradeable", nullable = false)
    private boolean tradeable;

}
