package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.*;
import com.project.service.IHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class HotelController {
    @Autowired
    IHotelService hotelService;

    @GetMapping("/hotels")
    public ResponseEntity<?> getAllHotels() {
        List<Hotel> allHotels = hotelService.findAllHotels();
        return new ResponseEntity<>(allHotels, HttpStatus.OK);
    }

    @GetMapping("/hotel/rooms/{id}")
    public ResponseEntity<?> getHotelRooms(@PathVariable("id") long hotelId) {
        List<Room> rooms = hotelService.findRooms(hotelId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/cities")
    public ResponseEntity<?> getCities(){
        List<City> cities = hotelService.findAllCities();
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }

    @PostMapping(value ="/freeRooms")
    public ResponseEntity<?> postFindFreeRooms(@RequestBody Map<String,Object> requestParams) throws ParseException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
        Integer numPlaces = mapper.convertValue(requestParams.get("numPlaces"),Integer.class);
        //modificare
        Date arrival = sf.parse((String)requestParams.get("arrival"));
        Date departure = sf.parse((String)requestParams.get("departure"));
        String city = mapper.convertValue(requestParams.get("city"),String.class);

        List<Room> freeRooms = (city!=null) ? freeRooms =
                hotelService.findFreeRooms(arrival, departure, city) :
                hotelService.findFreeRooms(arrival, departure) ;

        return new ResponseEntity<>(freeRooms, HttpStatus.OK);
    }

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
    GET - localhost:8080/search
    {
	    "arrival":"20/05/2020",
	    "departure":"22/05/2020",
	    "city":"torino"
    }

 */
