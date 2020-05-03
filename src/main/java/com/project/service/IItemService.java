package com.project.service;

import com.project.controller.DataException.InsertException;
import com.project.controller.DataException.UpdateException;
import com.project.model.Booking;
import com.project.model.Item;
import com.project.model.Sojourn;
import com.project.model.SojournItem;

import java.util.Date;

public interface IItemService {

    Item findById(Long itemId);

    Item addItem(Item i);

    SojournItem bookItem(Long sojournId, Long itemId, Date startRent, Date endRent) throws InsertException;
}
