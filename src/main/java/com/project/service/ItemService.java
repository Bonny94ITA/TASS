package com.project.service;

import com.project.controller.exception.InsertException;
import com.project.model.Sojourn;
import com.project.model.SojournItem;
import com.project.repository.SojournItemRepository;
import com.project.repository.SojournRepository;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class ItemService implements IItemService {

    @Autowired
    private SojournRepository sojournRepository;
    @Autowired
    private SojournItemRepository sojournItemRepository;

    private static String serviceUrl = "https://safe-escarpment-32688.herokuapp.com/";

    @Override
    public ResponseEntity<String> searchItem(String string, Date startRent, Date endRent) throws JSONException {
        RestTemplate restTemplate = new RestTemplate();
        String url = serviceUrl+"sp/search/"+string;
        HttpEntity<Message> h = new  HttpEntity<>(createMessage(startRent, endRent));
        return restTemplate.postForEntity(url, h, String.class);
    }

    public ResponseEntity<String> rentItem(Long sojournId, Long productId, Date startRent, Date endRent)
            throws InsertException {
        RestTemplate restTemplate = new RestTemplate();
        String url = serviceUrl+" /sp/rent/product/"+productId;
        HttpEntity<Message> h = new  HttpEntity<>(createMessage(startRent, endRent));
        ResponseEntity<String> response = restTemplate.postForEntity(url, h, String.class);
        if (response.getStatusCode() == HttpStatus.OK){
            this.bookItem(sojournId, productId, startRent, endRent);
        }
        return response;
    }

    private Message createMessage(Date startRent, Date endRent){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return new Message(sf.format(startRent), sf.format(endRent));
    }

    private SojournItem bookItem(Long sojournId, Long itemId, Date startRent, Date endRent) throws InsertException {
        Optional<Sojourn> sojourn = sojournRepository.findById(sojournId);

        if (!sojourn.isPresent()) {
            throw new InsertException("Sojourn with id. " + sojournId + " not found.");
        }

        SojournItem sojournItem = new SojournItem();
        sojournItem.setItem(itemId);
        sojournItem.setSojourn(sojourn.get());
        sojournItem.setStartRent(startRent);
        sojournItem.setEndRent(endRent);

        return sojournItemRepository.save(sojournItem);
    }
}

class Message{
    public String startDate;
    public String endDate;

    public Message(String s, String e){
        this.startDate=s;
        this.endDate=e;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
