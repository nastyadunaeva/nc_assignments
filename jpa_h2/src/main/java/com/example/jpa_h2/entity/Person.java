package com.example.jpa_h2.entity;

import com.example.jpa_h2.model.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "PERSONS")
public class Person {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;
 
    @Column(name = "First_Name", length = 64, nullable = false)
    private String firstName;
    
    @Column(name="Last_Name", length = 64, nullable = false)
    private String lastName;

    @Column(name = "Username", length = 64, nullable = false, unique = true)
    private String username;

    @Column(name = "Password", length = 64, nullable = false)
    private String password;

    @Column(name = "Role", length = 64, nullable = false)
    private Role role = Role.USER;

    @ManyToMany(fetch = FetchType.EAGER)
    public Set<Stock> stocks;

 
}
