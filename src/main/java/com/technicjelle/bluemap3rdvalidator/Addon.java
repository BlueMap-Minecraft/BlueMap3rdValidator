package com.technicjelle.bluemap3rdvalidator;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
@ConfigSerializable
public class Addon {
	private String name;
	private String description;
	private String author;
	private String apiVersion;
	private ArrayList<String> platforms;
	private Map<String, String> links;

	public boolean validate(List<String> platformsDir, List<String> validLinkTypes) {
		boolean valid = true;
		StringBuilder log = new StringBuilder("Checking validity of addon: " + name);

		if (isNullOrEmpty(name)) {
			log.append("\n\t⛔ Name is required");
			valid = false;
		}

		if (isNullOrEmpty(description)) {
			log.append("\n\t⛔ Description is required");
			valid = false;
		}

		if (isNullOrEmpty(author)) {
			log.append("\n\t⛔ Author is required");
			valid = false;
		}

		if (isNullOrEmpty(apiVersion)) {
			log.append("\n\t⚠ API version is not specified. This may be intentional.");
		}

		if (isNullOrEmpty(platforms)) {
			log.append("\n\t⛔ Platforms is required");
			valid = false;
		} else {
			for (String platform : platforms) {
				if (!platformsDir.contains(platform)) {
					log.append("\n\t⛔ Invalid platform: ").append(platform);
					valid = false;
				}
			}
		}

		if (isNullOrEmpty(links)) {
			log.append("\n\t⛔ Links is required");
			valid = false;
		} else {
			for (Map.Entry<String, String> entry : links.entrySet()) {
				String linkType = entry.getKey();
				String link = entry.getValue();
				if (!validLinkTypes.contains(linkType)) {
					log.append("\n\t⛔ Invalid link type: ").append(linkType).append(" (").append(link).append(")");
					valid = false;
				}
			}
		}

		if (valid) {
			log.append("\n\t✅ Valid!");
			System.out.println(log);
		} else {
			System.err.println(log);
		}

		return valid;
	}

	private static boolean isNullOrEmpty(String s) {
		return s == null || s.isEmpty();
	}

	private static boolean isNullOrEmpty(AbstractCollection<?> c) {
		return c == null || c.isEmpty();
	}

	private static boolean isNullOrEmpty(Map<?, ?> m) {
		return m == null || m.isEmpty();
	}
}
