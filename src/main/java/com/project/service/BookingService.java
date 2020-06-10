package com.project.service;

import com.project.controller.exception.DeleteException;
import com.project.controller.exception.InsertException;
import com.project.model.*;
import com.project.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingService implements IBookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private GuestService guestService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private SojournService sojournService;
    @Autowired
    private PaymentService paymentService;

    @Override
    public Booking addBook(Booking booking, Long guestId) throws InsertException {
        Booking b;
        Guest g = guestService.findById(guestId);
        List<Sojourn> sojournsFound = new ArrayList<>();
        for(Sojourn s : booking.getSojourns()){
            Room room = roomService.findById(s.getRoom().getId());
            if(room == null)
                throw new InsertException("Room with id. " +
                        s.getRoom().getId() + " not found.");
            s.setRoom(room);
            Sojourn soj = sojournService.addSojourn(s);
            sojournsFound.add(soj);
        }

        if (g != null) {
            b = bookingRepository.save(new Booking(sojournsFound));
            guestService.addBooking(guestId,b);
            return b;
        } else throw new InsertException("Guest with id. " +
                guestId + " not found.");
    }

    @Override
    public Booking addSojournToExistingBooking(Long bookingId, Sojourn soj) throws InsertException{
        Booking b = findById(bookingId);
        if (b == null)
            throw new InsertException("The booking " + bookingId + " does not exist!");
        b.addSojourns(soj);
        bookingRepository.save(b);
        return b;
    }

    @Override
    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        bookingRepository.findAll().forEach(bookings::add);
        return bookings;
    }

    @Override
    public void deleteById(long id) throws DeleteException {
        if (bookingRepository.findById(id).isPresent())
            bookingRepository.deleteById(id);
        else throw new DeleteException("Booking with id. " +
                id + " not found.");
    }

    @Override
    public void deleteAll() {
        bookingRepository.deleteAll();
    }

    @Override
    public Booking findById(Long id) {
        Optional<Booking> b = bookingRepository.findById(id);
        return b.isPresent() ? b.get() : null;
    }

    @Override
    public Payment payBooking(Long bookingId, Double totalPayment) throws InsertException {
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        if(!booking.isPresent())
            throw new InsertException("Booking with id. " +
                    bookingId + " not present.");
        return paymentService.addPayment(new Payment(totalPayment,booking.get()));
    }

    @Override
    public List<Booking> getSavedBooking(Long id) {
        return bookingRepository.findSavedBooking(id);
    }

    @Override
    public List<Booking> getPayedBooking(Long id) {
        return bookingRepository.findPayedBooking(id);
    }

    @Override
    public List<Long> getBookingsID(Long id) {
        List<Booking> bookingList = bookingRepository.findSavedBooking(id);
        return bookingList.stream().map((Booking b) -> b.getId()).collect(Collectors.toList());
    }
}
