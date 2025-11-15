package com.todaii.english.server.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.Admin;
import com.todaii.english.server.security.CustomAdminDetails;
import com.todaii.english.shared.request.UpdateProfileRequest;
import com.todaii.english.shared.request.server.AdminRequest;
import com.todaii.english.shared.response.PagedResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/admin")
public class AdminApiController {

	private final AdminService adminService;

	@Deprecated
	public ResponseEntity<List<Admin>> getAllAdmins() {
		List<Admin> admins = this.adminService.findAll();

		if (CollectionUtils.isEmpty(admins)) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(admins);
	}

	// sortBy dùng đúng tên trong entity chứ ko phải snakecase
	@GetMapping
	public ResponseEntity<PagedResponse<Admin>> getAllAdminsPaged(Authentication authentication,
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be greater than or equal to 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be greater than or equal to 1") int size,
			@RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = false) String keyword) {

		CustomAdminDetails principal = (CustomAdminDetails) authentication.getPrincipal();
		Long currentAdminId = principal.getAdmin().getId();

		Page<Admin> admins = this.adminService.findAllPaged(currentAdminId, page, size, sortBy, direction, keyword);

		PagedResponse<Admin> response = new PagedResponse<>(admins.getContent(), page, size, admins.getTotalElements(),
				admins.getTotalPages(), admins.isFirst(), admins.isLast(), sortBy, direction);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
		return ResponseEntity.ok(this.adminService.findById(id));
	}

	@GetMapping("/me")
	public ResponseEntity<Admin> getProfile(Authentication authentication) {
		CustomAdminDetails principal = (CustomAdminDetails) authentication.getPrincipal();
		Long currentAdminId = principal.getAdmin().getId();

		return ResponseEntity.ok(this.adminService.findById(currentAdminId));
	}

	@PostMapping
	public ResponseEntity<Admin> createAdmin(@Valid @RequestBody AdminRequest adminRequest) {
		return ResponseEntity.status(201).body(this.adminService.create(adminRequest));
	}

	@PutMapping("/me")
	public ResponseEntity<Admin> updateProfile(Authentication authentication,
			@Valid @RequestBody UpdateProfileRequest updateProfileRequest) {
		CustomAdminDetails principal = (CustomAdminDetails) authentication.getPrincipal();
		Long currentAdminId = principal.getAdmin().getId();

		return ResponseEntity.ok(this.adminService.updateProfile(currentAdminId, updateProfileRequest));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Admin> updateAdmin(@PathVariable Long id, @Valid @RequestBody AdminRequest adminRequest) {
		return ResponseEntity.ok(this.adminService.updateAdmin(id, adminRequest));
	}

	@PatchMapping("/{id}/enabled")
	public ResponseEntity<Void> toggleEnabled(@PathVariable Long id) {
		this.adminService.toggleEnabled(id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
		this.adminService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
