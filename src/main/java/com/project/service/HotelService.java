package com.project.service;

import com.project.model.City;
import com.project.model.Hotel;
import com.project.model.Room;
import com.project.model.TourismTypes;
import com.project.repository.CityRepository;
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
    @Autowired
    CityRepository cityRepository;

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
        Optional<City> byId = cityRepository.findById(hotel.getCity().getId());
        //if(!byId.isPresent())
            //throw errore
        return hotelRepository.save(new Hotel(hotel.getName(),hotel.getAddress(),byId.get(),
                                        hotel.getCellNumber(),hotel.getStars()));
    }

    @Override
    public City addCity(City city){
        List<TourismTypes> tourismTypesList = new ArrayList<>();
        for(TourismTypes t: city.getTourismTypes()){
            Optional<TourismTypes> byId = tourismTypesRepository.findById(t.getId());
            if(byId.isPresent())
                tourismTypesList.add(byId.get());
            //gestire quando passa un tourismtype non esistente in database
        }
        return cityRepository.save(new City(city.getCAP(), city.getName(), city.getRegion(),tourismTypesList));
    }

    @Override
    public TourismTypes addTourismType(TourismTypes tt){
        return tourismTypesRepository.save(new TourismTypes(tt.getType()));
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
