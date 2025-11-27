package com.todaii.english.client.notebook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.NotebookItem;
import com.todaii.english.core.entity.User;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.client.NotebookRequest;
import com.todaii.english.shared.response.NotebookNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotebookService {

	private final NotebookRepository notebookRepository;

	public List<NotebookNode> getTree(Long userId) {

		List<NotebookItem> items = notebookRepository.findByUserIdOrderByNameAsc(userId);

		Map<Long, NotebookNode> map = new HashMap<>();
		List<NotebookNode> roots = new ArrayList<>();

		for (NotebookItem item : items) {
			NotebookNode node = new NotebookNode();
			node.setId(item.getId());
			node.setName(item.getName());
			node.setType(item.getType());
			map.put(item.getId(), node);
		}

		for (NotebookItem item : items) {
			NotebookNode node = map.get(item.getId());
			if (item.getParentId() == null) {
				roots.add(node);
			} else {
				map.get(item.getParentId()).getChildren().add(node);
			}
		}

		return roots;
	}

	public NotebookItem createItem(Long userId, NotebookRequest req) {
		NotebookItem item = new NotebookItem();
		item.setName(req.getName());
		item.setType(req.getType());
		item.setParentId(req.getParentId());
		item.setUser(User.builder().id(userId).build());

		return notebookRepository.save(item);
	}

	public NotebookItem rename(Long userId, Long id, String newName) {
		NotebookItem item = getOwned(userId, id);
		item.setName(newName);
		return notebookRepository.save(item);
	}

	public void deleteItem(Long userId, Long id) {
		deleteRecursive(userId, id);
	}

	private void deleteRecursive(Long userId, Long id) {
		List<NotebookItem> children = notebookRepository.findByUserIdAndParentId(userId, id);
		for (NotebookItem child : children) {
			deleteRecursive(userId, child.getId());
		}
		notebookRepository.deleteById(id);
	}

	private NotebookItem getOwned(Long userId, Long id) {
		NotebookItem item = notebookRepository.findById(id)
				.orElseThrow(() -> new BusinessException(404, "Notebook not found"));

		if (!item.getUser().getId().equals(userId)) {
			throw new BusinessException(403, "Forbidden");
		}

		return item;
	}
}
