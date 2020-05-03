package com.project.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Alternative implements Serializable {
    private List<HashMap<String, Object>> roomsHotels;
    private Integer days;

    public Alternative(List<HashMap<String, Object>> roomsHotels, Integer days) {
        this.roomsHotels = roomsHotels;
        this.days = days;
    }

    public List<HashMap<String, Object>> getRoomsHotels() {
        return roomsHotels;
    }
    public Integer getDays() {
        return days;
    }
}
