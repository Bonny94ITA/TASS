package com.project.service;

import com.project.model.City;
import com.project.model.Hotel;
import com.project.model.Room;
import com.project.model.TourismType;
import com.project.repository.CityRepository;
import com.project.repository.HotelRepository;
import com.project.repository.RoomRepository;
import com.project.repository.TourismTypesRepository;
import jdk.nashorn.internal.runtime.regexp.joni.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HotelService implements IHotelService {

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private TourismTypesRepository tourismTypesRepository;
    @Autowired
    private CityRepository cityRepository;

    @Override
    public List<Hotel> findAllHotels() {
        List<Hotel> hotels = new ArrayList<>();
        hotelRepository.findAll().forEach(hotels::add);
        return hotels;
    }

    @Override
    public Hotel findHotelById(Long id) {
        Optional<Hotel> hotel = hotelRepository.findById(id);
        return hotel.isPresent() ? hotel.get() : null;
    }

    @Override
    public Room findRoomById(Long id) {
        Optional<Room> room = roomRepository.findById(id);
        return room.isPresent() ? room.get() : null;
    }

    @Override
    public List<Room> findRooms(long hotelId) {
        Hotel h = hotelRepository.findById(hotelId).get();

        return h!=null ? roomRepository.findByHotel(h) : null;
    }

    @Override
    public List<TourismType> findAllTourismTypes() {
        List<TourismType> tt = new ArrayList<>();
        tourismTypesRepository.findAll().forEach(tt::add);
        return tt;
    }

    @Override
    public List<City> findAllCities() {
        List<City> cities = new ArrayList<>();
        cityRepository.findAll().forEach(cities::add);
        return cities;
    }

    @Override
    public List<Room> findFreeRooms(Date arrival, Date departure, String city){
        System.out.println(arrival);
        System.out.println(departure);
        System.out.println(city);
        return roomRepository.findAllFreeRoomsIn(arrival,departure,city);
    }

    @Override
    public List<Room> findFreeRooms(Date arrival, Date departure){
        return roomRepository.findAllFreeRoomsIn(arrival,departure);
    }

    /* il soggiorno nuovo viene creato quando si paga una prenotazione e quindi blocca la stanza
    così per cercare le stanze libere basta guardare nei soggiorni esistenti
     */

    /*per la ricerca l'utente*/
}
