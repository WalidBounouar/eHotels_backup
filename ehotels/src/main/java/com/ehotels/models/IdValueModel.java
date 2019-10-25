package com.ehotels.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used when we want to add basic stuff.
 * - hotelid - phoneNumber
 * - roomid - amenity
 * - roomid - issueName
 */
@XmlRootElement
public class IdValueModel {

    private Integer ownerID;
    private String value;
    
    public IdValueModel(@JsonProperty("ownerID")Integer ownerID, 
            @JsonProperty("value")String value) {

        this.ownerID = ownerID;
        this.value = value;
    }

    @XmlElement(name="ownerID")
    public Integer getOwnerID() {
        return ownerID;
    }

    @XmlElement(name="value")
    public String getValue() {
        return value;
    }
    
}
