package com.project.repository;

import com.project.model.Room;
import com.project.model.Sojourn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SojournRepository  extends CrudRepository<Sojourn, Long> {
    //query per ricerca clips stanze libere in dati giorni
    //query per ricerca normale stanze libere, giorni ,città
    @Query(value = "SELECT room.*\n" +
            "FROM sojourn, room, hotel, city, booking\n" +
            "WHERE sojourn.arrival = :arrival AND sojourn.departure = :departure AND\n" +
            "    sojourn.room = room.id AND \n" +
            "    hotel.id = room.hotel AND \n" +
            "    city.id = hotel.city AND\n" +
            "    city.name = :city AND \n" +
            "    NOT EXISTS (SELECT * FROM payment \n" +
            "        WHERE payment.booking = booking.id)\n" +
            "    AND booking.id = sojourn.booking", nativeQuery = true)
    List<Room> findAllRoom(@Param("arrival") Date arrival,@Param("departure") Date departure,@Param("city") String cityName);
}
