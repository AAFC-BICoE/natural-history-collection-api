package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.service.MaterialSampleService;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Same as {@link OrganismCRUDIT} but for functionalities that should be tested AFTER transaction commit.
 */
@SpringBootTest(classes = CollectionModuleApiLauncher.class)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@ContextConfiguration(initializers = { PostgresTestContainerInitializer.class })
public class OrganismAfterCommitCRUDIT {

  @Inject
  private TransactionalMethodProvider transactionalMethodProvider;

  @Inject
  protected MaterialSampleService materialSampleService;

  @Test
  void targetOrganism_MixedTargetOrganism_Exception() {
    assertThrows(JpaSystemException.class, ()-> transactionalMethodProvider.runTransactionFor_TargetOrganism_MixedTargetOrganism_Exception());
  }

}
