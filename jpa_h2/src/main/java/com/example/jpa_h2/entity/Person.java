package com.example.jpa_h2.entity;

import com.example.jpa_h2.model.Role;

import javax.persistence.*;

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
    
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public String getFirstName() {
        return firstName;
    }
 
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
    	return lastName;
    }
    
    public void setLastName(String lastName) {
    	this.lastName = lastName;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() {return password; }

    public void setPassword(String password) {this.password = password; }

    public Role getRole() { return role; }

    public void setRole(Role role) { this.role = role; }
 
}
