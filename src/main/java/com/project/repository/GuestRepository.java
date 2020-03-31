package com.project.repository;

import com.project.model.Guest;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GuestRepository extends CrudRepository<Guest, Long> {
	//List<Guest> findByAge(int age);
}

