package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.Guest;
import com.project.model.Hotel;
import com.project.model.Room;
import com.project.service.IHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class HotelController {
    @Autowired
    IHotelService hotelService;

    @GetMapping("/hotels")
    public List<Hotel> getAllHotels() {
        return hotelService.findAll();
    }

    @GetMapping("/hotel/rooms/{id}")
    public List<Room> getHotelRooms(@PathVariable("id") long hotelId) {
        return hotelService.findRooms(hotelId);
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
}
/* JSON
HOTEL
    POST - localhost:8080/hotel/register
    {
        "address":"vialupa",
        "cap":11231,
        "city":"comodino",
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
 */
