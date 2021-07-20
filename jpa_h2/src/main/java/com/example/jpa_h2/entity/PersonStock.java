package com.example.jpa_h2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "PERSON_STOCK")
public class PersonStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Person_Id")
    private Long personId;

    //@Column(name = "Stock_Id")
    //private Long stockId;

    @Column(name = "Stock_Ticker")
    private String stockTicker;
}
