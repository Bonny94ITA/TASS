package com.project.repository;

import com.project.model.Booking;
import com.project.model.Room;
import com.project.model.Sojourn;
import org.springframework.data.repository.CrudRepository;

public interface SojournRepository extends CrudRepository<Sojourn, Long> {
}
