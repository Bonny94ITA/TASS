package com.project.model;

import javax.persistence.*;
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

    //CONSTRUCTORS
    public Booking() {}
    public Booking(List<Sojourn> sojourns) {
        this.sojourns = sojourns;
    }

    //GETTERS
    public Long getId() { return id; }
    public List<Sojourn> getSojourns() { return sojourns; }

    //SETTERS       //non va bene mettere lista = nuova pechè si perdono i collegamenti
    public void setId(Long id) { this.id = id; }
    public void setSojourns(List<Sojourn> sojourns) {
        this.sojourns.clear();
        this.sojourns.addAll(sojourns);
    }
    //public void setRentedItems(List<Item> rentedItems) { this.rentedItems = rentedItems; }

    //ADDERS
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
