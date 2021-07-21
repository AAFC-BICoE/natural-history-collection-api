package ca.gc.aafc.collection.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class VocabularyConfigurationTest extends CollectionModuleBaseIT {

  @Inject
  private VocabularyConfiguration vocabularyConfiguration;

  @Test
  void getDegreeOfEstablishment() {
    assertEquals("degreeOfEstablishment", vocabularyConfiguration.getGroups().get(0).getId());
    assertEquals("native", vocabularyConfiguration.getGroups().get(0).getVocabularyElements().get(0).getName());
    assertEquals("https://dwc.tdwg.org/doe/#dwcdoe_d001", vocabularyConfiguration.getGroups().get(0).getVocabularyElements().get(0).getTerm());
    assertEquals("native", vocabularyConfiguration.getGroups().get(0).getVocabularyElements().get(0).getLabels().get("en"));
    assertEquals("Indigène", vocabularyConfiguration.getGroups().get(0).getVocabularyElements().get(0).getLabels().get("fr"));
  }
  
}
