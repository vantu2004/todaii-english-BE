package com.todaii.english.server.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.DictionarySense;

@Repository
public interface DictionarySenseRepository extends JpaRepository<DictionarySense, Long> {

}
