package ca.gc.aafc.collection.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

  @Test
  void typeStatus() {
    List<VocabularyConfiguration.VocabularyElement> typeStatus = vocabularyConfiguration.getVocabulary()
      .get("typeStatus");
    assertEquals(11, typeStatus.size());
    typeStatus.forEach(assertVocabElement());
  }

  @Test
  void coordinateSystem() {
    List<VocabularyConfiguration.VocabularyElement> coordinateSystem = vocabularyConfiguration.getVocabulary()
      .get("coordinateSystem");
    assertEquals(4, coordinateSystem.size());
    coordinateSystem.forEach(assertVocabElement());
  }

  @Test
  void srs() {
    List<VocabularyConfiguration.VocabularyElement> srs = vocabularyConfiguration.getVocabulary().get("srs");
    assertEquals(2, srs.size());
    srs.forEach(assertVocabElement());
  }

  private static Consumer<VocabularyConfiguration.VocabularyElement> assertVocabElement() {
    return vocabularyElement -> {
      assertNotNull(vocabularyElement.getName());
      assertNotNull(vocabularyElement.getTerm());
      assertNotNull(vocabularyElement.getLabels());
    };
  }
}
