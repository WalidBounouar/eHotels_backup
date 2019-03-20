package com.ehotels.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class RegisterModel {

    private String email;
    private String password;
    
    private String ssn;
    private String lastName;
    private String middleName;
    private String firstName;
    private int streetNumber;
    private String streetName;
    private String city;
    private String state;
    private String zip;
    
    public RegisterModel(@JsonProperty("email")String email, @JsonProperty("password")String password, 
            @JsonProperty("ssn")String ssn, @JsonProperty("lastName")String lastName, 
            @JsonProperty("middleName")String middleName, @JsonProperty("firstName")String firstName, 
            @JsonProperty("streetNumber")int streetNumber, @JsonProperty("streetName")String streetName, 
            @JsonProperty("city")String city, @JsonProperty("state")String state, 
            @JsonProperty("zip")String zip) {

        this.email = email;
        this.password = password;
        this.ssn = ssn;
        this.lastName = lastName;
        this.middleName = middleName;
        this.firstName = firstName;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    @XmlElement(name="email")
    public String getEmail() {
        return email;
    }

    @XmlElement(name="password")
    public String getPassword() {
        return password;
    }

    @XmlElement(name="ssn")
    public String getSsn() {
        return ssn;
    }

    @XmlElement(name="lastName")
    public String getLastName() {
        return lastName;
    }

    @XmlElement(name="middleName")
    public String getMiddleName() {
        return middleName;
    }

    @XmlElement(name="firstName")
    public String getFirstName() {
        return firstName;
    }

    @XmlElement(name="streetNumber")
    public int getStreetNumber() {
        return streetNumber;
    }

    @XmlElement(name="streetName")
    public String getStreetName() {
        return streetName;
    }

    @XmlElement(name="city")
    public String getCity() {
        return city;
    }

    @XmlElement(name="state")
    public String getState() {
        return state;
    }

    @XmlElement(name="zip")
    public String getZip() {
        return zip;
    }
    
}
