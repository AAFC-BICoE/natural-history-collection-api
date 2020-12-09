package ca.gc.aafc.collection.api;

import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;

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
