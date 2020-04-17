package com.project.model;

import javax.persistence.*;


@Entity
@Table(name = "tourismType")
public class TourismTypes{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    private String type;

    public TourismTypes(){}
    public TourismTypes(String type){
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