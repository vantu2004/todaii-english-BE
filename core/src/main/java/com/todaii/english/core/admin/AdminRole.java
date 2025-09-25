package com.todaii.english.core.admin;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin_roles")
@Getter
@Setter
public class AdminRole {

	@Id
	@Column(length = 64, nullable = false, unique = true)
	private String code; // ví dụ: SUPER_ADMIN, CONTENT_MANAGER

	@Column(length = 191, nullable = false, unique = true)
	private String description; // ví dụ: "Super Admin", "Content Manager"
}
