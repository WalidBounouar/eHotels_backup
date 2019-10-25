package com.ehotels.models;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserModel {
    
    private Integer id;
    private String ssn;
    private String lastName;
    private String middleName;
    private String firstName;
    private Integer streetNumber;
    private String streetName;
    private String city;
    private String state;
    private String zip;
    
    public UserModel(@JsonProperty("id") Integer id, 
            @JsonProperty("ssn")String ssn, @JsonProperty("lastName")String lastName, 
            @JsonProperty("middleName")String middleName, @JsonProperty("firstName")String firstName, 
            @JsonProperty("streetNumber")Integer streetNumber, @JsonProperty("streetName")String streetName, 
            @JsonProperty("city")String city, @JsonProperty("state")String state, 
            @JsonProperty("zip")String zip) {

        this.id = id;
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

    @XmlElement(name="id")
    public Integer getID() {
        return id;
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
    public Integer getStreetNumber() {
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
