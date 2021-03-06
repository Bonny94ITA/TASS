package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.authentication.AuthenticationUtils;
import com.project.controller.exception.DeleteException;
import com.project.controller.exception.InsertException;
import com.project.model.Booking;
import com.project.model.Payment;
import com.project.model.Sojourn;
import com.project.service.IBookingService;
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


    /*
    *
    *
    * GETTER BOOKINS
    * */
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

    /*
    *
    *
    * POST INSERT
    * */
    @PostMapping(value = "/bookings/insert")
    public ResponseEntity<?> postRegisterBooking(@RequestBody Map<String, Object> requestParams) {
        ObjectMapper mapper = new ObjectMapper();
        Long guestId = mapper.convertValue(requestParams.get("guest"), Long.class);
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

    /*
    *
    *
    * POST PAY
    * */
    @PostMapping(value = "/bookings/pay")
    public ResponseEntity<?> postPayBooking(@RequestBody Map<String, Object> requestParams) {
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


    /*
    *
    *
    * ITEM SERVICE CALL
    * */
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
            Long sojournId = mapper.convertValue(requestParams.get("sojournId"), Long.class);
            SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = sf.parse((String)requestParams.get("startDate"));
            Date returnDate = sf.parse((String)requestParams.get("endDate"));

            return itemService.rentItem(sojournId, stringToSearch, startDate, returnDate);
        } catch (ParseException | InsertException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    /*
    * NOTIFICATION SECTION
    * */
    private void sendNotificationToClient(int clientId, String notification) {
        List<WebSocketSession> webSocketSessions = sharedModel.getSocketClients().get(clientId);

        if (webSocketSessions != null) {
            for (final WebSocketSession session : webSocketSessions) {
                if (session.isOpen()) {
                    Runnable sendMessageThread = new Runnable() {
                        @Override
                        public void run() {
                            int millsBeforeClientSynchronized = 1000;

                            if (session == null || !session.isOpen()) {
                                try {
                                    Thread.sleep(millsBeforeClientSynchronized);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                if (session != null && session.isOpen())
                                    session.sendMessage(new TextMessage(notification));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    sendMessageThread.run();
                }
            }
        }
    }
}
