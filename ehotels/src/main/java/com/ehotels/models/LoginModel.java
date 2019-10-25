package com.ehotels.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.ehotels.enums.Permission;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class LoginModel {
    
    private String email;
    private String password;
    private Permission permission;
    
    public LoginModel(@JsonProperty("email")String email, @JsonProperty("password")String password, 
            @JsonProperty("permission")Permission permission) {
        this.email = email;
        this.password = password;
        this.permission = permission;
    }
    
    @XmlElement(name="email")
    public String getUsername() {
        return this.email;
    }
    
    @XmlElement(name="password")
    public String getPassword() {
        return this.password;
    }
    
    @XmlElement(name="permission")
    public Permission getPermission() {
        return this.permission;
    }
    
}