package com.example.jpa_h2.entity;

import javax.persistence.*;

@Entity
@Table(name = "NUMBERS")
public class NumberInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "Number", nullable = false)
    private int number;

    @Column(name = "Info", length = 200, nullable = false)
    private String info;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumber() { return number; }

    public void setNumber(int number) {this.number = number;}

    public String getInfo() {return info;}

    public void setInfo(String info) {this.info = info;}
}
