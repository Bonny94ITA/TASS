package com.project.controller;

import java.util.*;

import com.project.Authentication.AuthenticationUtils;
import com.project.controller.DataException.InsertException;
import com.project.model.Booking;
import com.project.model.Guest;
import com.project.service.IGuestService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@CrossOrigin(origins = "*")
@RestController
public class GuestController {

    @Autowired
    IGuestService guestService;

    @GetMapping("/guests/bookings/{id}")
    public ResponseEntity<?> getMyBookings(@RequestBody Map<String,Object> requestParams, @PathVariable @NotNull Long id) {
        String token = (String)((LinkedHashMap<String, Object>)requestParams.get("token_info")).get("token");
        int tokenType = (Integer)((LinkedHashMap<String, Object>)requestParams.get("token_info")).get("type");

        if (AuthenticationUtils.checkTokenIntegrity(token, tokenType)) {
            List<Booking> bookings = guestService.getBookings(id);
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } else return new ResponseEntity<> ("Invalid Token", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping(value = "/guests/register")
    public ResponseEntity<?> postRegisterGuest(@RequestBody Guest u) {
        try {
            Guest guest = guestService.addGuest(u);
            return new ResponseEntity<>(guest, HttpStatus.OK);
        } catch (InsertException e) {
            return new ResponseEntity<>(e.getExceptionDescription(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Ogetto guest se tutto ok, -1 Password sbagliata, -2 utente inesistente
    @PostMapping(value = "/guests/login")
    public ResponseEntity<?> postLoginGuest(@RequestBody Map<String,Object> requestParams) throws JSONException {
        int ttlToken = 100000000;
        String email = (String)requestParams.get("email");
        String pwd = (String)requestParams.get("pwd");
        Object loginValue = guestService.login(email, pwd);

        if (loginValue != null) {
            Map<String, Object> results = new HashMap<>();
            Map<String, Object> wrapper = new HashMap<>();

            String jwt =
                    AuthenticationUtils.createJWT("1", "localhost:8080", ((Guest) loginValue).getId().toString(), ttlToken);

            results.put("id", ((Guest) loginValue).getId());
            results.put("email", ((Guest) loginValue).getEmail());
            results.put("name", ((Guest) loginValue).getName());

            wrapper.put("token", jwt);
            wrapper.put("guest", results);
            return new ResponseEntity<>(wrapper, HttpStatus.OK);
        } else return new ResponseEntity<>("Fail to login.", HttpStatus.UNAUTHORIZED);
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
