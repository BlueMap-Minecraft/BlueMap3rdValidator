package com.technicjelle.bluemap3rdvalidator;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.util.NamingSchemes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
		System.out.println("Found these valid platforms: " + validPlatforms);

		final Path linksDir = addonBrowserDir.resolve("links");
		List<String> validLinkTypes = getFilesInDirectory(linksDir);
		System.out.println("Found these valid link types: " + validLinkTypes);

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
		boolean valid = true;
		System.out.println("Checking validity of addons in " + confFile);

		String contents = Files.readString(confFile);

		// make sure all apostrophes are escaped:
		final Pattern apostrophePattern = Pattern.compile("(\\\\)?'");
		final Matcher matcher = apostrophePattern.matcher(contents);
		while (matcher.find()) {
			if (matcher.group(1) == null) {
				System.err.println("Unescaped apostrophe found at character " + matcher.start());
				System.exit(1);
			}
		}

		// remove all apostrophe escapes before we feed it into Configurate, because it rightfully rejects them,
		//  but the hocon-js library requires them for some reason:
		contents = contents.replaceAll("\\\\'", "'");

		// AAAAAAAAAAAAAAAAAAAAAAAAAA :notlikethis:
		// https://github.com/lightbend/config/issues/460
		final String prefix = "addons: ";
		contents = prefix + contents;

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

			if (!addon.validate(platformsDir, validLinkTypes)) {
				valid = false;
			}
		}

		if (valid) {
			System.out.println("All addons are valid!");
		} else {
			System.err.println("Some addons are invalid!");
			System.exit(1);
		}
	}
}
