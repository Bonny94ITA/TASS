package com.project.repository;

import com.project.model.Room;
import com.project.model.Sojourn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SojournRepository  extends JpaRepository<Sojourn, Long> {
}
