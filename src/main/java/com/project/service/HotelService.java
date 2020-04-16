package com.project.service;

import com.project.model.Hotel;
import com.project.model.Room;
import com.project.repository.HotelRepository;
import com.project.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HotelService implements IHotelService {

    @Autowired
    HotelRepository hotelRepository;
    @Autowired
    RoomRepository roomRepository;

    @Override
    public List<Hotel> findAll() {
        List<Hotel> hotels = new ArrayList<>();
        hotelRepository.findAll().forEach(hotels::add);
        return hotels;
    }

    @Override
    public Hotel findById(Long id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        return hotel.isPresent() ? hotel.get() : null;
    }

    @Override
    public Hotel addHotel(Hotel hotel){
        return hotelRepository.save(new Hotel(hotel.getName(),hotel.getAddress(),hotel.getCAP(),hotel.getCity(),hotel.getCellNumber(),hotel.getStars()));
    }

    @Override
    public Room addRoom(Long hotelId, Room r){
        Hotel hotel = findById(hotelId);
        if(hotel == null) //gestire quando non trova
            return null;
        else
            return roomRepository.save(new Room(r.getNumPlaces(),hotel,r.getPricePerNight()));

    }

    @Override
    public List<Room> findRooms(long hotelId) {
        Hotel h = hotelRepository.findById(hotelId).get();

        return h!=null ? roomRepository.findByHotel(h) : null;
    }

}
