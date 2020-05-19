package com.project.service;

import com.project.controller.exception.DeleteException;
import com.project.model.Sojourn;
import com.project.repository.SojournRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SojournService implements ISojournService {

    @Autowired
    private SojournRepository sojournRepository;

    @Override
    public Sojourn addSojourn(Sojourn sojourn) {
        return sojournRepository.save(new Sojourn(sojourn.getArrival(),sojourn.getDeparture(),sojourn.getRoom()));
    }

    @Override
    public void deleteById(long id) throws DeleteException {
        if (sojournRepository.findById(id).isPresent()) {
            sojournRepository.deleteById(id);
        } else throw new DeleteException("Sojourn with id. " +
                id + " not found.");
    }

    @Override
    public void deleteAll() {
        sojournRepository.deleteAll();
    }
}
