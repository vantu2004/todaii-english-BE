package com.todaii.english.server.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.Admin;
import com.todaii.english.server.AdminUtils;
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
		Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);

		return ResponseEntity.ok(this.adminService.findById(currentAdminId));
	}

	@PostMapping
	public ResponseEntity<Admin> createAdmin(Authentication authentication,
			@Valid @RequestBody AdminRequest adminRequest) {
		Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);

		return ResponseEntity.status(201).body(this.adminService.create(currentAdminId, adminRequest));
	}

	@PutMapping("/me")
	@Operation(summary = "Update current admin profile", description = "Update profile information of the logged-in admin.")
	public ResponseEntity<Admin> updateProfile(Authentication authentication,
			@Valid @RequestBody UpdateProfileRequest updateProfileRequest) {
		Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);

		return ResponseEntity.ok(this.adminService.updateProfile(currentAdminId, updateProfileRequest));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Admin> updateAdmin(Authentication authentication, @PathVariable Long id,
			@Valid @RequestBody AdminRequest adminRequest) {
		Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);

		return ResponseEntity.ok(this.adminService.updateAdmin(currentAdminId, id, adminRequest));
	}

	@PatchMapping("/{id}/enabled")
	public ResponseEntity<Void> toggleEnabled(Authentication authentication, @PathVariable Long id) {
		Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);
		this.adminService.toggleEnabled(currentAdminId, id);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAdmin(Authentication authentication, @PathVariable Long id) {
		Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);
		this.adminService.delete(currentAdminId, id);

		return ResponseEntity.noContent().build();
	}
}
