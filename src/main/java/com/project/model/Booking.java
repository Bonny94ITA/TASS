package com.project.model;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "booking")
public class Booking {

    //FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="booking")
    private List<Sojourn> sojourns;

    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="booking")
    private List<Item> rentedItems;

    //CONSTRUCTORS
    public Booking() {
        rentedItems = new LinkedList<>();
        sojourns = new LinkedList<>();
    }

    //GETTERS
    public Long getId() { return id; }
    public List<Sojourn> getSojourns() { return sojourns; }
    public List<Item> getRentedItem() { return rentedItems; }

    //SETTERS
    public void setId(Long id) { this.id = id; }
    public void setSojourns(List<Sojourn> sojourns) { this.sojourns = sojourns; }
    public void setRentedItems(List<Item> rentedItems) { this.rentedItems = rentedItems; }

    //ADDERS
    public void addItem(Item item) {
        rentedItems.add(item);
    }
    public void addSojourn(Sojourn sojourn) {
        sojourns.add(sojourn);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                '}';
    }
}
