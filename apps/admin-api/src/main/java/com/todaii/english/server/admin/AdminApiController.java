package com.todaii.english.server.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.Admin;
import com.todaii.english.server.security.CustomAdminDetails;
import com.todaii.english.shared.request.UpdateProfileRequest;
import com.todaii.english.shared.request.server.CreateAdminRequest;
import com.todaii.english.shared.request.server.UpdateAdminRequest;
import com.todaii.english.shared.response.PagedResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminApiController {

	private final AdminService adminService;

	@Deprecated
	public ResponseEntity<?> getAllAdmins() {
		List<Admin> admins = this.adminService.findAll();

		if (CollectionUtils.isEmpty(admins)) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(admins);
	}

	// sortBy dùng đúng tên trong entity chứ ko phải snakecase
	@GetMapping
	public ResponseEntity<?> getAllAdminsPaged(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "asc") String direction, @RequestParam(required = false) String keyword) {
		Page<Admin> admins = this.adminService.findAllPaged(page, size, sortBy, direction, keyword);

		PagedResponse<Admin> response = new PagedResponse<Admin>(admins.getContent(), page, size,
				admins.getTotalElements(), admins.getTotalPages(), admins.isFirst(), admins.isLast(), sortBy,
				direction);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getAdminById(@PathVariable Long id) {
		return ResponseEntity.ok(this.adminService.findById(id));
	}

	@GetMapping("/me")
	public ResponseEntity<?> getProfile(Authentication authentication) {
		CustomAdminDetails principal = (CustomAdminDetails) authentication.getPrincipal();
		Long currentAdminId = principal.getAdmin().getId();

		return ResponseEntity.ok(this.adminService.findById(currentAdminId));
	}

	@PostMapping
	public ResponseEntity<?> createAdmin(@Valid @RequestBody CreateAdminRequest createAdminRequest) {
		return ResponseEntity.status(201).body(this.adminService.create(createAdminRequest));
	}

	@PutMapping("/me")
	public ResponseEntity<?> updateProfile(Authentication authentication,
			@Valid @RequestBody UpdateProfileRequest updateProfileRequest) {
		CustomAdminDetails principal = (CustomAdminDetails) authentication.getPrincipal();
		Long currentAdminId = principal.getAdmin().getId();

		return ResponseEntity.ok(this.adminService.updateProfile(currentAdminId, updateProfileRequest));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateAdmin(@PathVariable Long id,
			@Valid @RequestBody UpdateAdminRequest updateAdminRequest) {
		return ResponseEntity.ok(this.adminService.updateAdmin(id, updateAdminRequest));
	}

	@PatchMapping("/{id}/enabled")
	public ResponseEntity<?> toggleEnabled(@PathVariable Long id) {
		this.adminService.toggleEnabled(id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
		this.adminService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
