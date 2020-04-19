package com.project.service;

import com.project.model.Room;
import com.project.model.Sojourn;
import com.project.repository.SojournRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SojournService implements ISojournService {

    @Autowired
    private SojournRepository sojournRepository;

    @Override
    public Sojourn addSojourn(Sojourn sojourn) {
        return sojournRepository.save(new Sojourn(sojourn.getArrival(),sojourn.getDeparture(),sojourn.getRoom()));
    }
}
