package com.project.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guest")
public class Guest {

    //FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "hashPwd")
    private String pwd;

    @Column(name = "social_auth")
    private Boolean social_auth;

    @OneToMany
    @JoinColumn(name="guest")
    private List<Booking> booking;

    //CONSTRUCTORS
    public Guest() {}
    public Guest(String email, String name, String pwd, Boolean social_auth) {
        this.pwd = pwd;
        this.email = email;
        this.name = name;
        this.social_auth = social_auth;
        this.booking = null;
    }

    //GETTERS
    public Long getId(){ return id;}
    public String getEmail() { return email; }
    public String getName() { return name; }
    public List<Booking> getBooking() { return booking; }
    public String getPwd() { return pwd; }
    public Boolean getSocial_auth() { return social_auth; }

    //SETTERS
    public void setEmail(String email) { this.email = email; }
    public void setPwd(String pwd) { this.pwd = pwd; }
    public void setName(String name) { this.name = name; }
    public void setBooking(List<Booking> booking) { this.booking = booking; }
    public void setSocial_auth(Boolean social_auth) { this.social_auth = social_auth; }

    public void addBooking(Booking booking){
        if(this.booking == null)
            this.booking = new ArrayList<>();
        this.booking.add(booking);
    }
}