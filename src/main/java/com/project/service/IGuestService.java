package com.project.service;

import com.project.model.Booking;
import com.project.model.Guest;

import java.util.List;
import java.util.Map;

public interface IGuestService{
    List<Guest> findAll();

    Guest findById(Long id);

    Guest addGuest(Guest guest);

    Integer login(String email, String pwd);

    void deleteById(long id);

    void deleteAll();

    List<Booking> getBookings(Long id);

    void addBooking(Long id, Booking booking);
}
