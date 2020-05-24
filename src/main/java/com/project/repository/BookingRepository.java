package com.project.repository;


import com.project.model.Booking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Long> {

    @Query(value =
            "SELECT booking.* " +
            "FROM booking " +
            "WHERE booking.guest = :guest AND" +
                    "NOT EXISTS (" +
                        "SELECT * " +
                        "FROM payment " +
                        "WHERE payment.booking = booking.id)", nativeQuery = true)
    List<Booking> findSavedBooking(@Param("guest") Long GuestID);

    @Query(value =
            "SELECT booking.* " +
            "FROM booking, payment " +
            "WHERE booking.id = payment.booking AND" +
                    "booking.guest = :guest", nativeQuery = true)
    List<Booking> findPayedBooking(@Param("guest") Long GuestID);

}

