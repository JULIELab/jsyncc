package de.julielab.jsyncc.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang3.exception.ContextedException;
import org.yaml.snakeyaml.Yaml;

public class YamlReader {

	private Map<String, String> bookPaths;
	private Map<String, String> sources;
	private Map<String, String> sources_short;

	public YamlReader() throws ContextedException {
		Map<String, Map<String, String>> yamlMap = readYaml();
		bookPaths = yamlMap.get("bookPaths");
		sources = yamlMap.get("sources");
		sources_short = yamlMap.get("sources-short");
	}

	private Map<String, Map<String, String>> readYaml() throws ContextedException {
		Yaml yaml = new Yaml();
		File yamlFile = new File("yamlFile.yaml");
		try {
			InputStream inputStream = new FileInputStream(yamlFile);
			Map<String, Map<String, String>> yamlMap = yaml.load(inputStream); // 3
																				// Maps,
																				// Keys
																				// sind:
																				// bookPaths,
																				// sources,
																				// sourcesShort
			return yamlMap;
		} catch (FileNotFoundException fnf) {
			System.err.println("No Yaml-file found");
			throw new ContextedException(fnf);
		}
	}

	public String getPathById(int id) {
		String key = "BOOK" + id;
		return bookPaths.get(key);
	}

	public String getSourceById(int id) {
		String key = "SOURCE" + id;
		return sources.get(key);
	}

	public String getSourceShortById(int id) {
		String key = "SOURCE_SHORT" + id;
		return sources_short.get(key);
	}
}
