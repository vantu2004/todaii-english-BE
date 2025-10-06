package com.todaii.english.core.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "video_lyric_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoLyricLine {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "line_order", nullable = false)
	private Integer lineOrder; // thứ tự dòng

	@Column(name = "start_ms", nullable = false)
	private Integer startMs; // thời gian bắt đầu (ms)

	@Column(name = "end_ms", nullable = false)
	private Integer endMs; // thời gian kết thúc (ms)

	@Lob
	@Column(name = "text_en", columnDefinition = "MEDIUMTEXT")
	private String textEn; // câu lyric tiếng Anh

	@Lob
	@Column(name = "text_vi", columnDefinition = "MEDIUMTEXT")
	private String textVi; // dịch tiếng Việt

	// Liên kết với bảng videos
	@ManyToOne
	@JoinColumn(name = "video_id", nullable = false)
	private Video video;
}
