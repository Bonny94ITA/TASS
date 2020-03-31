package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.*;
import com.project.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class BookingsController {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    GuestRepository guestRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    SojournRepository sojournRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    PaymentRepository paymentRepository;

    @RequestMapping(value = "/bookings", method = RequestMethod.POST)
    public List<Booking> getAllBooking() {
        List<Booking> bookings = new ArrayList<>();
        bookingRepository.findAll().forEach(bookings::add);

        return bookings;
    }

    @PostMapping(value = "/guests/login")
    public Guest postCustomer(@RequestBody Map<String, Object> requestParams) throws NoSuchAlgorithmException {
        ObjectMapper mapper = new ObjectMapper();
        Long id = mapper.convertValue(requestParams.get("id"), Long.class);

        if (guestRepository.findById(id).isPresent()) {

        }

        return guest;
    }

    @PostMapping(value = "/guests/register")
    public Guest postCustomer(@RequestBody Guest g) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(g.getPwd().getBytes());
        byte[] bytes = md.digest();
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < bytes.length ; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        String generatedPassword = sb.toString();

        Guest guest = guestRepository.save(new Guest(g.getEmail(), g.getName(),
                generatedPassword, g.getUsername()));
        return guest;
    }

    /*
    {
	"guest": 1,
	"items": [
		{
			 "item": 1
		},
		{
			 "item": 1
		}
	],
	"sojourns": [
		{
			"arrival": "21/05/1921",
			"departure": "21/05/1921",
			"room": 1
		},
		{
			"arrival": "21/05/1921",
			"departure": "21/05/1921",
			"room": 1
		}
	]
}
     */

    @RequestMapping(value = "/bookings/book", method = RequestMethod.POST)
    public String postInsertBooking(@RequestBody Map<String,Object> requestParams) throws ParseException {
        ObjectMapper mapper = new ObjectMapper();
        Long id = mapper.convertValue(requestParams.get("guest"),Long.class);
        Object sojourns = requestParams.get("sojourns");
        Object items = requestParams.get("items");
        Booking booking = bookingRepository.save(new Booking());
        Guest guest = guestRepository.findById(id).get();

        for (LinkedHashMap<String, Object> i : (ArrayList<LinkedHashMap<String, Object>>)items) {
            Long itemId = mapper.convertValue(i.get("item"), Long.class);
            Item item = itemRepository.findById(itemId).get();
            booking.addItem(item);
        }

        //Total cost da calcolare
        Payment payment = new Payment(450, booking);
        guest.addBooking(booking);
        guest.addBooking(booking);

        for (LinkedHashMap<String, Object> s : (ArrayList<LinkedHashMap<String, Object>>)sojourns) {
            Long roomId = mapper.convertValue(s.get("room"), Long.class);
            String arrival = mapper.convertValue(s.get("arrival"), String.class);
            String departure = mapper.convertValue(s.get("departure"), String.class);
            Room room = roomRepository.findById(roomId).get();
            Sojourn sojourn = new Sojourn(new SimpleDateFormat("dd/MM/yyyy").parse(departure),
                    new SimpleDateFormat("dd/MM/yyyy").parse(arrival));
            sojournRepository.save(sojourn);
            sojourn.setRoom(room);
            booking.addSojourn(sojourn);
        }

        guestRepository.save(guest);
        paymentRepository.save(payment);
        bookingRepository.save(booking);

        return "OK";
    }
}
