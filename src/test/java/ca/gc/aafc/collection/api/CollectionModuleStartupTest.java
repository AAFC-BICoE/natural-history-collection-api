package ca.gc.aafc.collection.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.collection.api.config.ApiInfoConfiguration;

@SpringBootTest(classes = CollectionModuleApiLauncher.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CollectionModuleStartupTest extends CollectionModuleBaseIT {

  @Inject
  private ApiInfoConfiguration apiInfoConfiguration;

  @Test
  public void pendingImport_marksApiInfoAsNeedingAttention() {
    var apiInfo = apiInfoConfiguration.buildApiInfoDto();

    // this is due to pending data import from transaction-api
    assertTrue(apiInfo.isAttentionRequired());
    assertTrue(apiInfo.getModuleInfo().containsKey(StartupChecks.PENDING_IMPORT_KEY));
    assertNotNull(apiInfo.getModuleInfo().get(StartupChecks.PENDING_IMPORT_KEY));
  }

}
