package com.project.model;

import javax.persistence.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "booking")
public class Booking {

    //FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name="booking")
    private List<Sojourn> sojourns;

    @ManyToMany
    @JoinTable(
            name = "booking_item",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> rentedItems;

    //CONSTRUCTORS
    public Booking() {}
    public Booking(List<Sojourn> sojourns, List<Item> items) {
        this.sojourns = sojourns;
        this.rentedItems = items;
    }

    //GETTERS
    public Long getId() { return id; }
    public List<Sojourn> getSojourns() { return sojourns; }
    public List<Item> getRentedItems() { return rentedItems; }

    //SETTERS       //non va bene mettere lista = nuova pechè si perdono i collegamenti
    public void setId(Long id) { this.id = id; }
    //public void setSojourns(List<Sojourn> sojourns) { this.sojourns = sojourns; }
    //public void setRentedItems(List<Item> rentedItems) { this.rentedItems = rentedItems; }

    //ADDERS
    public void addRentedItems(Item item) {
        if(rentedItems == null)
            rentedItems = new ArrayList<>();
        rentedItems.add(item);
    }
    public void addSojourns(Sojourn sojourn) {
        if(sojourns == null)
            sojourns = new ArrayList<>();
        sojourns.add(sojourn);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                '}';
    }
}
