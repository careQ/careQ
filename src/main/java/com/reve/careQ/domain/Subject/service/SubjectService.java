package com.reve.careQ.domain.Subject.service;

import com.reve.careQ.domain.Subject.entity.Subject;
import com.reve.careQ.domain.Subject.repository.SubjectRepository;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectService {

    private final SubjectRepository subjectRepository;

    @Transactional
    public RsData<List<Subject>> find(String name) {

        List<Subject> subjectList = subjectRepository.findByNameLike("%"+name+"%");

        return RsData.of("S-1", "과목테이블에 삽입되었습니다.", subjectList);
    }

    @Transactional
    public RsData<Subject> insert(String code, String name) {

        Subject subject = Subject
                .builder()
                .code(code)
                .name(name)
                .build();

        subjectRepository.save(subject);

        return RsData.of("S-1", "과목테이블에 삽입되었습니다.", subject);

    }

    @Transactional
    public RsData<Optional<Subject>> findById(Long id) {

        Optional<Subject> subject = subjectRepository.findById(id);

        return RsData.of("S-1", "과목이 존재합니다.", subject);
    }
}
