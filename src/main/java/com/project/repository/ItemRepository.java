package com.project.repository;

import com.project.model.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {

    //bisognerò tenere presente che l'arco temporale di prenotazione dell'item sia contenuto nell'arco temporale di prenotazione
    //del sojourn
    @Query(value = "SELECT sojourn_item.item_id " +
                   "FROM sojourn_item " +
                   "WHERE sojourn_item.item_id = :id AND " +
                         "(NOT (:start < sojourn_item.start_rent AND :end < sojourn_item.start_rent)) AND " +
                         "(NOT (:start > sojourn_item.end_rent AND :end > sojourn_item.end_rent))", nativeQuery = true)
    List<BigInteger> checkItemAlreadyRented(@Param("id") Long itemId, @Param("start") Date startRent, @Param("end") Date endRent);
}
