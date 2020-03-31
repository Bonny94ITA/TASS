package com.project.repository;

import com.project.model.Payment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    @Modifying
    @Transactional
    @Query(value = "insert into payment (totalCost, booking) " +
            "VALUES (?1, ?2)", nativeQuery = true)
    void insertPayment(
            @Param("totalCost") Long totalCost,
            @Param("booking") Long booking);
}
