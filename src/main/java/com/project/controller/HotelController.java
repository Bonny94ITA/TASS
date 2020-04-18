package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.*;
import com.project.service.IHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class HotelController {
    @Autowired
    IHotelService hotelService;


    @GetMapping("/hotels")
    public List<Hotel> getAllHotels() {
        return hotelService.findAllHotels();
    }

    @GetMapping("/hotel/rooms/{id}")
    public List<Room> getHotelRooms(@PathVariable("id") long hotelId) {
        return hotelService.findRooms(hotelId);
    }

    @PostMapping(value ="/search")
    public List<Room> findFreeRooms(@RequestBody Map<String,Object> requestParams){
        ObjectMapper mapper = new ObjectMapper();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        mapper.setDateFormat(df);
        Date arrival = mapper.convertValue(requestParams.get("arrival"),Date.class);
        Date departure = mapper.convertValue(requestParams.get("departure"),Date.class);
        String city = mapper.convertValue(requestParams.get("city"),String.class);
        return hotelService.findFreeRooms(arrival,departure,city);
    }

    @PostMapping(value = "/hotel/register")
    public Hotel postAddHotel(@RequestBody Hotel h){ return hotelService.addHotel(h); }

    @PostMapping(value = "/hotel/rooms/new")
    public Room postAddRoom(@RequestBody Map<String,Object> requestParams){
        ObjectMapper mapper = new ObjectMapper();
        Long hotelId = mapper.convertValue(requestParams.get("hotel"),Long.class);
        //Long hotelId = mapper.convertValue(requestParams.get("hotel"),Long.class).getId();
        Room room = mapper.convertValue(requestParams.get("room"),Room.class);
        return hotelService.addRoom(hotelId,room);
    }

    @PostMapping(value = "/city/new")
    public City postAddCity(@RequestBody City city){ return hotelService.addCity(city); }

    @PostMapping(value = "/tourismType/new")
    public TourismType postAddTourismTypes(@RequestBody TourismType tt){ return hotelService.addTourismType(tt); }



    //BUG: se crei l'oggetto manualmente da pgadmin e poi usi insomnia ti da eccezione dicendo che quell'id esiste già
    // su tourismtype ( il contatore di spring non si aggiorna sulle modifiche del db?)

}
/* JSON
HOTEL
    POST - localhost:8080/hotel/register
{
	"name":"hotellino",
	"address":"vialupa",
	"city": {"id":1},
	"cellNumber":"1111111",
	"stars":5
}

ROOM
    POST- localhost:8080/hotel/rooms/new
    {
        "hotel":2,
        "room":{
		    "numPlaces":4,
		    "pricePerNight":50
	        }
    }

    GET - localhost:8080/hotel/rooms/1      (per vedere le stanze di un hotel, id è riferito a stanza)

 CITY
    POST -localhost:8080/city/new
    {
        "cap":55555,
        "name":"torino"
	    "region":"piemonte",
        "tourismTypes":[
            {"id":1},
        ]
    }

 TOURISMTYPES
    POST - localhost:8080/tourismType/new
    {
        "type": "montagna"
    }

    CERCA STANZE LIBERE
    POST -localhost:8080/search
    {
	"arrival":"20/05/2020",
	"departure":"22/05/2020",
	"city":"torino"
    }

 */
