package com.ehotels.models;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientModel extends UserModel{
    
    private String registrationDate;
    
    public ClientModel(@JsonProperty("id") Integer id, 
            @JsonProperty("ssn")String ssn, @JsonProperty("lastName")String lastName, 
            @JsonProperty("middleName")String middleName, @JsonProperty("firstName")String firstName, 
            @JsonProperty("streetNumber")Integer streetNumber, @JsonProperty("streetName")String streetName, 
            @JsonProperty("city")String city, @JsonProperty("state")String state, 
            @JsonProperty("zip")String zip, @JsonProperty("registrationDate") String registrationDate) {
        
        super(id, ssn, lastName, middleName, firstName, streetNumber, streetName, city, state, zip);
        
        this.registrationDate = registrationDate;
    }
    
    @XmlElement(name="registrationDate")
    public String getRegistrationDate() {
        return registrationDate;
    }

}
