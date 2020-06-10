package com.project.service;

import com.project.controller.exception.InsertException;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public interface IItemService {

    ResponseEntity<String> searchItem(String string, Date startRent, Date endRent) throws JSONException;

    ResponseEntity<String> rentItem(Long SojournId, Long productId, Date startRent, Date endRent) throws InsertException;
}
