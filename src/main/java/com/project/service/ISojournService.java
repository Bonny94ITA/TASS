package com.project.service;

import com.project.controller.exception.DeleteException;
import com.project.model.Sojourn;

public interface ISojournService {
    Sojourn addSojourn(Sojourn sojourn);

    void deleteById(long id) throws DeleteException;

    void deleteAll();
}
