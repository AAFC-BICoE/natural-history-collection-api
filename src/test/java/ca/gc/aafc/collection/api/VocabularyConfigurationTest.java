package ca.gc.aafc.collection.api;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.i18n.MultilingualTitle;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = CollectionModuleApiLauncher.class)
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
    assertEquals("captive", getTitleForLang(degreeOfEstablishment.get(0).getMultilingualTitle(),"en").orElse("not found"));
    assertEquals("captif", getTitleForLang(degreeOfEstablishment.get(0).getMultilingualTitle(),"fr").orElse("not found"));
  }

  private Optional<String> getTitleForLang(MultilingualTitle multilingualTitle, String lang) {
    return multilingualTitle.getTitles().stream()
            .filter( mtp -> lang.equals(mtp.getLang()))
            .map(MultilingualTitle.MultilingualTitlePair::getTitle)
            .findFirst();
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
  void unitsOfMeasurement() {
    List<CollectionVocabularyConfiguration.CollectionVocabularyElement> unitsOfMeasurement = vocabularyConfiguration.getVocabulary().get("unitsOfMeasurement");
    assertEquals(4, unitsOfMeasurement.size());
  }

  @Test
  void associationType() {
    List<CollectionVocabularyConfiguration.CollectionVocabularyElement> associationType = vocabularyConfiguration.getVocabulary().get("associationType");
    assertEquals(10, associationType.size());
    associationType.forEach(assertVocabElement());

    assertEquals(10,
        associationType.stream().filter(o -> StringUtils.isNotBlank(o.getInverseOf())).count());
  }

  @Test
  void materialSampleType() {

    List<CollectionVocabularyConfiguration.CollectionVocabularyElement> materialSampleType = vocabularyConfiguration.getVocabulary().get("materialSampleType");

    List<MaterialSample.MaterialSampleType> fromVocabularyFile = materialSampleType.stream()
        .map(mst -> MaterialSample.MaterialSampleType.fromString(mst.getName()).orElse(null))
        .toList();

    assertArrayEquals(MaterialSample.MaterialSampleType.values(), fromVocabularyFile.toArray());

  }

  private static Consumer<CollectionVocabularyConfiguration.CollectionVocabularyElement> assertVocabElement() {
    return vocabularyElement -> {
      assertNotNull(vocabularyElement.getName());
      assertNotNull(vocabularyElement.getTerm());
      assertNotNull(vocabularyElement.getMultilingualTitle());
    };
  }
}
