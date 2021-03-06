package com.project.model;

import javax.persistence.*;

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

    @Column(name = "cellNumber")
    private String cellNumber;

    @Column(name = "stars")
    private Short stars;

    @ManyToOne
    @JoinColumn(name="city")
    private City city;

    //CONSTRUCTORS
    public Hotel() {    }

    public Hotel(String name, String address, City city, String cellNumber, Short stars) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.cellNumber = cellNumber;
        this.stars = stars;
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
