package com.project.service;

import com.project.controller.DataException.DeleteException;
import com.project.controller.DataException.InsertException;
import com.project.controller.DataException.UpdateException;
import com.project.model.Booking;
import com.project.model.Guest;
import com.project.model.Payment;
import com.project.model.SojournItem;

import java.util.Date;
import java.util.List;

public interface IBookingService {

    Booking addBook(Booking booking, Long guestId) throws InsertException;

    Booking findById(Long id);

    List<Booking> findAll();

    void deleteById(long id) throws DeleteException;

    void deleteAll();

    Payment payBooking(Long bookingId, Double totalPayment) throws InsertException;
}
