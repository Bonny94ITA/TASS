package com.project.service;

import com.project.model.Item;

public interface IItemService {

    Item findById(Long itemId);

    Item addItem(Item i);
}
