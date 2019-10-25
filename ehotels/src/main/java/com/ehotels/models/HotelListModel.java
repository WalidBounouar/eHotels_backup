package com.ehotels.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HotelListModel {
    
    private int hotelID;
    private int streetNumber;
    private String streetName;
    private String city;
    private String state;
    private String zip;
    private int chainID;
    private String chainName;
    
    public HotelListModel(int hotelID, int streetNumber, String streetName, 
            String city, String state, String zip, int chainID, String chainName) {

        this.hotelID = hotelID;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.chainID = chainID;
        this.chainName = chainName;
    }
    
    @XmlElement(name="hotelID")
    public int getHotelID() {
        return hotelID;
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

    @XmlElement(name="chainID")
    public int getChainID() {
        return chainID;
    }

    @XmlElement(name="chainName")
    public String getChainName() {
        return chainName;
    }

}
