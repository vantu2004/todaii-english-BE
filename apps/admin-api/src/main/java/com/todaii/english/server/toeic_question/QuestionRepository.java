package com.todaii.english.server.toeic_question;

import com.todaii.english.core.entity.ToeicQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<ToeicQuestion, Long>, JpaSpecificationExecutor<ToeicQuestion> {


}
