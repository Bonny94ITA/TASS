package com.project.controller;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.model.Guest;
import com.project.repository.GuestRepository;
import com.project.security.PasswordHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GuestController {

    @Autowired
    GuestRepository guestRepository;

    @GetMapping("/guests")
    public List<Guest> getAllGuests() {
        List<Guest> customers = new ArrayList<>();
        guestRepository.findAll().forEach(customers::add);

        return customers;
    }

    @PostMapping(value = "/guests/register")
    public Guest postCustomer(@RequestBody Guest g) throws NoSuchAlgorithmException {
        PasswordHash ph = new PasswordHash(g.getPwd());

        Guest guest = guestRepository.save(new Guest(g.getEmail(), g.getName(),
                ph.getPwdHash(), g.getUsername()));
        return guest;
    }

    @PostMapping(value = "/guests/login")
    public String postCustomer(@RequestBody Map<String, Object> requestParams) throws NoSuchAlgorithmException {
        ObjectMapper mapper = new ObjectMapper();

        Long id = mapper.convertValue(requestParams.get("id"), Long.class);
        String pwd = mapper.convertValue(requestParams.get("pwd"), String.class);
        PasswordHash ph = new PasswordHash(pwd);

        Optional<Guest> guest = guestRepository.findById(id);

        if (!guest.isPresent()) {
            return new String("NOT PRESENT");
        } else {
            if (ph.getPwdHash().equals(guest.get().getPwd())) {
                return "OK";
            } else {
                return "NOT CORRECT";
            }
        }
    }

    @DeleteMapping("/guests/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable("id") long id) {
        System.out.println("Delete Customer with ID = " + id + "...");

        guestRepository.deleteById(id);

        return new ResponseEntity<>("Customer has been deleted!", HttpStatus.OK);
    }

    @DeleteMapping("/guests/delete")
    public ResponseEntity<String> deleteAllCustomers() {
        System.out.println("Delete All Customers...");

        guestRepository.deleteAll();

        return new ResponseEntity<>("All customers have been deleted!", HttpStatus.OK);
    }
}
