package com.project.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cap")
    private Short CAP;

    @Column(name = "city")
    private String city;

    public List<TourismTypes> getTourismTypes() {
        return tourismTypes;
    }

    public void setTourismTypes(List<TourismTypes> tourismTypes) {
        this.tourismTypes = tourismTypes;
    }

    @ManyToMany
    @JoinTable(
            name = "tourism_type_city",
            joinColumns = @JoinColumn(name = "city_id"),
            inverseJoinColumns = @JoinColumn(name = "tourismTypes_id"))
    private List<TourismTypes> tourismTypes;

    public City(Short CAP, String city) {
        this.CAP = CAP;
        this.city = city;
    }

    public Short getCAP() {
        return CAP;
    }

    public void setCAP(Short CAP) {
        this.CAP = CAP;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
