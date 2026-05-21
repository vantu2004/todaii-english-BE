package com.todaii.english.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.DictionaryWord;

@Repository
public interface DictionaryRepository extends JpaRepository<DictionaryWord, Long> {}
