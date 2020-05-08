package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.controller.DataException.InsertException;
import com.project.controller.DataFormatter.OutputData;
import com.project.model.Alternative;
import com.project.model.Booking;
import com.project.model.Payment;
import com.project.model.SojournItem;
import com.project.service.IBookingService;
import com.project.service.IItemService;
import com.project.service.ISecretSearch;
import ilog.concert.IloException;
import net.sf.clipsrules.jni.CLIPSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CrossOrigin(origins = "*")
@RestController
public class BookingsController {
//PROBLEMA: ITEM DEVONO ESSERE ASSOCIABILI A PIù PRENOTAZIONI, INOLTRE DEV'ESSERCI LA DATA DELLA PRENOTAZIONE
    //controllare che se lancia un'eccezione non deve salvare nulla in database (forse lo fa in automatico)
    @Autowired
    private IBookingService bookingService;
    @Autowired
    private IItemService itemService;
    @Autowired
    private ISecretSearch secretSearch;
    
    @PostMapping("/secretSearch")
    public OutputData getSecretSearch(@RequestBody Map<String,Object> requestParams)
            throws CLIPSException, IloException, ParseException {
        OutputData df = new OutputData();
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat  sf = new SimpleDateFormat("dd/MM/yyyy");

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

        df.setResultCode(OutputData.ResultCode.RESULT_OK);
        df.setReturnedValue(allAlternatives);

        return df;
    }

    @GetMapping("/bookings")
    public OutputData getAllBooking() {
        OutputData df = new OutputData();
        List<Booking> allBookings = bookingService.findAll();

        df.setResultCode(OutputData.ResultCode.RESULT_OK);
        df.setReturnedValue(allBookings);

        return df;
    }

    //da fare: quando i parametri passati non sono in database ritorna null, quando non passiamo i parametri la get su json torna null
    @PostMapping(value = "/bookings/insert")
    public OutputData postRegisterBooking(@RequestBody Map<String,Object> requestParams) throws ParseException {
        OutputData df = new OutputData();
        ObjectMapper mapper = new ObjectMapper();
        //get from params
        Long guestId = mapper.convertValue(requestParams.get("guest"),Long.class);
        //boolean paymentId = mapper.convertValue(requestParams.get("payment"),Boolean.class);
        Booking booking = mapper.convertValue(requestParams.get("booking"),Booking.class);

        try {
            Booking b = bookingService.addBook(booking, guestId);
            df.setResultCode(OutputData.ResultCode.RESULT_OK);
            df.setReturnedValue(b);
        } catch (InsertException e) {
            df.setResultCode(e.getExceptionCode());
        }

        return df;
    }

    @PostMapping(value = "/booking/pay")
    public OutputData postPayBooking(@RequestBody Map<String,Object> requestParams) throws ParseException{
        OutputData df = new OutputData();
        ObjectMapper mapper = new ObjectMapper();
        Long bookingId = mapper.convertValue(requestParams.get("bookingId"),Long.class);
        Double totalPayment = mapper.convertValue(requestParams.get("totalPayment"),Double.class);

        try {
            Payment payment = bookingService.payBooking(bookingId, totalPayment);
            df.setResultCode(OutputData.ResultCode.RESULT_OK);
            df.setReturnedValue(payment);
        } catch (InsertException e) {
            df.setResultCode(e.getExceptionCode());
        }

        return df;
    }

    @PostMapping(value = "/booking/rentItem")
    public OutputData postRentItem(@RequestBody Map<String,Object> requestParams) throws ParseException {
        OutputData df = new OutputData();
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat  sf = new SimpleDateFormat("dd/MM/yyyy");
        Long sojournId = mapper.convertValue(requestParams.get("sojournId"),Long.class);
        Long itemId = mapper.convertValue(requestParams.get("itemId"),Long.class);
        Date startRent = sf.parse((String)requestParams.get("startRent"));
        Date endRent = sf.parse((String)requestParams.get("endRent"));

        try {
            SojournItem sojournItem = itemService.bookItem(sojournId, itemId, startRent, endRent);
            df.setResultCode(OutputData.ResultCode.RESULT_OK);
            df.setReturnedValue(sojournItem);
        } catch (InsertException e) {
            df.setResultCode(e.getExceptionCode());
        }

        return df;
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
	"maxBudget": 700.0,
	"people": 3,
	"onlyRegion": "Sardegna",
	"onlyNotRegion": "Lombardia",
	"maxStars": 4,
	"minStars": 1,
	"tourismTypes":
		[
			"balneare", "enogastronomico"
		],
	"arrival": "8/07/1921",
    "departure": "11/08/1921"
}

 */
