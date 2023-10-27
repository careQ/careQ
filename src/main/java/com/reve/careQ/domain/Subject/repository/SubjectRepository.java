package com.reve.careQ.domain.Subject.repository;

import com.reve.careQ.domain.Subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findById(Long id);

    Optional<Subject> findByCode(String code);

    Optional<Subject> findByName(String name);

    List<Subject> findByNameLike(String name);

}
