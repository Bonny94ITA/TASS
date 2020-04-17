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
    private int CAP;

    @Column(name = "name")
    private String name;

    @Column(name = "region")
    private String region;

    @ManyToMany
    @JoinTable(
            name = "tourism_type_city",
            joinColumns = @JoinColumn(name = "city_id"),
            inverseJoinColumns = @JoinColumn(name = "tourismTypes_id"))
    private List<TourismTypes> tourismTypes;

    public Long getId() {
        return id;
    }

    public City(){}
    public City(int CAP, String name, String region, List<TourismTypes> tourismTypes) {
        this.CAP = CAP;
        this.name = name;
        this.region = region;
        this.tourismTypes = tourismTypes;
    }

    public int getCAP() {
        return CAP;
    }

    public void setCAP(int CAP) {
        this.CAP = CAP;
    }

    public String getName() {
        return name;
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

    public List<TourismTypes> getTourismTypes() {
        return tourismTypes;
    }

    public void setTourismTypes(List<TourismTypes> tourismTypes) {
        this.tourismTypes = tourismTypes;
    }
    // può dare problemi perchè si modifica il puntatore, forse meglio clear-> addall

}
