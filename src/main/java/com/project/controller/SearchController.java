package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.Alternative;
import com.project.model.Room;
import com.project.service.IHotelService;
import com.project.service.ISecretSearch;
import ilog.concert.IloException;
import net.sf.clipsrules.jni.CLIPSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CrossOrigin(origins = "*")
@RestController
public class SearchController {

    @Autowired
    private IHotelService hotelService;
    @Autowired
    private ISecretSearch secretSearch;

    @PostMapping(value ="/hotels/freeRooms")
    public ResponseEntity<?> postFindFreeRooms(@RequestBody Map<String,Object> requestParams) throws ParseException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
        Integer numPlaces = mapper.convertValue(requestParams.get("numPlaces"),Integer.class);
        Date arrival = sf.parse((String)requestParams.get("arrival"));
        Date departure = sf.parse((String)requestParams.get("departure"));
        String city = mapper.convertValue(requestParams.get("city"),String.class);

        List<Room> freeRooms = (city!=null) ?
                hotelService.findFreeRooms(arrival, departure, city) :
                hotelService.findFreeRooms(arrival, departure) ;

        return new ResponseEntity<>(freeRooms, HttpStatus.OK);
    }

    @PostMapping("/hotels/secretSearch")
    public ResponseEntity<?> getSecretSearch(@RequestBody Map<String,Object> requestParams)
            throws CLIPSException, IloException, ParseException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat  sf = new SimpleDateFormat("dd/MM/yyyy");

        System.out.println(requestParams);

        List<LinkedHashMap<String, String>> cities = (List<LinkedHashMap<String, String>>)requestParams.get("cities");
        Double maxBudget = mapper.convertValue(requestParams.get("maxBudget"), Double.class);
        Integer numPeople = mapper.convertValue(requestParams.get("people"), Integer.class);
        String onlyRegion = mapper.convertValue(requestParams.get("onlyRegion"), String.class);
        String onlyNotRegion = mapper.convertValue(requestParams.get("onlyNotRegion"), String.class);
        Integer maxStars = mapper.convertValue(requestParams.get("maxStars"), Integer.class);
        Integer minStars = mapper.convertValue(requestParams.get("minStars"), Integer.class);
        List<String> tourismTypes = (List<String>)requestParams.get("tourismTypes");
        Date arrival = sf.parse((String)requestParams.get("arrival"));
        Date departure = sf.parse((String)requestParams.get("departure"));

        List<Alternative> allAlternatives = secretSearch.getAllAlternatives(cities,
                (int) TimeUnit.DAYS.convert(departure.getTime() - arrival.getTime(), TimeUnit.MILLISECONDS),
                maxBudget, numPeople, onlyRegion,
                onlyNotRegion, maxStars, minStars, tourismTypes, arrival, departure);

        return new ResponseEntity<>(allAlternatives, HttpStatus.OK);
    }
}
