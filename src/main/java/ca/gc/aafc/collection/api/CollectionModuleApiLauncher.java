package ca.gc.aafc.collection.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Launches the application.
 */
// CHECKSTYLE:OFF HideUtilityClassConstructor (Configuration class can not have
// invisible constructor, ignore the check style error for this case)
@SpringBootApplication
public class CollectionModuleApiLauncher {
  public static void main(String[] args) {
    SpringApplication.run(CollectionModuleApiLauncher.class, args);
  }
}
