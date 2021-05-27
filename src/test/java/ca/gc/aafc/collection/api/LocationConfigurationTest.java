package ca.gc.aafc.collection.api;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class LocationConfigurationTest extends CollectionModuleBaseIT {
  
  @Inject
  private LocationConfiguration locationConfiguration;

  @Test
  void getCoordinateSystem() {
    MatcherAssert.assertThat(
      locationConfiguration.getCoordinateSystem(),
      Matchers.contains(
        "decimal degrees",
        "degrees decimal minutes",
        "degrees minutes seconds",
        "UTM"));
  }

  @Test
  void getSrs() {
    MatcherAssert.assertThat(
      locationConfiguration.getSrs(),
      Matchers.contains("WGS84 (EPSG:4326)", "NAD27 (EPSG:4276)"));
  }
}