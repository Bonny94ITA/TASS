package com.project.repository;

import com.project.model.SojournItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SojournItemRepository extends CrudRepository<SojournItem, Long> {

}
