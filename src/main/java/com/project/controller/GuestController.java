package com.project.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.*;

import com.project.authentication.AuthenticationUtils;
import com.project.controller.exception.InsertException;
import com.project.model.Booking;
import com.project.model.Guest;
import com.project.service.IGuestService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

@CrossOrigin(origins = "*")
@RestController
public class GuestController {

    @Autowired
    private IGuestService guestService;

    private static class CheckIntegrityTokenFilter extends GenericFilterBean {

        public CheckIntegrityTokenFilter () {}

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            System.out.println(((HttpServletRequest) request).getMethod());
            try {
                if(!"OPTIONS".equals(((HttpServletRequest) request).getMethod()) &&
                        !"CONNECT".equals(((HttpServletRequest) request).getMethod()) &&
                        !"TRACE".equals(((HttpServletRequest) request).getMethod())) {
                    JSONObject token_info = new JSONObject(((HttpServletRequest) request).getHeader("token_info"));
                    String token = token_info.getString("token");
                    Integer tokenType = token_info.getInt("type");

                    if (!AuthenticationUtils.checkTokenIntegrity(token, tokenType)) {
                        HttpServletResponse resp = ((HttpServletResponse) response);
                        resp.setHeader("Access-Control-Allow-Origin", ((HttpServletRequest) request).getHeader("Origin"));
                        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                    } else chain.doFilter(request, response);
                } else chain.doFilter(request, response);
            } catch (JSONException e) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Bean
    private FilterRegistrationBean<GuestController.CheckIntegrityTokenFilter> loggingFilter(){
        FilterRegistrationBean<GuestController.CheckIntegrityTokenFilter> registrationBean
                = new FilterRegistrationBean<>();

        GuestController.CheckIntegrityTokenFilter filter = new GuestController.CheckIntegrityTokenFilter();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/guests/socialLogin");

        return registrationBean;
    }

    @PostMapping(value = "/guests/register")
    public ResponseEntity<?> postRegisterGuest(@RequestBody Guest u) {
        u.setSocial_auth(false);

        try {
            Guest guest = guestService.addGuest(u);
            return new ResponseEntity<>("Sign up successful", HttpStatus.OK);
        } catch (InsertException e) {
            return new ResponseEntity<>(e.getExceptionDescription(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/guests/socialLogin")
    public ResponseEntity<?> postSocialLogin(@RequestBody Guest u) throws NoSuchAlgorithmException {
        Guest g = guestService.findByEmail(u.getEmail());
        if (g == null) {
            String tmp = String.valueOf(u.hashCode() + LocalDateTime.now().getNano());
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] messageDigest = md.digest(tmp.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            u.setPwd(no.toString(16));
            u.setSocial_auth(true);

            try {
                Guest guest = guestService.addGuest(u);
                return new ResponseEntity<>(guest.getId(), HttpStatus.OK);
            } catch (InsertException e) {
                return new ResponseEntity<>(e.getExceptionDescription(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else return new ResponseEntity<>(g.getId(), HttpStatus.OK);
    }

    //Ogetto guest se tutto ok, -1 Password sbagliata, -2 utente inesistente
    @PostMapping(value = "/guests/login")
    public ResponseEntity<?> postLoginGuest(@RequestBody Map<String,Object> requestParams) throws JSONException,
            NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        System.out.println(requestParams);
        int ttlToken = 3600000; /// 1 ora in msec
        String email = (String)requestParams.get("email");
        String pwd = (String)requestParams.get("pwd");
        Guest loginValue = guestService.login(email, pwd);

        if (loginValue != null) {
            System.out.println("OK");
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            Map<String, Object> results = new HashMap<>();
            Map<String, Object> wrapper = new HashMap<>();
            Random rnd = new Random();
            byte[] messageDigest = md.digest((String.valueOf(rnd.nextDouble()) + loginValue.getEmail()).getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);

            String jwt =
                    AuthenticationUtils.createJWT(hashtext, "localhost:8433",
                            loginValue.getId().toString(), ttlToken);

            results.put("id", loginValue.getId());
            results.put("email", loginValue.getEmail());
            results.put("name", loginValue.getName());

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
