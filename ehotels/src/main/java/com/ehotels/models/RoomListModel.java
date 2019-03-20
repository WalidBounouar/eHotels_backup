package com.ehotels.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RoomListModel {
    
    /*
     * Kept the date as String so that we can check valid format ourselves
     */
    private int roomID;
    private int roomNumber;
    private int capacity;
    private Float price;
    private boolean extendable;
    private String view;
    private int hotelID;
    private int starRating;
    private int streetNumber;
    private String streetName;
    private String city;
    private String state;
    private String zip;
    private int chainID;
    private String chainName;
    
    public RoomListModel(int roomID, int roomNumber, int capacity, Float price, boolean extendable, String view,
            int hotelID, int starRating, int streetNumber, String streetName, String city, String state, String zip,
            int chainID, String chainName) {

        this.roomID = roomID;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.price = price;
        this.extendable = extendable;
        this.view = view;
        this.hotelID = hotelID;
        this.starRating = starRating;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.chainID = chainID;
        this.chainName = chainName;
    }
    
    @XmlElement(name="roomID")
    public int getRoomID() {
        return roomID;
    }

    @XmlElement(name="roomNumber")
    public int getRoomNumber() {
        return roomNumber;
    }

    @XmlElement(name="capacity")
    public int getCapacity() {
        return capacity;
    }

    @XmlElement(name="price")
    public Float getPrice() {
        return price;
    }

    @XmlElement(name="extendable")
    public boolean isExtendable() {
        return extendable;
    }

    @XmlElement(name="view")
    public String getView() {
        return view;
    }

    @XmlElement(name="hotelID")
    public int getHotelID() {
        return hotelID;
    }

    @XmlElement(name="starRating")
    public int getStarRating() {
        return starRating;
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
