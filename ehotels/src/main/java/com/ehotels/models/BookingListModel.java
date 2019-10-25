package com.ehotels.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BookingListModel extends RoomListModel {

    private int bookingID;
    private String startDate;
    private String endDate;
    private boolean paid;
    private int clientID;
    private String clientFirstName;
    private String clientLastName;
    private String ssn;
    
    public BookingListModel(int roomID, int roomNumber, int capacity, Float price, boolean extendable, String view,
            int hotelID, int starRating, int streetNumber, String streetName, String city, String state, String zip,
            int chainID, String chainName, int bookingID, String startDate, String endDate, boolean paid, int clientID, 
            String clientFirstName, String clientLastName, String ssn) {
        
        super(roomID, roomNumber, capacity, price, extendable, view, hotelID, starRating, streetNumber, streetName, city, state,
                zip, chainID, chainName);

        this.bookingID = bookingID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.paid = paid;
        this.clientID = clientID;
        this.clientFirstName = clientFirstName;
        this.clientLastName = clientLastName;
        this.ssn = ssn;
    }

    @XmlElement(name="bookingID")
    public int getBookingID() {
        return bookingID;
    }
    
    @XmlElement(name="startDate")
    public String getStartDate() {
        return startDate;
    }

    @XmlElement(name="endDate")
    public String getEndDate() {
        return endDate;
    }
    
    @XmlElement(name="paid")
    public boolean getPaidDate() {
        return paid;
    }
    
    @XmlElement(name="clientID")
    public int getClientID() {
        return clientID;
    }
    
    @XmlElement(name="clientFirstName")
    public String getClientFirstName() {
        return clientFirstName;
    }
    
    @XmlElement(name="clientLastName")
    public String getClientLastName() {
        return clientLastName;
    }
    
    @XmlElement(name="clientSSN")
    public String getSSN() {
        return ssn;
    }

}