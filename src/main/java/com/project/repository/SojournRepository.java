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
    @Query(value = "SELECT room.* " +
                   "FROM sojourn, room, hotel " +
                   "WHERE (:arrival <= sojourn.departure OR :departure >= sojourn.arrival) AND " +
                          "sojourn.room = room.id AND " +
                          "hotel.id = room.hotel AND " +
                          "NOT EXISTS (SELECT * FROM payment, booking " +
                                     "WHERE payment.booking = booking.id AND " + "booking.id = sojourn.booking) " +
                   "UNION " +
                   "SELECT room.* " +
                   "FROM room, hotel " +
                   "WHERE hotel.id = room.hotel AND " +
                         "NOT EXISTS (SELECT * FROM booking, sojourn " +
                                     "WHERE sojourn.booking = booking.id AND sojourn.room = room.id)", nativeQuery = true)
    List<Object[]> findAllRoom(@Param("arrival") Date arrival,@Param("departure") Date departure);

    //query per ricerca normale stanze libere, giorni ,città
    @Query(value = "SELECT room.* " +
                   "FROM sojourn, room, hotel, city " +
                   "WHERE (:arrival <= sojourn.departure OR :departure >= sojourn.arrival) AND " +
                          "sojourn.room = room.id AND " +
                          "hotel.id = room.hotel AND " +
                          "city.id = hotel.city AND " +
                          "city.name = :city AND " +
                          "NOT EXISTS (SELECT * FROM payment, booking " +
                                      "WHERE payment.booking = booking.id AND " + "booking.id = sojourn.booking) " +
                   "UNION " +
                   "SELECT room.* " +
                   "FROM room, hotel, city " +
                   "WHERE hotel.id = room.hotel AND " +
                         "city.id = hotel.city AND " +
                         "city.name = :city AND " +
                         "NOT EXISTS (SELECT * FROM booking, sojourn " +
                                     "WHERE sojourn.booking = booking.id AND sojourn.room = room.id)", nativeQuery = true)
    List<Object[]> findAllRoom(@Param("arrival") Date arrival,@Param("departure") Date departure,@Param("city") String cityName);
}




