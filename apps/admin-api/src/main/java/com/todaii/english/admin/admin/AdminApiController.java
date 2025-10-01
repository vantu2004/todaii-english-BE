package com.todaii.english.admin.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.admin.admin.Admin;
import com.todaii.english.core.admin.admin.AdminService;
import com.todaii.english.infra.security.admin.CustomAdminDetails;
import com.todaii.english.shared.request.UpdateProfileRequest;
import com.todaii.english.shared.request.admin.CreateAdminRequest;
import com.todaii.english.shared.request.admin.UpdateAdminRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminApiController {

	private final AdminService adminService;

	@GetMapping
	public ResponseEntity<?> getAllAdmins() {
		List<Admin> admins = this.adminService.findAll();

		if (CollectionUtils.isEmpty(admins)) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(admins);
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
		return ResponseEntity.ok(this.adminService.create(createAdminRequest));
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

	@PutMapping("/toggle-enabled/{id}")
	public ResponseEntity<?> toggleEnable(@PathVariable Long id) {
		this.adminService.toggleEnabled(id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
		this.adminService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
