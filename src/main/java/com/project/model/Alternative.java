package com.project.model;

import java.io.Serializable;
import java.util.*;

public class Alternative implements Serializable {
    private List<Sojourn> sojourns;
    private Integer days;

    public Alternative(List<HashMap<String, Object>> roomsHotels, Integer days, Date startingDate){
        Calendar c = Calendar.getInstance();
        List<Sojourn> sojournList = new ArrayList<>();
        c.setTime(startingDate);
        for(HashMap<String, Object> stanza : roomsHotels) {
            Date arrival = c.getTime();
            c.add(Calendar.DAY_OF_MONTH, ((Double)stanza.get("DaysInRoom")).intValue());
            Date departure = c.getTime();
            sojournList.add(new Sojourn(arrival, departure, (Room)stanza.get("Room")));
        }
        this.sojourns = sojournList;
        this.days = days;
    }

    public List<Sojourn> getSojourns() {
        return sojourns;
    }

    public void setSojourns(List<Sojourn> sojourns) {
        this.sojourns = sojourns;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Double getTotalPrice(){
        Double totalPrice = 0.0;
        for(Sojourn s : sojourns)
            totalPrice += s.getTotalPrice();
        return totalPrice;
    }
}
