package com.project.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.project.model.Booking;
import com.project.model.Guest;
import com.project.model.Item;
import com.project.repository.GuestRepository;
import com.project.service.IGuestService;
import com.project.service.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GuestController {

    @Autowired
    IGuestService guestService;
    @Autowired
    IItemService itemService;


    @GetMapping("/guests")
    public List<Guest> getAllGuests() {
        return guestService.findAll();
    }
    @GetMapping("/guests/bookings")
    public List<Booking> getMyBookings(Guest g){return guestService.getBookings(g.getId());}

    @PostMapping(value = "/guests/register") // se fallisce a creare hashpsw ritorna null
    public Guest postRegisterGuest(@RequestBody Guest u){ return guestService.addGuest(u); }

    @CrossOrigin
    @PostMapping(value = "/items/register")
    public Item postRegisterItem(@RequestBody Item i){ return itemService.addItem(i); }

    @DeleteMapping("/guests/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable("id") long id) {
        System.out.println("Delete Customer with ID = " + id + "...");

        guestService.deleteById(id);

        return new ResponseEntity<>("Customer has been deleted!", HttpStatus.OK);
    }

    @DeleteMapping("/guests/delete")
    public ResponseEntity<String> deleteAllCustomers() {
        System.out.println("Delete All Customers...");

        guestService.deleteAll();

        return new ResponseEntity<>("All customers have been deleted!", HttpStatus.OK);
    }
}
/*  JSON
GUEST
    POST - localhost:8080/guests/register
    {
		"email":"aaa@aa.ac",
		"name":"luca",
		"pwd":"lululu",
		"username":"luchinobellodemamma"
    }

    DELETE - localhost:8080/guest/1     (1 è l'id da cancellare, non serve json per questo)

ITEM
    POST- localhost:8080/items/register
    {
	"name":"moto2"
    }

 */
