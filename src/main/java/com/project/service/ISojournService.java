package com.project.service;

import com.project.model.Room;
import com.project.model.Sojourn;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ISojournService {
    Sojourn addSojourn(Sojourn sojourn);
}
