package com.project.service;

import com.project.controller.exception.InsertException;
import com.project.model.Booking;
import com.project.model.Guest;

import java.util.List;

public interface IGuestService{
    List<Guest> findAll();

    Guest findById(Long id);

    Guest findByEmail(String email);

    Guest addGuest(Guest guest) throws InsertException;

    Guest login(String email, String pwd);

    List<Booking> getBookings(Long id);

    void addBooking(Long id, Booking booking);
}
