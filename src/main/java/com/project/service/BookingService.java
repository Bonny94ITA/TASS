package com.project.service;

import com.project.model.*;
import com.project.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService implements IBookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private GuestService guestService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private SojournService sojournService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private PaymentService paymentService;


    @Override
    public Booking addBook(Booking booking, Long guestId, boolean paid){
        //cerco items
        List<Item> itemsFound = new ArrayList();
        for (Item i : booking.getRentedItems()) {
            Long itemId = i.getId();
            Item item = itemService.findById(itemId);
            //if(item == null)
                //throw exception
            itemsFound.add(item);
        }

        //create e salvo sojourn
        List<Sojourn> sojournsFound = new ArrayList<>();
        for(Sojourn s : booking.getSojourns()){
            //cerco room
            Room room = roomService.findById(s.getRoom().getId());
            //if(room == null)
                //throw exception
            s.setRoom(room);
            Sojourn soj = sojournService.addSojourn(s);
            sojournsFound.add(soj);
        }

        //creo e salvo booking
        Booking b = bookingRepository.save(new Booking(sojournsFound,itemsFound));
        //creo e salvo payment
        if(paid)
            paymentService.addPayment(new Payment(450, b));       //TOTAL COST DA CALCOLARE
        //aggiorno guest
        guestService.addBooking(guestId,b);
        return b;
    }

    @Override
    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        bookingRepository.findAll().forEach(bookings::add);
        return bookings;
    }

    @Override
    public void deleteById(long id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        bookingRepository.deleteAll();
    }

    @Override
    public Payment payBooking(Long bookingId){
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        //if(!booking.isPresent())
            //throw exception
        return paymentService.addPayment(new Payment(400,booking.get()));
    }

}
