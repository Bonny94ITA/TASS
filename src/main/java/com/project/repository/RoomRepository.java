package com.project.repository;

import com.project.model.Hotel;
import com.project.model.Room;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RoomRepository extends CrudRepository<Room, Long> {
    List<Room> findByHotel(Hotel h);
    @Query(value =
            "SELECT room.* " +
            "FROM room " +
            "WHERE room.id NOT IN " +
                "(SELECT DISTINCT room.id " +
                "FROM sojourn, room, hotel, city " +
                "WHERE NOT (:departure < sojourn.arrival OR :arrival > sojourn.departure) AND " +
                "sojourn.room = room.id AND " +
                "hotel.id = room.hotel AND " +
                "city.id = hotel.city AND " +
                "city.name = :city AND " +
                "EXISTS (SELECT * FROM payment, booking " +
                "WHERE payment.booking = booking.id AND " + "booking.id = sojourn.booking))", nativeQuery = true)
    List<Room> findAllFreeRoomsIn(@Param("arrival") Date arrival, @Param("departure") Date departure, @Param("city") String cityName);

    @Query(value = "SELECT room.* " +
                   "FROM room " +
                   "WHERE room.id NOT IN " +
                        "(SELECT DISTINCT room.id " +
                        "FROM sojourn, room " +
                        "WHERE NOT (:departure < sojourn.arrival OR :arrival > sojourn.departure) AND " +
                        "sojourn.room = room.id AND " +
                        "EXISTS (SELECT * FROM payment, booking " +
                        "WHERE payment.booking = booking.id AND booking.id = sojourn.booking))", nativeQuery = true)
    List<Room> findAllFreeRoomsIn(@Param("arrival") Date arrival, @Param("departure") Date departure);

}