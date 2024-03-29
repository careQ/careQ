package com.reve.careQ.domain.Subject.service;

import com.reve.careQ.domain.Subject.entity.Subject;
import com.reve.careQ.domain.Subject.repository.SubjectRepository;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }

    public Optional<Subject> findByCode(String code) {
        return subjectRepository.findByCode(code);
    }

    public Optional<Subject> findByName(String name) {
        return subjectRepository.findByName(name);
    }

    @Transactional
    public RsData<Subject> insert(String code, String name) {

        Subject subject = createSubject(code, name);

        return RsData.of("S-1", "과목테이블에 삽입되었습니다.", subject);
    }

    private Subject createSubject(String code, String name) {
        Subject subject = Subject
                .builder()
                .code(code)
                .name(name)
                .build();

        return subjectRepository.save(subject);
    }
}
