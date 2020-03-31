package com.project.model;

import javax.persistence.*;

@Entity
@Table(name = "item")
public class Item {

    //FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Altri attributi

    //CONSTRUCTORS
    public Item() {}
    public Item(Long itemId) {}

    //GETTERS
    public Long getId() { return id; }

    //SETTERS
    public void setId(Long id) { this.id = id; }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                '}';
    }
}
