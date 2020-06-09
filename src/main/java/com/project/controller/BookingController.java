package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.authentication.AuthenticationUtils;
import com.project.controller.exception.DeleteException;
import com.project.controller.exception.InsertException;
import com.project.model.Booking;
import com.project.model.Payment;
import com.project.model.Sojourn;
import com.project.model.SojournItem;
import com.project.service.IBookingService;
import com.project.service.IGuestService;
import com.project.service.IItemService;
import com.project.websocket.SharedModel;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class BookingController {
//PROBLEMA: ITEM DEVONO ESSERE ASSOCIABILI A PIù PRENOTAZIONI, INOLTRE DEV'ESSERCI LA DATA DELLA PRENOTAZIONE
    //controllare che se lancia un'eccezione non deve salvare nulla in database (forse lo fa in automatico)

    @Autowired
    private IBookingService bookingService;
    @Autowired
    private IItemService itemService;

    public static SharedModel sharedModel = new SharedModel();

    private static class CheckIntegrityTokenFilter extends GenericFilterBean {

        public CheckIntegrityTokenFilter () {}

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            System.out.println(((HttpServletRequest) request).getMethod());
            try {
                if(!"OPTIONS".equals(((HttpServletRequest) request).getMethod()) &&
                        !"CONNECT".equals(((HttpServletRequest) request).getMethod()) &&
                        !"TRACE".equals(((HttpServletRequest) request).getMethod())) {
                    JSONObject token_info = new JSONObject(((HttpServletRequest) request).getHeader("token_info"));
                    String token = token_info.getString("token");
                    Integer tokenType = token_info.getInt("type");

                    if (!AuthenticationUtils.checkTokenIntegrity(token, tokenType)) {
                        HttpServletResponse resp = ((HttpServletResponse) response);
                        resp.setHeader("Access-Control-Allow-Origin", ((HttpServletRequest) request).getHeader("Origin"));
                        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                    } else chain.doFilter(request, response);
                } else chain.doFilter(request, response);
            } catch (JSONException e) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        e.getMessage());
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

    @GetMapping("/bookings/paid/{guest_id}")
    public ResponseEntity<?> getMyPaidBookings(@PathVariable @NotNull Long guest_id) {
        List<Booking> bookings = bookingService.getPayedBooking(guest_id);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/bookings/saved/{guest_id}")
    public ResponseEntity<?> getMyFreeBookings(@PathVariable @NotNull Long guest_id) {
        List<Booking> bookings = bookingService.getSavedBooking(guest_id);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/bookings/id/{guest_id}")
    public ResponseEntity<?> getMyBookingsID(@PathVariable @NotNull Long guest_id) {
        List<Long> bookings = bookingService.getBookingsID(guest_id);
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
            sendNotificationToClient(Math.toIntExact(guestId), b.getId().toString());
            return new ResponseEntity<>(b, HttpStatus.OK);
        }
        catch (InsertException e) {
            return new ResponseEntity<>(e.getExceptionDescription(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "bookings/addSojourn")
    public ResponseEntity<?> addSojournToExistingBooking(@RequestBody Map<String, Object> requestParams) {
        ObjectMapper mapper = new ObjectMapper();
        Long bookingId = mapper.convertValue(requestParams.get("bookingId"), Long.class);
        Sojourn sojourn = mapper.convertValue(requestParams.get("sojourn"), Sojourn.class);

        try {
            Booking b = bookingService.addSojournToExistingBooking(bookingId, sojourn);
            return new ResponseEntity<>(b, HttpStatus.OK);
        } catch (InsertException e) {
            return new ResponseEntity<>(e.getExceptionDescription(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/bookings/pay")
    public ResponseEntity<?> postPayBooking(@RequestBody Map<String, Object> requestParams) throws ParseException,
            IOException {
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

    @DeleteMapping(value ="/bookings/delete/{booking_id}")
    public ResponseEntity<?> deleteBooking(@PathVariable @NotNull Long booking_id){
        try{
            bookingService.deleteById(booking_id);
            return new ResponseEntity<>("deleted booking", HttpStatus.OK);
        }catch (DeleteException e) {
            return new ResponseEntity<>(e.getExceptionDescription(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "bookings/items/searchItem")
    public ResponseEntity<?> searchItem(@RequestBody Map<String, Object> requestParams){
        try {
            ObjectMapper mapper = new ObjectMapper();
            String stringToSearch = mapper.convertValue(requestParams.get("stringToSearch"), String.class);
            SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = sf.parse((String)requestParams.get("startDate"));
            Date returnDate = sf.parse((String)requestParams.get("endDate"));

            return itemService.searchItem(stringToSearch, startDate, returnDate);
        } catch (ParseException | JSONException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "bookings/items/rentItem")
    public ResponseEntity<?> rentItem(@RequestBody Map<String, Object> requestParams){
        try {
            ObjectMapper mapper = new ObjectMapper();
            Long stringToSearch = mapper.convertValue(requestParams.get("productId"), Long.class);
            SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = sf.parse((String)requestParams.get("startDate"));
            Date returnDate = sf.parse((String)requestParams.get("endDate"));

            return itemService.rentItem(stringToSearch, startDate, returnDate);
        } catch (ParseException | JSONException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    // TODO => CANCELLARE
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

    private void sendNotificationToClient(int clientId, String notification) {
        Runnable sendMessageThread = () -> {
            int millsBeforeClientSynchronized = 1000;
            WebSocketSession session;

            session = sharedModel.getSocketClients().get(clientId);
            if (session == null || !session.isOpen()) {
                try {
                    Thread.sleep(millsBeforeClientSynchronized);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                session = sharedModel.getSocketClients().get(clientId);

                if (session != null && session.isOpen()) {
                    System.out.println("primple");
                    sharedModel.getSocketClients().get(clientId).sendMessage(new TextMessage(notification));
                } /*else if (!sharedModel.getSocketClients().get(clientId).isOpen()) {
                    System.out.println("chiusa");
                }*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        sendMessageThread.run();
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
