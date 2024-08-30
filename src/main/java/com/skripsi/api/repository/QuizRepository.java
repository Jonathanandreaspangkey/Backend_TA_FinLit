package com.skripsi.api.repository;
import com.skripsi.api.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findBySubModuleIdOrderByOrderNumberAsc(Long subModuleId);
}
