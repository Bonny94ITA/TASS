package com.project.service;

import com.project.controller.DataException.InsertException;
import com.project.model.City;
import com.project.model.Hotel;
import com.project.model.Room;
import com.project.model.TourismType;

import java.util.Date;
import java.util.List;

public interface IHotelService  {
    List<Hotel> findAllHotels();

    Hotel findHotelById(Long id);

    Room findRoomById(Long id);

    List<Room> findRooms(long h);

    List<TourismType> findAllTourismTypes();

    List<City> findAllCities();

    List<Room> findFreeRooms(Date arrival, Date departure, String city);

    List<Room> findFreeRooms(Date arrival, Date departure);
}
