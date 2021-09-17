package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.VocabularyConfiguration;
import ca.gc.aafc.collection.api.VocabularyConfiguration.VocabularyElement;
import ca.gc.aafc.collection.api.dto.VocabularyDto;
import io.crnk.core.queryspec.QuerySpec;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VocabularyRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private VocabularyRepository vocabularyConfigurationRepository;

  @Inject
  private VocabularyConfiguration vocabularyConfiguration;

  @Test
  public void findAll_VocabularyConfiguration() {
    List<VocabularyDto> listOfVocabularies =
      vocabularyConfigurationRepository.findAll(new QuerySpec(VocabularyDto.class));
    assertEquals(6, listOfVocabularies.size());

    List<List<VocabularyElement>> listOfVocabularyElements = new ArrayList<>();
    for (VocabularyDto vocabularyDto : listOfVocabularies) {
      listOfVocabularyElements.add(vocabularyDto.getVocabularyElements());
    }

    MatcherAssert.assertThat(
      listOfVocabularyElements,
      Matchers.containsInAnyOrder(
        vocabularyConfiguration.getVocabulary().get("degreeOfEstablishment"),
        vocabularyConfiguration.getVocabulary().get("srs"),
        vocabularyConfiguration.getVocabulary().get("coordinateSystem"),
        vocabularyConfiguration.getVocabulary().get("typeStatus"),
        vocabularyConfiguration.getVocabulary().get("substrate"),
        vocabularyConfiguration.getVocabulary().get("materialSampleState")
      ));
  }

}
