package com.project.service;

import com.project.controller.exception.InsertException;
import com.project.model.Item;
import com.project.model.SojournItem;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public interface IItemService {

    Item findById(Long itemId);

    Item addItem(Item i);

    SojournItem bookItem(Long sojournId, Long itemId, Date startRent, Date endRent) throws InsertException;

    ResponseEntity<String> getRentedItem();

    ResponseEntity<String> searchItem(String string, Date startRent, Date endRent) throws JSONException;

    ResponseEntity<String> rentItem(Long SojournId, Long productId, Date startRent, Date endRent) throws InsertException;
}
