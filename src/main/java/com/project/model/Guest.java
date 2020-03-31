package com.project.model;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "guest")
public class Guest {

    //FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String username;

    @Column(name = "hashPwd")
    private String pwd;

    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="guest")
    private List<Booking> booking;

    //CONSTRUCTORS
    public Guest() {}
    public Guest(String email, String name, String pwd, String username) {
        this.pwd = pwd;
        this.username = username;
        this.email = email;
        this.name = name;
        this.booking = new LinkedList<>();
    }

    //GETTERS
    public Long getId(){ return id;}
    public String getEmail() { return email; }
    public String getName() { return name; }
    public List<Booking> getBooking() { return booking; }
    public String getUsername() { return username; }
    public String getPwd() { return pwd; }

    //SETTERS
    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
    public void setBooking(List<Booking> booking) { this.booking = booking; }

    //ADDERS
    public void addBooking(Booking book) {
        booking.add(book);
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}