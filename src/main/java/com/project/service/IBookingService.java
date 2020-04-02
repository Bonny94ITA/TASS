package com.project.service;

import com.project.model.Booking;
import com.project.model.Guest;

import java.util.List;

public interface IBookingService {
    Booking addBook();
    Booking addBook(Booking booking);

    Booking addBook(Booking booking, Long guestId);

    List<Booking> findAll();

    void deleteById(long id);

    void deleteAll();

}
