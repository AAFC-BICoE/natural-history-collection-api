package ca.gc.aafc.collection.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class VocabularyConfigurationTest extends CollectionModuleBaseIT {

  @Inject
  private VocabularyConfiguration vocabularyConfiguration;

  @Test
  void getDegreeOfEstablishment() {
    List<VocabularyConfiguration.VocabularyElement> degreeOfEstablishment = vocabularyConfiguration.getVocabulary().get("degreeOfEstablishment");

    assertNotNull(degreeOfEstablishment);
    assertEquals("native", degreeOfEstablishment.get(0).getName());
    assertEquals("https://dwc.tdwg.org/doe/#dwcdoe_d001", degreeOfEstablishment.get(0).getTerm());
    assertEquals("native", degreeOfEstablishment.get(0).getLabels().get("en"));
    assertEquals("indigène", degreeOfEstablishment.get(0).getLabels().get("fr"));
  }
  
}
