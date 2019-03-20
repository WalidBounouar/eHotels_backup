package com.ehotels.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class RoomSearchModel {
    
    /*
     * Kept the date as String so that we can check valid format ourselves
     */
    private String startDate;
    private String endDate;
    private Integer minCapacity;
    private Integer maxCapacity;
    private String town;
    private String state;
    private String hotelChain;
    private Integer minRating;
    private Integer maxRating;
    private Integer minNumberRooms;
    private Integer maxNumberRooms;
    private Float minPrice;
    private Float maxPrice;
    
    public RoomSearchModel(@JsonProperty("startDate")String startDate, 
            @JsonProperty("endDate")String endDate, @JsonProperty("minCapacity")Integer minCapacity, 
            @JsonProperty("maxCapacity")Integer maxCapacity, @JsonProperty("town")String town,
            @JsonProperty("state")String state, @JsonProperty("hotelChain")String hotelChain, 
            @JsonProperty("minRating")Integer minRating, @JsonProperty("maxRating")Integer maxRating, 
            @JsonProperty("minNumberRooms")Integer minNumberRooms, 
            @JsonProperty("maxNumberRooms")Integer maxNumberRooms, 
            @JsonProperty("minPrice")Float minPrice, @JsonProperty("maxPrice")Float maxPrice) {

        this.startDate = startDate;
        this.endDate = endDate;
        this.minCapacity = minCapacity;
        this.maxCapacity = maxCapacity;
        this.town = town;
        this.state = state;
        this.hotelChain = hotelChain;
        this.minRating = minRating;
        this.maxRating = maxRating;
        this.minNumberRooms = minNumberRooms;
        this.maxNumberRooms = maxNumberRooms;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    @XmlElement(name="startDate")
    public String getStartDate() {
        return startDate;
    }

    @XmlElement(name="endDate")
    public String getEndDate() {
        return endDate;
    }

    @XmlElement(name="minCapacity")
    public Integer getMinCapacity() {
        return minCapacity;
    }

    @XmlElement(name="maxCapacity")
    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    @XmlElement(name="town")
    public String getTown() {
        return town;
    }

    @XmlElement(name="state")
    public String getState() {
        return state;
    }

    @XmlElement(name="hotelChain")
    public String getHotelChain() {
        return hotelChain;
    }

    @XmlElement(name="minRating")
    public Integer getMinRating() {
        return minRating;
    }

    @XmlElement(name="maxRating")
    public Integer getMaxRating() {
        return maxRating;
    }

    @XmlElement(name="minNumberRooms")
    public Integer getMinNumberRooms() {
        return minNumberRooms;
    }

    @XmlElement(name="maxNumberRooms")
    public Integer getMaxNumberRooms() {
        return maxNumberRooms;
    }

    @XmlElement(name="minPrice")
    public Float getMinPrice() {
        return minPrice;
    }

    @XmlElement(name="maxPrice")
    public Float getMaxPrice() {
        return maxPrice;
    }

    @Override
    public String toString() {
        return "RoomSearchModel [startDate=" + startDate + ", endDate=" + endDate + ", minCapacity=" + minCapacity
                + ", maxCapacity=" + maxCapacity + ", town=" + town + ", state=" + state + ", hotelChain=" + hotelChain
                + ", minRating=" + minRating + ", maxRating=" + maxRating + ", minNumberRooms=" + minNumberRooms
                + ", maxNumberRooms=" + maxNumberRooms + ", minPrice=" + minPrice + ", maxPrice=" + maxPrice + "]";
    }
    
}
