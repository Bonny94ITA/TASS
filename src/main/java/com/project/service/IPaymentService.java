package com.project.service;

import com.project.controller.DataException.InsertException;
import com.project.model.Payment;

public interface IPaymentService {
    Payment addPayment(Payment payment);
}
