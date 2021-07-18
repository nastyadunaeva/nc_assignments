package com.example.jpa_h2.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
public class Cat {
    @Id
    private Long id;

    private Long length;
    private Long weight;
    private String name;
}
