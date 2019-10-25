package com.ehotels.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class RoomBasicModel {
    
    private Integer roomID;
    private Integer hotelID;
    private Integer roomNumber;
    private Integer capacity;
    private Float price;
    private Boolean extendable;
    private String view;
    
    public RoomBasicModel(@JsonProperty("roomID") Integer roomID, @JsonProperty("hotelID") Integer hotelID, 
            @JsonProperty("roomNumber") Integer roomNumber, @JsonProperty("capacity") Integer capacity, 
            @JsonProperty("price") Float price, @JsonProperty("extendable") Boolean extendable, 
            @JsonProperty("view") String view) {

        this.roomID = roomID;
        this.hotelID = hotelID;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.price = price;
        this.extendable = extendable;
        this.view = view;
    }

    @XmlElement(name="roomID")
    public Integer getRoomID() {
        return roomID;
    }
    
    @XmlElement(name="hotelID")
    public Integer getHotelID() {
        return hotelID;
    }

    @XmlElement(name="roomNumber")
    public Integer getRoomNumber() {
        return roomNumber;
    }

    @XmlElement(name="capacity")
    public Integer getCapacity() {
        return capacity;
    }

    @XmlElement(name="price")
    public Float getPrice() {
        return price;
    }

    @XmlElement(name="extendable")
    public Boolean getExtendable() {
        return extendable;
    }

    @XmlElement(name="view")
    public String getView() {
        return view;
    }

}
