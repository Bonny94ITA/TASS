package com.project.service;

import com.project.model.Guest;
import com.project.model.Item;
import com.project.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ItemService implements IItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Item addItem(Item i) {
        return itemRepository.save(new Item(i.getName()));
    }

    @Override
    public Item findById(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        return item.isPresent() ? item.get() : null;
    }

}
