package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtils {

	private static final Properties properties = new Properties();

	// ==========================================================
	// Load config.properties
	// ==========================================================

	static {

		try (InputStream input = ConfigUtils.class.getClassLoader().getResourceAsStream("config.properties")) {

			if (input == null) {

				throw new RuntimeException("Configuration Error : config.properties file not found.");

			}

			properties.load(input);

		} catch (IOException e) {

			throw new RuntimeException("Unable to load config.properties", e);

		}
	}

	// ==========================================================
	// Get Optional String Property
	// ==========================================================

	public static String getProperty(String key) {

		return properties.getProperty(key);

	}

	// ==========================================================
	// Get Required String Property
	// ==========================================================

	public static String getRequiredProperty(String key) {

		String value = properties.getProperty(key);

		if (value == null || value.trim().isEmpty()) {

			throw new RuntimeException(
					"Configuration Error : '" + key + "' is Empty.");

		}

		return value.trim();

	}

	// ==========================================================
	// Get Boolean Property
	// ==========================================================

	public static boolean getBooleanProperty(String key) {

		String value = getRequiredProperty(key);

		if (!value.equalsIgnoreCase("true")
				&& !value.equalsIgnoreCase("false")) {

			throw new RuntimeException(
					"Configuration Error : '" + key
							+ "' must be either true or false. Current Value : " + value);

		}

		return Boolean.parseBoolean(value);

	}

	// ==========================================================
	// Get Integer Property
	// ==========================================================

	public static int getIntProperty(String key) {

		String value = getRequiredProperty(key);

		try {

			return Integer.parseInt(value);

		} catch (NumberFormatException e) {

			throw new RuntimeException(
					"Configuration Error : '" + key
							+ "' must be a valid Integer. Current Value : " + value);

		}

	}

}