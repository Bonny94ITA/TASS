package com.project.service;

import com.project.model.Alternative;
import ilog.concert.IloException;
import net.sf.clipsrules.jni.CLIPSException;

import java.util.List;

public interface ISecretSearch {
    List<Alternative> getAllAlternatives(Object... args) throws CLIPSException, IloException;
}
