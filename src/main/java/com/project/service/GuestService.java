package com.project.service;

import com.project.controller.exception.InsertException;
import com.project.model.Booking;
import com.project.model.Guest;
import com.project.repository.BookingRepository;
import com.project.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GuestService implements IGuestService {

    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private BookingRepository bookingRepository;

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
    public Guest findByEmail(String email) {
        Optional<Guest> guest = guestRepository.findByEmail(email);
        return guest.isPresent() ? guest.get() : null;
    }

    @Override
    public Guest addGuest(Guest g) throws InsertException {
        Guest newGuest = guestRepository.save(new Guest(g.getEmail(), g.getName(), g.getPwd(), g.getSocial_auth()));

        if (newGuest == null) {
            throw new InsertException("Error in creating new guest.");
        }

        return newGuest;
    }

    @Override
    public Guest login(String email, String pwd) {
        Optional<Guest> guest = guestRepository.findByEmail(email);

        if (guest.isPresent() && pwd.equals(guest.get().getPwd()))
            return guest.get();

        return null;
    }

    @Override
    public void addBooking(Long id, Booking booking) {
        Guest g = findById(id);
        g.addBooking(booking);
        guestRepository.save(g);
    }
}
