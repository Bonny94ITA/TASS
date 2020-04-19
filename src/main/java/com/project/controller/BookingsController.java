package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.*;
import com.project.service.*;
import ilog.concert.IloException;
import net.sf.clipsrules.jni.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;
import java.util.*;

@RestController
public class BookingsController {
//PROBLEMA: ITEM DEVONO ESSERE ASSOCIABILI A PIù PRENOTAZIONI, INOLTRE DEV'ESSERCI LA DATA DELLA PRENOTAZIONE
    //controllare che se lancia un'eccezione non deve salvare nulla in database (forse lo fa in automatico)
    @Autowired
    private IBookingService bookingService;
    @Autowired
    private ISecretSearch secretSearch;
    
    @GetMapping("/prova")
    public List<Alternative> prova(@RequestBody Map<String,Object> requestParams)
            throws CLIPSException, IloException, ParseException {
        List<LinkedHashMap<String, String>> cities = (List<LinkedHashMap<String, String>>)requestParams.get("cities");
        Integer days = (Integer)requestParams.get("days");
        Double maxBudget = (Double)requestParams.get("max-budget");
        Integer numPeople = (Integer)requestParams.get("people");
        String onlyRegion = (String)requestParams.get("only-region");
        String onlyNotRegion = (String)requestParams.get("only-not-region");
        Integer maxStars =(Integer)requestParams.get("max-stars");
        Integer minStars =(Integer)requestParams.get("min-stars");
        List<String> tourismTypes = (List<String>)requestParams.get("tourism-types");

        return secretSearch.getAllAlternatives(cities, days, maxBudget, numPeople, onlyRegion,
                                               onlyNotRegion, maxStars, minStars, tourismTypes);
    }

    @GetMapping("/booking")
    public List<Booking> getAllBooking() {
        return bookingService.findAll();
    }

    //da fare: quando i parametri passati non sono in database ritorna null, quando non passiamo i parametri la get su json torna null
    @PostMapping(value = "/bookings/insert")
    public Booking postRegisterBooking(@RequestBody Map<String,Object> requestParams) throws ParseException {
        ObjectMapper mapper = new ObjectMapper();
        //get from params
        Long guestId = mapper.convertValue(requestParams.get("guest"),Long.class);
        boolean paid = mapper.convertValue(requestParams.get("paid"),Boolean.class);
        Booking booking = mapper.convertValue(requestParams.get("booking"),Booking.class);
        Booking b =  bookingService.addBook(booking,guestId,paid);
        return b;
    }


    @PostMapping(value = "/booking/pay")
    public Payment payBooking(@RequestBody Map<String,Object> requestParams) throws ParseException{
        ObjectMapper mapper = new ObjectMapper();
        Long bookingId = mapper.convertValue(requestParams.get("id"),Long.class);
        return bookingService.payBooking(bookingId);
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

/* JSON
GET - localhost:8080/prova
{
	"cities":
		[
			{
				"city": "Nuoro",
				"region": "Sardegna"
			},
			{
				"city": "MedioCampidano",
				"region": "Sardegna"
			},
			{
				"city": "Salerno",
				"region": "Campania"
			}
		],
	"days": 5,
	"max-budget": 700.0,
	"people": 3,
	"only-region": "Sardegna",
	"only-not-region": "Lombardia",
	"max-stars": 4,
	"min-stars": 1,
	"tourism-types":
		[
			"balneare", "enogastronomico"
		]
}

 */
