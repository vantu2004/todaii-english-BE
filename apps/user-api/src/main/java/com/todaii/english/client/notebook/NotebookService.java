package com.todaii.english.client.notebook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.NotebookItem;
import com.todaii.english.shared.enums.NotebookType;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.NotebookNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotebookService {
	private final NotebookRepository notebookRepository;

	public List<NotebookNode> getTree() {
		List<NotebookItem> items = notebookRepository.findAllByOrderByNameAsc();

		// Map: id → node
		Map<Long, NotebookNode> map = new HashMap<>();

		for (NotebookItem item : items) {
			NotebookNode node = new NotebookNode();
			node.setId(item.getId());
			node.setName(item.getName());
			node.setFolder(item.getType().equals(NotebookType.FOLDER));

			map.put(item.getId(), node);
		}

		// build tree
		List<NotebookNode> roots = new ArrayList<>();

		for (NotebookItem item : items) {
			NotebookNode node = map.get(item.getId());

			if (item.getParentId() == null) {
				roots.add(node);
			} else {
				NotebookNode parent = map.get(item.getParentId());
				if (parent != null)
					parent.getChildren().add(node);
			}
		}

		return roots;
	}

	public NotebookItem createItem(String name, NotebookType type, Long parentId) {
		NotebookItem item = new NotebookItem();
		item.setName(name);
		item.setType(type);
		item.setParentId(parentId);

		return notebookRepository.save(item);
	}

	public NotebookItem rename(Long id, String newName) {
		NotebookItem item = notebookRepository.findById(id)
				.orElseThrow(() -> new BusinessException(404, "Notebook not found"));
		item.setName(newName);

		return notebookRepository.save(item);
	}

	public void deleteItem(Long id) {
		// delete tất cả con trước
		deleteRecursive(id);
	}

	private void deleteRecursive(Long id) {
		List<NotebookItem> children = notebookRepository.findByParentId(id);
		for (NotebookItem child : children) {
			deleteRecursive(child.getId());
		}
		notebookRepository.deleteById(id);
	}
}
