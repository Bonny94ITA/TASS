package com.project.service;

import com.project.model.Hotel;
import com.project.model.Room;

import java.util.List;

public interface IHotelService  {
    List<Hotel> findAll();

    Hotel findById(Long id);

    Hotel addHotel(Hotel hotel);

    Room addRoom(Long h, Room r);

    List<Room> findRooms(long h);
}
