package com.project.service;

import com.project.controller.exception.DeleteException;
import com.project.controller.exception.InsertException;
import com.project.model.Booking;
import com.project.model.Payment;
import com.project.model.Sojourn;

import java.util.List;

public interface IBookingService {

    Booking addBook(Booking booking, Long guestId) throws InsertException;

    Booking findById(Long id);

    Booking addSojournToExistingBooking(Long bookingId, Sojourn soj) throws InsertException;

    List<Booking> findAll();

    void deleteById(long id) throws DeleteException;

    void deleteAll();

    List<Booking> getSavedBooking(Long id);

    List<Booking> getPayedBooking(Long id);

    List<Long> getBookingsID(Long id);

    Payment payBooking(Long bookingId, Double totalPayment) throws InsertException;
}
