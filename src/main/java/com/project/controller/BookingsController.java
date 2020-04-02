package com.project.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.*;


import com.project.repository.SojournRepository;
import com.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class BookingsController {
//PROBLEMA: ITEM DEVONO ESSERE ASSOCIABILI A PIù PRENOTAZIONI, INOLTRE DEV'ESSERCI LA DATA DELLA PRENOTAZIONE
    @Autowired
    IBookingService bookingService;


    @GetMapping("/booking")
    public List<Booking> getAllBooking() {
        return bookingService.findAll();
    }

    //da fare: quando i parametri passati non sono in database ritorna null, quando non passiamo i parametri la get su json torna null
    @PostMapping(value = "/bookings/insert")
    public Booking postRegisterGuest(@RequestBody Map<String,Object> requestParams) throws ParseException {
        ObjectMapper mapper = new ObjectMapper();
        //get from params
        Long guestId = mapper.convertValue(requestParams.get("guest"),Long.class);
        Booking booking = mapper.convertValue(requestParams.get("booking"),Booking.class);
        Booking b =  bookingService.addBook(booking,guestId);
        return b;
    }
}
/* json
    localhost:8080/bookings/insert
    {
        "guest": 1,
        "booking":{
            "sojourns": [
                    {
                            "arrival": "21/05/1921",
                            "departure": "4/06/1121",
                            "room": {"id":1}
                    },
                    {
                            "arrival": "8/07/1921",
                            "departure": "11/08/1921",
                            "room": {"id":1}
                    }
            ],
            "rentedItems": [
                    {"id": 1},
                    {"id": 2}
            ]
        }
    }
 */
