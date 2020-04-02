package com.project.service;

import com.project.model.Booking;
import com.project.model.Guest;

import java.util.List;

public interface IGuestService{
    List<Guest> findAll();

    Guest findById(Long id);

    Guest addGuest(Guest guest);

    void deleteById(long id);

    void deleteAll();

    List<Booking> getBookings(Long id);

    void addBooking(Long id, Booking booking);
}
