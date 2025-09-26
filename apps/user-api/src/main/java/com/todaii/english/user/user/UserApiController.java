package com.todaii.english.user.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.user.user.User;
import com.todaii.english.core.user.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserApiController {
	private final UserService userService;
	
	@GetMapping
	public ResponseEntity<?> getAllUsers() {
		List<User> students = this.userService.getAllUsers();

		if (students.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(students);
	}

//	@PostMapping
//	public ResponseEntity<?> createUser(@RequestBody User user) {
//		User createdUser = this.userService.
//
//		return new ResponseEntity<>(addedStudent, HttpStatus.CREATED);
//	}

//	@PutMapping
//	public ResponseEntity<?> updateStudent(@RequestBody Student student) {
//		if (this.studentRepository.existsById(student.getId())) {
//			Student updatedStudent = this.studentRepository.save(student);
//
//			return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
//		} else {
//			return ResponseEntity.notFound().build();
//		}
//	}
//
//	@DeleteMapping("/{id}")
//	@PreAuthorize("hasAuthority('write')")
//	public ResponseEntity<?> deleteStudent(
//			@PathVariable("id") @Positive(message = "Student ID must be greater than 0") Integer id) {
//		if (this.studentRepository.existsById(id)) {
//			this.studentRepository.deleteById(id);
//
//			return ResponseEntity.noContent().build();
//		} else {
//			return ResponseEntity.notFound().build();
//		}
//	}
}
