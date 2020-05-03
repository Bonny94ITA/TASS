package com.project.service;

import com.project.controller.DataException.InsertException;
import com.project.controller.DataFormatter.OutputData;
import com.project.model.Booking;
import com.project.model.Guest;
import com.project.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Guest newGuest = null;
        newGuest = guestRepository.save(new Guest(g.getEmail(), g.getName(),
                g.getPwd(), g.getUsername()));
        return newGuest;
    }

    @Override
    public Object login(String email, String pwd) {
        Optional<Guest> guest = guestRepository.findByEmail(email);

        if (guest.isPresent()) {
            return (pwd.equals(guest.get().getPwd())) ? guest : new Integer(-1);
        } else
            return new Integer(-2);
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
        g.addBooking(booking);
        guestRepository.save(g);
    }
}
