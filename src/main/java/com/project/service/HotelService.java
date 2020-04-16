package com.project.service;

import com.project.model.Hotel;
import com.project.model.Room;
import com.project.model.TourismTypes;
import com.project.repository.HotelRepository;
import com.project.repository.RoomRepository;
import com.project.repository.TourismTypesRepository;
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
    @Autowired
    TourismTypesRepository tourismTypesRepository;

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

        List<TourismTypes> tourismTypesList = new ArrayList<>();
        for(TourismTypes t: hotel.getTourismTypes()){
            Optional<TourismTypes> byId = tourismTypesRepository.findById(t.getId());
            if(byId.isPresent())
                tourismTypesList.add(byId.get());
            //gestire quando passa un tourismtype non esistente in database
        }

        return hotelRepository.save(new Hotel(hotel.getName(),hotel.getAddress(),hotel.getCAP(),hotel.getCity(),
                                        hotel.getCellNumber(),hotel.getStars(),hotel.getRegion(),tourismTypesList));
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
