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

    @Column(name = "cap")
    private Short CAP;

    @Column(name = "city")
    private String city;

    @Column(name = "cellNumber")
    private String cellNumber;

    @Column(name = "stars")
    private Short stars;

    public List<TourismTypes> getTourismTypes() {
        return tourismTypes;
    }

    public void setTourismTypes(List<TourismTypes> tourismTypes) {
        this.tourismTypes = tourismTypes;
    }

    @Column(name = "region")
    private String region;

    @ManyToMany
    @JoinTable(
            name = "hotel_type",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "tourismTypes_id"))
    private List<TourismTypes> tourismTypes;

    //CONSTRUCTORS
    public Hotel() {    }

    public Hotel(String name, String address, Short CAP, String city, String cellNumber, Short stars, String region, List<TourismTypes> tList) {
        this.name = name;
        this.address = address;
        this.CAP = CAP;
        this.city = city;
        this.cellNumber = cellNumber;
        this.stars = stars;
        this.region = region;
        this.tourismTypes = tList;
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
