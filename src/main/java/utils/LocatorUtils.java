package utils;

import org.openqa.selenium.By;

public class LocatorUtils {
	
	public static By getLocator(String objectName) {
		
	    //FrameworkLogger.info("Fetching Locator : " + objectName);
		
		String locator = PropertyUtils.getProperty(objectName);
		
		if (locator == null) {
		    throw new RuntimeException(
		        "Locator not found in ObjectRepository.properties : " + objectName);
		}
		
		String[] parts = locator.split(":");
		String locatorType = parts[0];
		String locatorValue = parts[1];
		
		if (locatorType.equalsIgnoreCase("id")) {
		    return By.id(locatorValue);
		}

	    return null;

	}

}
