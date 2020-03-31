package com.project.repository;

import com.project.model.Item;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ItemRepository extends CrudRepository<Item, Long> {
    @Modifying
    @Transactional
    @Query(value = "insert into item (booking) " +
            "VALUES (?1)", nativeQuery = true)
    void insertItem(
            @Param("booking") Long booking);
}
