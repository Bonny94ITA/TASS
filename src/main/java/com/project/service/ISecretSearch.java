package com.project.service;

import com.project.model.Alternative;
import ilog.concert.IloException;
import net.sf.clipsrules.jni.CLIPSException;

import java.util.List;

public interface ISecretSearch {

    static ISecretSearch getInstance(){
        return SecretSearch.getInstance();
    }
    List<Alternative> getAllAlternatives(/*PARAMETERS*/) throws CLIPSException, IloException;
}
