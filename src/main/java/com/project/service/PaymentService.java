package com.project.service;

import com.project.controller.DataException.InsertException;
import com.project.controller.DataFormatter.OutputData;
import com.project.model.Payment;
import com.project.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentService implements IPaymentService {
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    PaymentRepository bookingRepository;

    @Override
    public Payment addPayment(Payment payment) {
        return paymentRepository.save(new Payment(payment.getTotalCost(), payment.getBooking()));
    }
}
