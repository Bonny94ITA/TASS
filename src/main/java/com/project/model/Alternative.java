package com.project.model;

import java.io.Serializable;
import java.util.*;

public class Alternative implements Serializable {
    private List<Sojourn> sojournLists;
    private Integer days;

    public Alternative(List<HashMap<String, Object>> roomsHotels, Integer days, Date startingDate){
        Calendar c = Calendar.getInstance();
        List<Sojourn> sojorunList = new ArrayList<>();
        c.setTime(startingDate);
        for(HashMap<String, Object> stanza : roomsHotels) {
            Date arrival = c.getTime();
            c.add(Calendar.DAY_OF_MONTH, ((Double)stanza.get("DaysInRoom")).intValue());
            Date departure = c.getTime();
            sojorunList.add(new Sojourn(arrival, departure, (Room)stanza.get("Room")));
        }
        this.sojournLists = sojorunList;
        this.days = days;
    }


    public List<Sojourn> getSojorunLists() {
        return sojournLists;
    }
    public Integer getDays() {
        return days;
    }
}
