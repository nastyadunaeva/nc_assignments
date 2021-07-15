package com.example.jpa_h2.model;

public enum Permission {
    PERSON_READ("person:read"),
    PERSON_WRITE("person:write");

    private String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() { return permission; }
}
