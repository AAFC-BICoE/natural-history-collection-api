package ca.gc.aafc.collection.api;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class VocabularyConfigurationTest extends CollectionModuleBaseIT {

  @Inject
  private CollectionVocabularyConfiguration vocabularyConfiguration;

  @Test
  void getDegreeOfEstablishment() {
    List<CollectionVocabularyConfiguration.CollectionVocabularyElement> degreeOfEstablishment = vocabularyConfiguration.getVocabulary()
        .get("degreeOfEstablishment");

    assertNotNull(vocabularyConfiguration.getVocabulary());
    assertEquals("captive", degreeOfEstablishment.get(0).getName());
    assertEquals("https://dwc.tdwg.org/doe/#dwcdoe_d002", degreeOfEstablishment.get(0).getTerm());
    assertEquals("captive", degreeOfEstablishment.get(0).getLabels().get("en"));
    assertEquals("captif", degreeOfEstablishment.get(0).getLabels().get("fr"));
  }

  @Test
  void typeStatus() {
    List<CollectionVocabularyConfiguration.CollectionVocabularyElement> typeStatus = vocabularyConfiguration.getVocabulary()
      .get("typeStatus");
    assertEquals(11, typeStatus.size());
    typeStatus.forEach(assertVocabElement());
  }

  @Test
  void coordinateSystem() {
    List<CollectionVocabularyConfiguration.CollectionVocabularyElement> coordinateSystem = vocabularyConfiguration.getVocabulary()
      .get("coordinateSystem");
    assertEquals(4, coordinateSystem.size());
    coordinateSystem.forEach(assertVocabElement());
  }

  @Test
  void srs() {
    List<CollectionVocabularyConfiguration.CollectionVocabularyElement> srs = vocabularyConfiguration.getVocabulary().get("srs");
    assertEquals(2, srs.size());
    srs.forEach(assertVocabElement());
  }

  @Test
  void associationType() {
    List<CollectionVocabularyConfiguration.CollectionVocabularyElement> associationType = vocabularyConfiguration.getVocabulary().get("associationType");
    assertEquals(10, associationType.size());
    associationType.forEach(assertVocabElement());

    assertTrue(
      associationType
        .stream()
        .filter(o -> StringUtils.isNotBlank(o.getInverseOf())).count() == 10
    );
  }

  private static Consumer<CollectionVocabularyConfiguration.CollectionVocabularyElement> assertVocabElement() {
    return vocabularyElement -> {
      assertNotNull(vocabularyElement.getName());
      assertNotNull(vocabularyElement.getTerm());
      assertNotNull(vocabularyElement.getLabels());
    };
  }
}
