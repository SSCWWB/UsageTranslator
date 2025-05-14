package com.yushiz.project.UsageTranslator.io;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.Map;

public class JsonTypeMapper {
	public static Map<String, String> loadTypeMap(Path jsonPath) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(jsonPath.toFile(), Map.class);
	}
}