package com.todaii.english.infra.client;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.todaii.english.core.port.CloudinaryPort;
import com.todaii.english.core.port.SettingQueryPort;
import com.todaii.english.shared.constants.SettingKey;

@Component
public class CloudinaryClient implements CloudinaryPort {
	private final Cloudinary cloudinary;
	private final SettingQueryPort settingQueryPort;

	public CloudinaryClient(SettingQueryPort settingQueryPort) {
		this.settingQueryPort = settingQueryPort;

		String cloudName = settingQueryPort.getSettingByKey(SettingKey.CLOUDINARY_CLOUD_NAME).getValue();
		String apiKey = settingQueryPort.getSettingByKey(SettingKey.CLOUDINARY_API_KEY).getValue();
		String apiSecret = settingQueryPort.getSettingByKey(SettingKey.CLOUDINARY_API_SECRET).getValue();

		this.cloudinary = new Cloudinary(
				ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret));
	}

	@Override
	public String uploadImage(String base64Image, String folderName) {
		try {
			Map uploadResult = cloudinary.uploader().upload(base64Image, ObjectUtils.asMap("folder",
					"todaii/" + folderName, "resource_type", "image", "use_filename", false, "unique_filename", true));

			return uploadResult.get("secure_url").toString();

		} catch (IOException e) {
			throw new RuntimeException("Failed to upload image to Cloudinary");
		}
	}
}
