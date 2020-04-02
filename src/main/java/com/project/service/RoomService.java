package com.project.service;

import com.project.model.Room;
import com.project.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class RoomService implements IRoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public Room findById(Long id) {
        Optional<Room> room = roomRepository.findById(id);
        return room.isPresent() ? room.get() : null;
    }
}
