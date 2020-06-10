package com.project.controller;

import com.project.model.City;
import com.project.model.Hotel;
import com.project.model.Room;
import com.project.service.IHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class HotelController {

    @Autowired
    private IHotelService hotelService;

    @GetMapping("/hotels")
    public ResponseEntity<?> getAllHotels() {
        List<Hotel> allHotels = hotelService.findAllHotels();
        return new ResponseEntity<>(allHotels, HttpStatus.OK);
    }

    @GetMapping("/hotels/rooms/{id}")
    public ResponseEntity<?> getHotelRooms(@PathVariable("id") long hotelId) {
        List<Room> rooms = hotelService.findRooms(hotelId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/hotels/cities")
    public ResponseEntity<?> getCities(){
        List<City> cities = hotelService.findAllCities();
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }
}