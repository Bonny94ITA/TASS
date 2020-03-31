package com.project.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sojourn")
public class Sojourn {

    //FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "arrival")
    private Date arrival;

    @Column(name = "departure")
    private Date departure;

    @OneToOne(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "room")
    private Room room;

    //CONSTRUCTORS
    public Sojourn() {}
    public Sojourn(Date arrival, Date departure) {
        this.arrival = arrival;
        this.departure = departure;
        this.room = null;
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
