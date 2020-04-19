package com.project.service;

import com.project.model.*;

import java.util.List;

public interface IHotelService  {
    List<Hotel> findAllHotels();

    Hotel findById(Long id);

    Hotel addHotel(Hotel hotel);

    City addCity(City city);

    TourismType addTourismType(TourismType tt);

    Room addRoom(Long h, Room r);

    List<Room> findRooms(long h);

    List<TourismType> findAllTourismTypes();

    List<City> findAllCities();

    List<Room> test();
}
