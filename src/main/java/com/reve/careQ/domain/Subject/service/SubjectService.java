package com.reve.careQ.domain.Subject.service;

import com.reve.careQ.domain.Subject.entity.Subject;
import com.reve.careQ.global.rsData.RsData;

import java.util.List;
import java.util.Optional;

public interface SubjectService {
    Optional<Subject> findById(Long id);
    Optional<Subject> findByCode(String code);
    Optional<Subject> findByName(String name);
    RsData<List<Subject>> find(String name);
    RsData<Subject> insert(String code, String name);
}
