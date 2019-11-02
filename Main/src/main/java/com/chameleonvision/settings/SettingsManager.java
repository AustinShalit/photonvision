package com.chameleonvision.settings;

import com.chameleonvision.util.FileHelper;
import com.chameleonvision.vision.camera.CameraManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsManager {
	public static final Path SettingsPath = Paths.get(System.getProperty("user.dir"), "settings");
	public static GeneralSettings generalSettings;

	private SettingsManager() {}

	public static void initialize() {
		initGeneralSettings();
		var allCameras = CameraManager.getAllCamerasByName();
		if (!allCameras.containsKey(generalSettings.currentCamera) && allCameras.size() > 0) {
			var cam = allCameras.entrySet().stream().findFirst().get().getValue();
			generalSettings.currentCamera = cam.name;
			generalSettings.currentPipeline = cam.getCurrentPipelineIndex();
		}
	}

	private static void initGeneralSettings() {
		FileHelper.CheckPath(SettingsPath);
		try {
			generalSettings = new Gson().fromJson(new FileReader(Paths.get(SettingsPath.toString(), "settings.json").toString()), com.chameleonvision.settings.GeneralSettings.class);
		} catch (FileNotFoundException e) {
			generalSettings = new GeneralSettings();
		}
	}

	public static void updateCameraSetting(String cameraName, int pipelineNumber) {
		generalSettings.currentCamera = cameraName;
		generalSettings.currentPipeline = pipelineNumber;
	}

	public static void updatePipelineSetting(int pipelineNumber) {
		generalSettings.currentPipeline = pipelineNumber;
	}

	public static void saveSettings() {
		CameraManager.saveCameras();
		saveGeneralSettings();
	}

	private static void saveGeneralSettings() {
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			FileWriter writer = new FileWriter(Paths.get(SettingsPath.toString(), "settings.json").toString());
			gson.toJson(generalSettings, writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
