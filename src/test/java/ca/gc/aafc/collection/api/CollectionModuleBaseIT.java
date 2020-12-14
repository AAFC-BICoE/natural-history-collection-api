package ca.gc.aafc.collection.api;

import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;
import javax.transaction.Transactional;

@SpringBootTest(classes = CollectionModuleApiLauncher.class)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = { PostgresTestContainerInitializer.class })
public class CollectionModuleBaseIT {
  @Inject
  protected DatabaseSupportService service;    
}

