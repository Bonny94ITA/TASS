package com.project.repository;

import com.project.model.Guest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends CrudRepository<Guest, Long> {
	//List<Guest> findByAge(int age);
}

