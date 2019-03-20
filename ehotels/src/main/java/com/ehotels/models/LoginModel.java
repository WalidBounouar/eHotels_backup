package com.ehotels.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class LoginModel {
    
    private String email;
    private String password;
    
    public LoginModel(@JsonProperty("email")String email, @JsonProperty("password")String password) {
        this.email = email;
        this.password = password;
    }
    
    @XmlElement(name="email")
    public String getUsername() {
        return this.email;
    }
    
    @XmlElement(name="password")
    public String getPassword() {
        return this.password;
    }
    
}