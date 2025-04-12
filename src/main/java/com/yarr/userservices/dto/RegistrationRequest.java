package com.yarr.userservices.dto;

import java.util.List;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String email;
    private String contact;
    private List<String> roles;
    private String provider;
    private String type;
    private String password;

    public RegistrationRequest() {
    }

    public RegistrationRequest(String email, String contact, List<String> roles, String provider, String type, String password) {
        this.email = email;
        this.contact = contact;
        this.roles = roles;
        this.provider = provider;
        this.type = type;
        this.password = password;
    }
}
