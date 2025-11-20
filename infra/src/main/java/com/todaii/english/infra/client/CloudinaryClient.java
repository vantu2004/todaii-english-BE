package com.todaii.english.infra.client;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.todaii.english.core.port.CloudinaryPort;

@Component
public class CloudinaryClient implements CloudinaryPort {

	private final Cloudinary cloudinary;

	public CloudinaryClient(@Value("${cloudinary.cloud_name}") String cloudName,
			@Value("${cloudinary.api_key}") String apiKey, @Value("${cloudinary.api_secret}") String apiSecret) {

		this.cloudinary = new Cloudinary(
				ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret));
	}

	@Override
	public String uploadImage(String base64Image) {
		try {
			Map uploadResult = cloudinary.uploader().upload(base64Image, ObjectUtils.asMap("folder",
					"todaii/admin_avatars", "resource_type", "image", "use_filename", false, "unique_filename", true));

			return uploadResult.get("secure_url").toString();

		} catch (IOException e) {
			throw new RuntimeException("Failed to upload image to Cloudinary");
		}
	}
}
