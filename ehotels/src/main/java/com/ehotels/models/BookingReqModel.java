package com.ehotels.models;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookingReqModel {

    private int roomID;
    private String startDate;
    private String endDate;
    private Integer clientID;
    
    public BookingReqModel(@JsonProperty("roomID")int roomID, @JsonProperty("startDate")String startDate, 
            @JsonProperty("endDate")String endDate, @JsonProperty("clientID") Integer clientID) {

        this.roomID = roomID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.clientID = clientID;

    }

    @XmlElement(name="roomID")
    public int getRoomID() {
        return roomID;
    }

    @XmlElement(name="startDate")
    public String getStartDate() {
        return startDate;
    }

    @XmlElement(name="endDate")
    public String getEndDate() {
        return endDate;
    }
    
    @XmlElement(name="clientID")
    public Integer getClientID() {
        return clientID;
    }
    
    
}
