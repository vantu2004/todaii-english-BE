package com.todaii.english.core.refresh_token;

import com.todaii.english.core.admin.admin.Admin;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "admin_refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminRefreshToken extends BaseRefreshToken {
	@ManyToOne(optional = false)
	@JoinColumn(name = "admin_id")
	private Admin admin;
}
