package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * No Transactional annotation by design.
 * Repository level tests should not use it.
 */
@SpringBootTest(classes = CollectionModuleApiLauncher.class, properties = "keycloak.enabled = true")
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@ContextConfiguration(initializers = { PostgresTestContainerInitializer.class })
public class BaseRepositoryIT {
}
