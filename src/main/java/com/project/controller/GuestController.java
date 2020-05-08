package com.project.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.controller.DataException.DeleteException;
import com.project.controller.DataFormatter.OutputData;
import com.project.model.Booking;
import com.project.model.Guest;
import com.project.model.Item;
import com.project.repository.GuestRepository;
import com.project.service.IGuestService;
import com.project.service.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@CrossOrigin(origins = "*")
@RestController
public class GuestController {

    @Autowired
    IGuestService guestService;
    @Autowired
    IItemService itemService;

    @GetMapping("/guests/bookings/{id}")
    public OutputData getMyBookings(@PathVariable @NotNull Long id) {
        OutputData df = new OutputData();
        List<Booking> bookings = guestService.getBookings(id);

        df.setResultCode(OutputData.ResultCode.RESULT_OK);
        df.setReturnedValue(bookings);

        return df;
    }

    @PostMapping(value = "/guests/register") // se fallisce a creare hashpsw ritorna null
    public OutputData postRegisterGuest(@RequestBody Guest u) {
        OutputData df = new OutputData();
        Guest guest = guestService.addGuest(u);

        df.setResultCode(OutputData.ResultCode.RESULT_OK);
        df.setReturnedValue(guest);

        return df;
    }

    //Ogetto guest se tutto ok, -1 Password sbagliata, -2 utente inesistente
    @PostMapping(value = "/guests/login")
    public OutputData postLoginGuest(@RequestBody Map<String,Object> requestParams){
        OutputData df = new OutputData();
        String email = (String)requestParams.get("email");
        String pwd = (String)requestParams.get("pwd");
        Object loginValue = guestService.login(email, pwd);

        df.setResultCode(OutputData.ResultCode.RESULT_OK);
        df.setReturnedValue(loginValue);

        return df;
    }

    @PostMapping(value = "/items/register")
    public OutputData postRegisterItem(@RequestBody Item i) {
        OutputData df = new OutputData();
        Item item = itemService.addItem(i);

        df.setResultCode(OutputData.ResultCode.RESULT_OK);
        df.setReturnedValue(item);

        return df;
    }
}
/*  JSON
GUEST
    POST - localhost:8080/guests/register
    {
		"email":"aaa@aa.ac",
		"name":"luca",
		"pwd":"lululu",
		"username":"luchinobellodemamma"
    }

    GET - localhost:8080/guests/login
    {
		"email":"aaa@aa.ac",
		"pwd":"lululu"
    }

ITEM
    POST- localhost:8080/items/register
    {
	"name":"moto2"
    }

 */
