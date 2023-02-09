package ca.gc.aafc.collection.api.config;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = CollectionModuleApiLauncher.class)
public class ProtocolElementConfigurationTest extends CollectionModuleBaseIT {

  @Inject
  private TypedVocabularyConfiguration protocolElementConfiguration;

  @Test
  void getProtocolElements() {
    List<? extends TypedVocabularyElement> protocolElements = protocolElementConfiguration.getProtocolDataElement();
    assertNotNull(protocolElements);
    assertTrue(protocolElements.size() >= 2);
  }
}