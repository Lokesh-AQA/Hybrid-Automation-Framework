package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtils {

    private static final Properties properties = new Properties();

    static {

        try (InputStream input = PropertyUtils.class
                .getClassLoader()
                .getResourceAsStream("testdata/ObjectRepository.properties")) {

            if (input == null) {
                throw new RuntimeException("ObjectRepository.properties not found.");
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String getProperty(String key) {

        return properties.getProperty(key);

    }
    
    public static String getDisplayName(String objectName) {

        String displayName = properties.getProperty(objectName + ".display");

        if (displayName == null || displayName.isBlank()) {
            return objectName;
        }

        return displayName;
    }

}