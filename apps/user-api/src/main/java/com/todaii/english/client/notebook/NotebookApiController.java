package com.todaii.english.client.notebook;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
import com.todaii.english.shared.request.client.NotebookRequest;
import com.todaii.english.shared.response.NotebookNode;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notebook")
public class NotebookApiController {
  private final NotebookService notebookService;

  @GetMapping
  public ResponseEntity<List<NotebookNode>> getTree(Authentication authentication) {
    return ResponseEntity.ok(notebookService.getTree(UserUtils.getCurrentUserId(authentication)));
  }

  @PostMapping
  public ResponseEntity<Void> create(
      Authentication authentication, @Valid @RequestBody NotebookRequest notebookRequest) {
    notebookService.createItem(UserUtils.getCurrentUserId(authentication), notebookRequest);

    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> rename(
      Authentication authentication, @PathVariable Long id, @RequestParam String name) {
    notebookService.rename(UserUtils.getCurrentUserId(authentication), id, name);

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
    notebookService.deleteItem(UserUtils.getCurrentUserId(authentication), id);
    return ResponseEntity.ok().build();
  }
}
