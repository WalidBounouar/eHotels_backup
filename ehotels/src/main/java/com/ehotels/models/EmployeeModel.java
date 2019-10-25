package com.ehotels.models;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmployeeModel extends UserModel {
    
    private List<String> roles;
    
    public EmployeeModel(@JsonProperty("id") int id, 
            @JsonProperty("ssn")String ssn, @JsonProperty("lastName")String lastName, 
            @JsonProperty("middleName")String middleName, @JsonProperty("firstName")String firstName, 
            @JsonProperty("streetNumber")int streetNumber, @JsonProperty("streetName")String streetName, 
            @JsonProperty("city")String city, @JsonProperty("state")String state, 
            @JsonProperty("zip")String zip, @JsonProperty("roles")List<String> roles) {
        
        super(id, ssn, lastName, middleName, firstName, streetNumber, streetName, city, state, zip);
        
        this.roles = roles;
    }
    
    @XmlElement(name="roles")
    public List<String> getRoles() {
        return roles;
    }

}
