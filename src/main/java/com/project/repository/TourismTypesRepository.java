package com.project.repository;

import com.project.model.TourismTypes;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourismTypesRepository extends CrudRepository<TourismTypes,Long> {
}
