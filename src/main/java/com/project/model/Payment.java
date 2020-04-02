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

    @OneToOne(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="booking")
    private Booking booking;

    public Payment() {}
    public Payment(long totalCost, Booking booking) {
        this.booking = booking;
        this.totalCost = totalCost;
    }


    //GETTERS
    public long getId() {
        return id;
    }
    public long getTotalCost() {
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
