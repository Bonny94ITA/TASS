package com.project.model;

import javax.persistence.*;

@Entity
@Table(name = "item")
public class Item {

    //FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
    //CONSTRUCTORS
    public Item() {}
    public Item(String name){this.name=name;}

    //GETTERS
    public Long getId() { return id; }
    public String getName(){return name;}

    //SETTERS
    public void setName(String name){this.name = name;}

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                '}';
    }
}
