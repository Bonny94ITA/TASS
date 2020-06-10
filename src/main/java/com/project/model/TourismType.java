package com.project.model;

import javax.persistence.*;

@Entity
@Table(name = "tourismType")
public class TourismType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    private String type;

    public TourismType(){}
    public TourismType(String type){
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}