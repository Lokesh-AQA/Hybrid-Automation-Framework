package utils;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

public class KeyboardUtils {

	// Centralized Key Mapping
	private static final Map<String, Keys> KEY_MAP = new HashMap<>();

	static {
		
		KEY_MAP.put("TAB", Keys.TAB);
		KEY_MAP.put("ENTER", Keys.ENTER);
		KEY_MAP.put("ESCAPE", Keys.ESCAPE);
		KEY_MAP.put("BACKSPACE", Keys.BACK_SPACE);
		KEY_MAP.put("DELETE", Keys.DELETE);
		KEY_MAP.put("SPACE", Keys.SPACE);
		KEY_MAP.put("UP", Keys.ARROW_UP);
		KEY_MAP.put("DOWN", Keys.ARROW_DOWN);
		KEY_MAP.put("LEFT", Keys.ARROW_LEFT);
		KEY_MAP.put("RIGHT", Keys.ARROW_RIGHT);

	}

	// For Excel Keywords
	public static void pressKey(WebDriver driver, String testData) {

		Keys key = KEY_MAP.get(testData.toUpperCase());

		if (key == null) {

			FrameworkLogger.fail("Unsupported Key : " + testData);

			throw new IllegalArgumentException("Unsupported Key : " + testData);

		}

		pressKey(driver, key);

	}

	// For Java Code
	public static void pressKey(WebDriver driver, Keys key) {

		Actions actions = new Actions(driver);

		actions.sendKeys(key).perform();

	}

}