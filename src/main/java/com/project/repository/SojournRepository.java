package com.project.repository;

import com.project.model.Room;
import com.project.model.Sojourn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SojournRepository  extends CrudRepository<Sojourn, Long> {
    @Query(value = "SELECT room.id, room.num_places, room.ppn, room.hotel " +
                   "FROM sojourn, room, hotel, city " +
                   "WHERE ('17/11/1994' > sojourn.departure OR '25/11/1994' < sojourn.arrival) AND " +
                           "sojourn.room = room.id AND " +
                           "hotel.id = room.hotel AND " +
                           "city.id = hotel.city AND " +
                           "city.name = 'Cagliari' AND " +
                           "NOT EXISTS (SELECT * FROM payment, booking " +
                                       "WHERE payment.booking = booking.id AND " +
                                             "booking.id = sojourn.booking) " +
                    "UNION " +
                    "SELECT room.id, room.num_places, room.ppn, room.hotel " +
                    "FROM room, hotel, city " +
                    "WHERE hotel.id = room.hotel AND " +
                    "city.id = hotel.city AND " +
                    "city.name = 'Cagliari' AND " +
                    "NOT EXISTS (SELECT * FROM booking, sojourn " +
                                "WHERE sojourn.booking = booking.id AND sojourn.room = room.id)", nativeQuery = true)
            List<Room> findAllFreeRoomsIn();
}
