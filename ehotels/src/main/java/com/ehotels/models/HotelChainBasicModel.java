package com.ehotels.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HotelChainBasicModel {
    
    private int id;
    private String name;
    
    public HotelChainBasicModel(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    @XmlElement(name="id")
    public int getId() {
        return id;
    }
    
    @XmlElement(name="name")
    public String getName() {
        return name;
    }
    
    

}
