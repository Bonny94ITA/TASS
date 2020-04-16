package com.project.model;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

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

    @OneToOne
    @JoinColumn(name="city")
    private City city;

    @Column(name = "cellNumber")
    private String cellNumber;

    @Column(name = "stars")
    private Short stars;

    @Column(name = "region")
    private String region;



    //CONSTRUCTORS
    public Hotel() {    }

    public Hotel(String name, String address, City city, String cellNumber, Short stars, String region) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.cellNumber = cellNumber;
        this.stars = stars;
        this.region = region;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
    public City getCity() {
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

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(City city) {
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
                ", city='" + city + '\'' +
                ", cellNumber='" + cellNumber + '\'' +
                ", stars=" + stars +
                '}';
    }
}
