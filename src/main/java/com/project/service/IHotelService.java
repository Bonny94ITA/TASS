package com.project.service;

import com.project.model.City;
import com.project.model.Hotel;
import com.project.model.Room;
import com.project.model.TourismTypes;

import java.util.List;

public interface IHotelService  {
    List<Hotel> findAll();

    Hotel findById(Long id);

    Hotel addHotel(Hotel hotel);

    City addCity(City city);

    TourismTypes addTourismType(TourismTypes tt);

    Room addRoom(Long h, Room r);

    List<Room> findRooms(long h);
}
