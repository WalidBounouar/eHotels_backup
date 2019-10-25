package com.ehotels.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RoomsPerAreaModel {
    
    private int numberOfRooms;
    private String city;
    private String state;
    
    
    public RoomsPerAreaModel(int numberOfRooms, String city, String state) {

        this.numberOfRooms = numberOfRooms;
        this.city = city;
        this.state = state;
    }

    @XmlElement(name="numberOfRooms")
    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    @XmlElement(name="city")
    public String getCity() {
        return city;
    }

    @XmlElement(name="state")
    public String getState() {
        return state;
    }    

}
