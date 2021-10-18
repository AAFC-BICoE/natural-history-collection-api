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
  private CollectionVocabularyConfiguration vocabularyConfiguration;

  @Test
  void getDegreeOfEstablishment() {
    //List<CollectionVocabularyConfiguration.CollectionVocabularyElement> degreeOfEstablishment = vocabularyConfiguration.getCollectionVocabulary();

    assertNotNull(vocabularyConfiguration.getCollectionVocabulary());
    assertEquals("expected", vocabularyConfiguration.getCollectionVocabulary());
    // assertEquals("native", degreeOfEstablishment.get(0).getName());
    // assertEquals("https://dwc.tdwg.org/doe/#dwcdoe_d001", degreeOfEstablishment.get(0).getTerm());
    // assertEquals("native", degreeOfEstablishment.get(0).getLabels().get("en"));
    // assertEquals("indigène", degreeOfEstablishment.get(0).getLabels().get("fr"));
  }

  @Test
  void typeStatus() {
    List<CollectionVocabularyConfiguration.CollectionVocabularyElement> typeStatus = vocabularyConfiguration.getCollectionVocabulary()
      .get("typeStatus");
    assertEquals(11, typeStatus.size());
    typeStatus.forEach(assertVocabElement());
  }

  @Test
  void coordinateSystem() {
    List<CollectionVocabularyConfiguration.CollectionVocabularyElement> coordinateSystem = vocabularyConfiguration.getCollectionVocabulary()
      .get("coordinateSystem");
    assertEquals(4, coordinateSystem.size());
    coordinateSystem.forEach(assertVocabElement());
  }

  @Test
  void srs() {
    List<CollectionVocabularyConfiguration.CollectionVocabularyElement> srs = vocabularyConfiguration.getCollectionVocabulary().get("srs");
    assertEquals(2, srs.size());
    srs.forEach(assertVocabElement());
  }

  private static Consumer<CollectionVocabularyConfiguration.CollectionVocabularyElement> assertVocabElement() {
    return vocabularyElement -> {
      assertNotNull(vocabularyElement.getName());
      assertNotNull(vocabularyElement.getTerm());
      assertNotNull(vocabularyElement.getLabels());
    };
  }
}
