package com.project.service;

import com.project.controller.DataException.InsertException;
import com.project.controller.DataException.UpdateException;
import com.project.model.Booking;
import com.project.model.Sojourn;
import com.project.model.SojournItem;
import com.project.model.Item;
import com.project.repository.BookingRepository;
import com.project.repository.ItemRepository;
import com.project.repository.SojournItemRepository;
import com.project.repository.SojournRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public Item addItem(Item i) {
        return itemRepository.save(new Item(i.getName()));
    }

    @Override
    public SojournItem bookItem(Long sojournId, Long itemId, Date startRent, Date endRent) throws InsertException {
        Optional<Item> item = itemRepository.findById(itemId);
        Optional<Sojourn> sojourn = sojournRepository.findById(sojournId);

        if (!item.isPresent()){
            throw new InsertException("Item with id. " + itemId + " not found.");
        } else if (!sojourn.isPresent()) {
            throw new InsertException("Sojourn with id. " + sojournId + " not found.");
        } else if (!itemRepository.checkItemAlreadyRented(itemId, startRent, endRent).isEmpty()) {
            throw new InsertException("Item with id. " + itemId + " already rented.");
        }

        if (sojourn.get().getArrival().compareTo(startRent) == 1 ||
                sojourn.get().getDeparture().compareTo(endRent) == -1) {
            throw new InsertException("Wrong time frame.");
        }

        SojournItem sojournItem = new SojournItem();
        sojournItem.setItem(item.get());
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
}
