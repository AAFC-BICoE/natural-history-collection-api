package ca.gc.aafc.collection.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class CollectionModuleStartupTest extends CollectionModuleBaseIT {

  @Test
  public void dummyTest() {
    //empty test
  }

}
