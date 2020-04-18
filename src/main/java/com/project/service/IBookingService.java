package com.project.service;

import com.project.model.Booking;
import com.project.model.Guest;
import com.project.model.Payment;

import java.util.List;

public interface IBookingService {

    Booking addBook(Booking booking, Long guestId, boolean paid);

    List<Booking> findAll();

    void deleteById(long id);

    void deleteAll();

    Payment payBooking(Long bookingId);
}
