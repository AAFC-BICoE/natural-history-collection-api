package ca.gc.aafc.collection.api.repository;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;

import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.VocabularyConfiguration;
import ca.gc.aafc.collection.api.VocabularyConfiguration.VocabularyElement;
import ca.gc.aafc.collection.api.dto.VocabularyDto;
import io.crnk.core.queryspec.QuerySpec;

public class VocabularyRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private VocabularyRepository vocabularyConfigurationRepository;

  @Inject
  private VocabularyConfiguration vocabularyConfiguration;

  @Test
  @SuppressWarnings( "deprecation" )
  public void findAll_VocabularyConfiguration() {
    List<VocabularyDto> listOfVocabularies =
      vocabularyConfigurationRepository.findAll(new QuerySpec(VocabularyDto.class));
    assertEquals(3, listOfVocabularies.size());

    List<List<VocabularyElement>> listOfVocabularyElements = new ArrayList<>();
    for (VocabularyDto vocabularyDto : listOfVocabularies) {
      listOfVocabularyElements.add(vocabularyDto.getVocabularyElements());
    }

    assertThat(listOfVocabularyElements, 
      Matchers.containsInAnyOrder(
        vocabularyConfiguration.getVocabulary().get("degreeOfEstablishment"),
        vocabularyConfiguration.getVocabulary().get("srs"),
        vocabularyConfiguration.getVocabulary().get("coordinateSystem")
      ));
  }
  
}
