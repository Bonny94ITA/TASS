package com.project.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sojourn")
public class Sojourn {

    //FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "arrival")
    @JsonFormat(pattern="dd/MM/yyyy")  //start date
    private Date arrival;

    @Column(name = "departure")
    @JsonFormat(pattern="dd/MM/yyyy")       //end date
    private Date departure;

    @OneToOne
    @JoinColumn(name = "room")
    private Room room;

    //CONSTRUCTORS
    public Sojourn() {}
    public Sojourn(Date arrival, Date departure, Room room) {
        this.arrival = arrival;
        this.departure = departure;
        this.room = room;
    }

    //GETTERS
    public Long getId() { return id; }
    public Date getArrival() { return arrival; }
    public Date getDeparture() { return departure; }
    public Room getRoom() { return room; }

    //SETTERS
    public void setId(Long id) { this.id = id; }
    public void setArrival(Date arrival) { this.arrival = arrival; }
    public void setDeparture(Date departure) { this.departure = departure; }
    public void setRoom(Room room) {this.room = room; }

    @Override
    public String toString() {
        return "Sojourn{" +
                "id=" + id +
                ", arrival=" + arrival +
                ", departure=" + departure +
                ", room=" + room +
                '}';
    }
}
