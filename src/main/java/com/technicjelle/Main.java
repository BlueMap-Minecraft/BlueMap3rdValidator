package com.technicjelle;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.util.NamingSchemes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	public static void main(String[] args) throws IOException {
		if (args.length < 1 || args[0] == null) {
			System.err.println("Please pass the path to the BlueMap Wiki (root) as an argument");
			System.exit(1);
			return;
		}

		final Path wikiRoot = Path.of(args[0]);
		final Path addonBrowserDir = wikiRoot.resolve("assets/addon_browser");

		final Path platformsDir = addonBrowserDir.resolve("platforms");
		List<String> validPlatforms = getFilesInDirectory(platformsDir);

		final Path linksDir = addonBrowserDir.resolve("links");
		List<String> validLinkTypes = getFilesInDirectory(linksDir);

		checkAddonsFileValidity(addonBrowserDir, validPlatforms, validLinkTypes);
	}

	private static List<String> getFilesInDirectory(Path dir) {
		List<String> collection;
		try (Stream<Path> paths = Files.list(dir)) {
			collection = paths
					.filter(file -> !Files.isDirectory(file))
					.map(Path::getFileName)
					.map(Path::toString)
					.map(fileName -> fileName.substring(0, fileName.lastIndexOf('.')))
					.collect(Collectors.toList());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return collection;
	}

	private static void checkAddonsFileValidity(Path addonBrowserDir, List<String> platformsDir, List<String> validLinkTypes) throws IOException {
		final Path confFile = addonBrowserDir.resolve("addons.conf");
		System.out.println("Checking validity of addons in " + confFile);

		// AAAAAAAAAAAAAAAAAAAAAAAAAA :notlikethis:
		// https://github.com/lightbend/config/issues/460
		String contents = "addons: " + Files.readString(confFile);

		final ObjectMapper.Factory customFactory = ObjectMapper.factoryBuilder()
				.defaultNamingScheme(NamingSchemes.PASSTHROUGH)
				.build();

		final ConfigurationNode root = HoconConfigurationLoader.builder()
				.defaultOptions(opts -> opts.serializers(build -> build.registerAnnotatedObjects(customFactory)))
				.buildAndLoadString(contents);

		List<? extends ConfigurationNode> childrenNodes = root.node("addons").childrenList();
		for (ConfigurationNode child : childrenNodes) {
			Addon addon = child.get(Addon.class);
			if (addon == null) {
				throw new RuntimeException("Failed to parse addon");
			}

			addon.checkValid(platformsDir, validLinkTypes);
		}
	}
}
