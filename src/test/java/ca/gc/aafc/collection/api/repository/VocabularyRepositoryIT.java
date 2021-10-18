package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.CollectionVocabularyConfiguration.CollectionVocabularyElement;
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
  private CollectionVocabularyConfiguration vocabularyConfiguration;

  @Test
  public void findAll_VocabularyConfiguration() {
    List<VocabularyDto> listOfVocabularies =
      vocabularyConfigurationRepository.findAll(new QuerySpec(VocabularyDto.class));
    assertEquals(7, listOfVocabularies.size());

    List<List<CollectionVocabularyElement>> listOfVocabularyElements = new ArrayList<>();
    for (VocabularyDto vocabularyDto : listOfVocabularies) {
      listOfVocabularyElements.add(vocabularyDto.getVocabularyElements());
    }

    MatcherAssert.assertThat(
      listOfVocabularyElements,
      Matchers.containsInAnyOrder(
        vocabularyConfiguration.getCollectionVocabulary().get("degreeOfEstablishment"),
        vocabularyConfiguration.getCollectionVocabulary().get("srs"),
        vocabularyConfiguration.getCollectionVocabulary().get("coordinateSystem"),
        vocabularyConfiguration.getCollectionVocabulary().get("typeStatus"),
        vocabularyConfiguration.getCollectionVocabulary().get("substrate"),
        vocabularyConfiguration.getCollectionVocabulary().get("materialSampleState"),
        vocabularyConfiguration.getCollectionVocabulary().get("associationType")
      ));
  }

}
