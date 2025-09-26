package com.todaii.english.core.admin.setting;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.todaii.english.shared.enums.SettingCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Setting {
	// key trùng từ khóa trong mysql nên phải chỉ định cụ thể
	@Id
	@Column(name = "`key`", nullable = false, length = 128)
	private String key;

	@Column(nullable = false, length = 1024)
	private String value;

	// @Enumerated(EnumType.STRING) giúp lưu Enum dưới dạng chuỗi.
	@Enumerated(EnumType.STRING)
	@Column(name = "setting_category", nullable = false, length = 32)
	private SettingCategory settingCategory;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}
