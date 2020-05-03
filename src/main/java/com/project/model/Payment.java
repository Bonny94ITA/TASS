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
    private double totalCost;

    @OneToOne(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="booking")
    private Booking booking;

    public Payment() {}
    public Payment(double totalCost, Booking booking) {
        this.booking = booking;
        this.totalCost = totalCost;
    }


    //GETTERS
    public long getId() {
        return id;
    }
    public double getTotalCost() {
        return totalCost;
    }
    public Booking getBooking() { return booking; }


    public void setTotalCost(long totalCost) {
        this.totalCost = totalCost;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "totalCost=" + totalCost +
                '}';
    }
}
