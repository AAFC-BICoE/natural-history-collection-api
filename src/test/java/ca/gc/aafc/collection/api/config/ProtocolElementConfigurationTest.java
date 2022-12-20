package ca.gc.aafc.collection.api.config;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = CollectionModuleApiLauncher.class)
public class ProtocolElementConfigurationTest extends CollectionModuleBaseIT {

  @Inject
  private ProtocolElementConfiguration protocolElementConfiguration;

  @Test
  void getProtocolElements() {
    List<ProtocolElementConfiguration.ProtocolElement> protocolElements = protocolElementConfiguration.getProtocolElements();
    assertNotNull(protocolElements);
    assertTrue(protocolElements.size() > 2);
  }
}