package com.project.repository;

import com.project.model.Booking;
import com.project.model.Guest;
import com.project.model.Payment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BookingRepository extends CrudRepository<Booking, Long> {
    @Modifying
    @Transactional
    @Query(value = "insert into booking (guest) " +
            "VALUES (?1)", nativeQuery = true)
    void insertBooking(
            @Param("guest") Long guest);
}

