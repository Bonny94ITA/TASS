package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.*;
import com.project.service.IHotelService;
import com.project.service.ISecretSearch;
import ilog.concert.IloException;
import net.sf.clipsrules.jni.CLIPSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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