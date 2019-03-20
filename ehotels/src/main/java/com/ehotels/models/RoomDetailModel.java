package com.ehotels.models;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RoomDetailModel extends RoomListModel {

    private List<String> amenities;
    private List<String> issues;
    
    public RoomDetailModel(int roomID, int roomNumber, int capacity, Float price, boolean extendable, String view,
            int hotelID, int starRating, int streetNumber, String streetName, String city, String state, String zip,
            int chainID, String chainName, List<String> amenities, List<String> issues) {
        
        super(roomID, roomNumber, capacity, price, extendable, view, hotelID, starRating, streetNumber, streetName, city, state,
                zip, chainID, chainName);

        this.amenities = amenities;
        this.issues = issues;
    }

    @XmlElement(name="amenities")
    public List<String> getAmenities() {
        return amenities;
    }
    
    @XmlElement(name="issues")
    public List<String> getIssues() {
        return issues;
    }

}
