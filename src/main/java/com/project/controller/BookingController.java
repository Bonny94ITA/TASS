package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.authentication.AuthenticationUtils;
import com.project.controller.exception.InsertException;
import com.project.model.Alternative;
import com.project.model.Booking;
import com.project.model.Payment;
import com.project.model.SojournItem;
import com.project.service.IBookingService;
import com.project.service.IGuestService;
import com.project.service.IItemService;
import com.project.service.ISecretSearch;
import ilog.concert.IloException;
import net.sf.clipsrules.jni.CLIPSException;
import netscape.javascript.JSObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.http.HTTPException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
public class BookingController {
//PROBLEMA: ITEM DEVONO ESSERE ASSOCIABILI A PIù PRENOTAZIONI, INOLTRE DEV'ESSERCI LA DATA DELLA PRENOTAZIONE
    //controllare che se lancia un'eccezione non deve salvare nulla in database (forse lo fa in automatico)

    @Autowired
    private IGuestService guestService;
    @Autowired
    private IBookingService bookingService;
    @Autowired
    private IItemService itemService;

    private static class CheckIntegrityTokenFilter extends GenericFilterBean {

        public CheckIntegrityTokenFilter () {}

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            try {
                JSONObject obj = new JSONObject(((HttpServletRequest)request).getHeader("token_info"));
                String token = obj.getString("token");
                Integer tokenType = obj.getInt("type");

                if (!AuthenticationUtils.checkTokenIntegrity(token, tokenType)) {
                    ((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                            "Invalid Token");
                } else {
                    chain.doFilter(request, response);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Bean
    private FilterRegistrationBean<CheckIntegrityTokenFilter> loggingFilter(){
        FilterRegistrationBean<CheckIntegrityTokenFilter> registrationBean
                = new FilterRegistrationBean<>();

        CheckIntegrityTokenFilter filter = new CheckIntegrityTokenFilter();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/bookings/*");

        return registrationBean;
    }

    @GetMapping("/bookings/{guest_id}")
    public ResponseEntity<?> getMyBookings(@RequestBody Map<String,Object> requestParams, @PathVariable @NotNull Long guest_id) {
        List<Booking> bookings = guestService.getBookings(guest_id);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    //da fare: quando i parametri passati non sono in database ritorna null, quando non passiamo i parametri la get su json torna null
    @PostMapping(value = "/bookings/insert")
    public ResponseEntity<?> postRegisterBooking(@RequestBody Map<String, Object> requestParams) throws ParseException {
        ObjectMapper mapper = new ObjectMapper();
        Long guestId = mapper.convertValue(requestParams.get("guest"), Long.class);
        //boolean paymentId = mapper.convertValue(requestParams.get("payment"),Boolean.class);
        Booking booking = mapper.convertValue(requestParams.get("booking"), Booking.class);

        try {
            Booking b = bookingService.addBook(booking, guestId);
            return new ResponseEntity<>(b, HttpStatus.OK);
        }
        catch (InsertException e) {
            return new ResponseEntity<>(e.getExceptionDescription(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/booking/pay")
    public ResponseEntity<?> postPayBooking(@RequestBody Map<String, Object> requestParams) throws ParseException{
        ObjectMapper mapper = new ObjectMapper();
        Long bookingId = mapper.convertValue(requestParams.get("bookingId"), Long.class);
        Double totalPayment = mapper.convertValue(requestParams.get("totalPayment"), Double.class);

        try {
            Payment payment = bookingService.payBooking(bookingId, totalPayment);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (InsertException e) {
            return new ResponseEntity<>(e.getExceptionDescription(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/bookings/rentItem")
    public ResponseEntity<?> postRentItem(@RequestBody Map<String,Object> requestParams) throws ParseException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
        Long sojournId = mapper.convertValue(requestParams.get("sojournId"), Long.class);
        Long itemId = mapper.convertValue(requestParams.get("itemId"), Long.class);
        Date startRent = sf.parse((String) requestParams.get("startRent"));
        Date endRent = sf.parse((String) requestParams.get("endRent"));

        try {
            SojournItem sojournItem = itemService.bookItem(sojournId, itemId, startRent, endRent);
            return new ResponseEntity<>(sojournItem, HttpStatus.OK);
        } catch (InsertException e) {
            return new ResponseEntity<>(e.getExceptionDescription(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
