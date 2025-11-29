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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "API for managing admin accounts")
public class AdminApiController {

	private final AdminService adminService;

	@Deprecated
	@Operation(summary = "Get all admins (deprecated)", description = "Returns all admins. This method is deprecated.")
	public ResponseEntity<List<Admin>> getAllAdmins() {
		List<Admin> admins = this.adminService.findAll();
		if (CollectionUtils.isEmpty(admins)) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(admins);
	}

	@GetMapping
	@Operation(summary = "Get paged list of admins", description = "Returns paginated list of admins with optional keyword filtering.")
	public ResponseEntity<PagedResponse<Admin>> getAllAdminsPaged(
			Authentication authentication,
			@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "10") @Min(1) int size,
			@RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = false) String keyword) {

		CustomAdminDetails principal = (CustomAdminDetails) authentication.getPrincipal();
		Long currentAdminId = principal.getAdmin().getId();

		Page<Admin> admins = this.adminService.findAllPaged(currentAdminId, page, size, sortBy, direction, keyword);

		PagedResponse<Admin> response = new PagedResponse<>(
			admins.getContent(), page, size, admins.getTotalElements(),
			admins.getTotalPages(), admins.isFirst(), admins.isLast(),
			sortBy, direction
		);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get admin by ID", description = "Retrieve full details of an admin by ID.")
	@ApiResponse(responseCode = "200", description = "Admin found")
	@ApiResponse(responseCode = "404", description = "Admin not found")
	public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
		return ResponseEntity.ok(this.adminService.findById(id));
	}

	@GetMapping("/me")
	@Operation(summary = "Get current admin profile", description = "Retrieve the profile of the logged-in admin.")
	public ResponseEntity<Admin> getProfile(Authentication authentication) {
		CustomAdminDetails principal = (CustomAdminDetails) authentication.getPrincipal();
		Long currentAdminId = principal.getAdmin().getId();
		return ResponseEntity.ok(this.adminService.findById(currentAdminId));
	}

	@PostMapping
	@Operation(summary = "Create new admin", description = "Create a new admin account.")
	public ResponseEntity<Admin> createAdmin(@Valid @RequestBody AdminRequest adminRequest) {
		return ResponseEntity.status(201).body(this.adminService.create(adminRequest));
	}

	@PutMapping("/me")
	@Operation(summary = "Update current admin profile", description = "Update profile information of the logged-in admin.")
	public ResponseEntity<Admin> updateProfile(Authentication authentication,
			@Valid @RequestBody UpdateProfileRequest updateProfileRequest) {
		CustomAdminDetails principal = (CustomAdminDetails) authentication.getPrincipal();
		Long currentAdminId = principal.getAdmin().getId();
		return ResponseEntity.ok(this.adminService.updateProfile(currentAdminId, updateProfileRequest));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update admin by ID", description = "Update an existing admin's information by ID.")
	public ResponseEntity<Admin> updateAdmin(@PathVariable Long id, @Valid @RequestBody AdminRequest adminRequest) {
		return ResponseEntity.ok(this.adminService.updateAdmin(id, adminRequest));
	}

	@PatchMapping("/{id}/enabled")
	@Operation(summary = "Toggle admin enabled status", description = "Enable or disable an admin account.")
	public ResponseEntity<Void> toggleEnabled(@PathVariable Long id) {
		this.adminService.toggleEnabled(id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete admin", description = "Remove an admin account by ID.")
	@ApiResponse(responseCode = "204", description = "Admin deleted successfully")
	public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
		this.adminService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
