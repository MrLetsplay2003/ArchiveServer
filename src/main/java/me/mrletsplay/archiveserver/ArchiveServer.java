package me.mrletsplay.archiveserver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.stream.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class ArchiveServer implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("archiveserver");

	private static final Path WORLDS_CONFIG = Paths.get("config/archiveserver/worlds.json");
	public static List<String> worlds = new ArrayList<>();

	@Override
	public void onInitialize() {
		try {
			if(!Files.exists(WORLDS_CONFIG)) {
				Files.createDirectories(WORLDS_CONFIG.getParent());
				Files.writeString(WORLDS_CONFIG, "{\"worlds\": []}", StandardCharsets.UTF_8);
			}

			Gson gson = new Gson();
			JsonObject object = gson.fromJson(Files.readString(WORLDS_CONFIG, StandardCharsets.UTF_8), JsonObject.class);
			worlds = Streams.of(object.getAsJsonArray("worlds").iterator()).map(e -> e.getAsString()).toList();
			LOGGER.info("ArchiveServer config loaded!");
			LOGGER.info("Archived worlds: " + worlds);
		}catch(IOException | JsonParseException e) {
			LOGGER.error("Failed to load config!", e);
			throw new RuntimeException(e);
		}
	}

	public static boolean isArchived(Identifier worldIdentifier) {
		return worlds.contains(worldIdentifier.toString());
	}

}