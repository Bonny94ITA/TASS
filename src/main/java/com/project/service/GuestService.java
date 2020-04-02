package com.project.service;

import com.project.model.Booking;
import com.project.model.Guest;
import com.project.repository.GuestRepository;
import com.project.security.PasswordHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GuestService implements IGuestService {

    @Autowired
    private GuestRepository guestRepository;

    @Override
    public List<Guest> findAll() {
        List<Guest> customers = new ArrayList<>();
        guestRepository.findAll().forEach(customers::add);
        return customers;
    }

    @Override
    public Guest findById(Long id) {
        Optional<Guest> guest = guestRepository.findById(id);
        return guest.isPresent() ? guest.get() : null;
    }


    @Override
    public Guest addGuest(Guest g) {
        PasswordHash ph = new PasswordHash(g.getPwd());

        Guest newGuest = null;
        try {
            newGuest = guestRepository.save(new Guest(g.getEmail(), g.getName(),
                    ph.getPwdHash(), g.getUsername()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return newGuest;
    }

    @Override
    public void deleteById(long id) {
        guestRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        guestRepository.deleteAll();
    }

    @Override
    public List<Booking> getBookings(Long id) {
        Guest guest = findById(id);
        if(guest != null)
            return guest.getBooking();
        else
            return new ArrayList<>();
    }

    @Override
    public void addBooking(Long id, Booking booking) {
        Guest g = findById(id);
        if(g != null) {
            g.addBooking(booking);
            guestRepository.save(g);
        }
    }
}
