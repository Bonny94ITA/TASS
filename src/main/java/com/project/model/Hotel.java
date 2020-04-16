package com.project.model;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "hotel")
public class Hotel {
    //FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "cap")
    private Short CAP;

    @Column(name = "city")
    private String city;

    @Column(name = "cellNumber")
    private String cellNumber;

    @Column(name = "stars")
    private Short stars;

    //CONSTRUCTORS
    public Hotel() {    }
    public Hotel(String name, String address, Short CAP, String city, String cellNumber, Short stars) {
        this.address = address;
        this.CAP = CAP;
        this.city = city;
        this.cellNumber = cellNumber;
        this.stars = stars;
    }

    public Long getId() {
        return id;
    }

    public String getName() { return name; }

    public String getAddress() {
        return address;
    }

    public Short getCAP() {
        return CAP;
    }

    public String getCity() {
        return city;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public Short getStars() {
        return stars;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {this.name = name; }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCAP(Short CAP) {
        this.CAP = CAP;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public void setStars(Short stars) {
        this.stars = stars;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", CAP=" + CAP +
                ", city='" + city + '\'' +
                ", cellNumber='" + cellNumber + '\'' +
                ", stars=" + stars +
                '}';
    }
}
