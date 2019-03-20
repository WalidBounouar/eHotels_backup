package com.ehotels.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AuthErrorModel {
    
    private String error;

    public AuthErrorModel(String error) {
        this.error = error;
    }

    @XmlElement(name="error")
    public String getError() {
        return error;
    }
    
}