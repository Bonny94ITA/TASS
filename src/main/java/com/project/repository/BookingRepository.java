package com.project.repository;


import com.project.model.Booking;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Long> {
    /*@Query("INSERT INTO booking (guest, payment) " +
            "VALUES (:guest, :payment)")
    //void insertBooking(
            @Param("guest") Long guest,
            @Param("payment") Long payment);*/
}

