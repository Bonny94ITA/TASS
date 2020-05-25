package com.project.model;

import javax.persistence.*;

@Entity
@Table(name = "room")
public class Room {
    //FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numPlaces")
    private int numPlaces;

    @Column(name = "ppn")
    private double pricePerNight;

    @OneToOne
    @JoinColumn(name="hotel")
    private Hotel hotel;

    //CONSTRUCTORS
    public Room() {}
    public Room(int numPlaces, Hotel hotel, double pricePerNight) {
        this.numPlaces = numPlaces;
        this.hotel = hotel;
        this.pricePerNight = pricePerNight;
    }

    //GETTERS
    public Long getId() { return id; }
    public int getNumPlaces() { return numPlaces; }
    public Hotel getHotel() { return hotel; }
    public double getPricePerNight() { return pricePerNight; }

    //SETTERS
    public void setId(Long id) { this.id = id; }
    public void setNumPlaces(int numPlaces) { this.numPlaces =numPlaces; }
    public void setHotel(Hotel hotel) { this.hotel= hotel; }
    public void setPricePerNight(double pricePerNight) {this.pricePerNight = pricePerNight; }


    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", numPlaces=" + numPlaces +
                ", hotel=" + hotel +
                ", pricePerNight=" + pricePerNight +
                '}';
    }
}
