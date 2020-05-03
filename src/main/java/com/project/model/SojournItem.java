package com.project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Check;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sojourn_item")
@Check(constraints = "start_rent <= end_rent")
public class SojournItem {

    //FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Sojourn sojourn;

    @ManyToOne
    private Item item;

    @Column(name="start_rent")
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date startRent;

    @Column(name="end_rent")
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date endRent;

    public Date getStartRent() {
        return startRent;
    }

    public void setStartRent(Date startRent) {
        this.startRent = startRent;
    }

    public Date getEndRent() {
        return endRent;
    }

    public void setEndRent(Date endRent) {
        this.endRent = endRent;
    }

    public SojournItem() {
    }

    public Sojourn getSojourn() {
        return sojourn;
    }

    public void setSojourn(Sojourn sojourn) {
        this.sojourn = sojourn;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "SojournItem{" +
                "sojourn=" + sojourn +
                ", item=" + item +
                ", startRent=" + startRent +
                ", endRent=" + endRent +
                '}';
    }
}
