package com.project.service;

import com.project.controller.exception.InsertException;
import com.project.model.Sojourn;
import com.project.model.SojournItem;
import com.project.model.Item;
import com.project.repository.ItemRepository;
import com.project.repository.SojournItemRepository;
import com.project.repository.SojournRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class ItemService implements IItemService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private SojournRepository sojournRepository;
    @Autowired
    private SojournItemRepository sojournItemRepository;

    private String serviceUrl = "https://safe-escarpment-32688.herokuapp.com/";

    @Override
    public Item addItem(Item i) {
        return itemRepository.save(new Item(i.getName()));
    }

    @Override
    public SojournItem bookItem(Long sojournId, Long itemId, Date startRent, Date endRent) throws InsertException {
        //Optional<Item> item = itemRepository.findById(itemId);
        Optional<Sojourn> sojourn = sojournRepository.findById(sojournId);

        if (!sojourn.isPresent()) {
            throw new InsertException("Sojourn with id. " + sojournId + " not found.");
        }
        /*if (sojourn.get().getArrival().compareTo(startRent) == 1 ||
                sojourn.get().getDeparture().compareTo(endRent) == -1) {
            throw new InsertException("Wrong time frame.");
        }*/

        SojournItem sojournItem = new SojournItem();
        sojournItem.setItem(itemId);
        sojournItem.setSojourn(sojourn.get());
        sojournItem.setStartRent(startRent);
        sojournItem.setEndRent(endRent);

        return sojournItemRepository.save(sojournItem);
    }

    @Override
    public Item findById(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        return item.isPresent() ? item.get() : null;
    }

    @Override
    public ResponseEntity<String> getRentedItem(){
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForEntity(serviceUrl+"sp/rent/sent", String.class);
    }

    // metodi tommy
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
        this.bookItem(sojournId, productId, startRent, endRent);
        HttpEntity<Message> h = new  HttpEntity<>(createMessage(startRent, endRent));
        return restTemplate.postForEntity(url, h, String.class);
    }

    private Message createMessage(Date startRent, Date endRent){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return new Message(sf.format(startRent), sf.format(endRent));
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
