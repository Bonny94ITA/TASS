package com.project.repository;

import com.project.model.Sojourn;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SojournRepository  extends CrudRepository<Sojourn, Long> {
}
