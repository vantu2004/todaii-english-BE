package com.todaii.english.client.notebook;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.NotebookItem;

@Repository
public interface NotebookRepository extends JpaRepository<NotebookItem, Long> {
	public List<NotebookItem> findByParentId(Long parentId);

	public List<NotebookItem> findAllByOrderByNameAsc();
}
