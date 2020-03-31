package com.project.model;

import javax.persistence.*;

@Entity
@Table(name = "payment")
public class Payment {

    //FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "totalCost")
    private long totalCost;

    //ALtri attributi

    @OneToOne(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="booking")
    private Booking booking;

    //CONSTRUCTORS
    public Payment() {}
    public Payment(long totalCost, Booking booking) {
        this.booking = booking;
        this.totalCost = totalCost;
    }

    //GETTERS
    public long getTotalCost() {
        return totalCost;
    }

    public long getId() {
        return id;
    }

    //SETTERS
    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    //ADDERS
    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "totalCost=" + totalCost +
                '}';
    }
}
