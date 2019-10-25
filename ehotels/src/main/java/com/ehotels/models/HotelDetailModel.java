package com.ehotels.models;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class HotelDetailModel {
    
    private Integer hotelID;
    private Integer managerID;
    private Integer starRating;
    private Integer streetNumber;
    private String streetName;
    private String city;
    private String state;
    private String zip;
    private Integer chainID;
    private String chainName;
    private List<String> phoneNumbers;
    
    public HotelDetailModel(@JsonProperty("hotelID") Integer hotelID, @JsonProperty("managerID") Integer managerID, 
            @JsonProperty("starRating") Integer starRating, @JsonProperty("streetNumber") Integer streetNumber, 
            @JsonProperty("streetName") String streetName, @JsonProperty("city") String city, 
            @JsonProperty("state") String state, @JsonProperty("zip") String zip, 
            @JsonProperty("chainID") Integer chainID, @JsonProperty("chainName") String chainName, 
            @JsonProperty("phoneNumber") List<String> phoneNumber) {

        this.hotelID = hotelID;
        this.managerID = managerID;
        this.starRating = starRating;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.chainID = chainID;
        this.chainName = chainName;
        this.phoneNumbers = phoneNumber;
    }

    @XmlElement(name="hotelID")
    public Integer getHotelID() {
        return hotelID;
    }
    
    @XmlElement(name="managerID")
    public Integer getManagerID() {
        return managerID;
    }
    
    @XmlElement(name="starRating")
    public Integer getStarRating() {
        return starRating;
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

    @XmlElement(name="chainID")
    public Integer getChainID() {
        return chainID;
    }

    @XmlElement(name="chainName")
    public String getChainName() {
        return chainName;
    }
    
    @XmlElement(name="phoneNumbers")
    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

}
