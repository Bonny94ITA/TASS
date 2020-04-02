package com.project.repository;

import com.project.model.Hotel;
import com.project.model.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends CrudRepository<Room, Long> {
    //List<Guest> findByAge(int age);
    List<Room> findByHotel(Hotel h);
}