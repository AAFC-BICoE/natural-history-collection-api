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
@ContextConfiguration(initializers = { PostgresTestContainerInitializer.class })
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
public class CollectionModuleStartupTest {

  @Test
  public void dummyTest() {
    //empty test
  }

}
