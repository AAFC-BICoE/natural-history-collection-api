package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.VocabularyDto;
import io.crnk.core.queryspec.QuerySpec;

public class VocabularyConfigurationRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private VocabularyRepository vocabularyConfigurationRepository;

  @Test
  public void findAll_VocabularyConfiguration() {
    VocabularyDto vocabularyConfigurationDto =
      vocabularyConfigurationRepository.findAll(new QuerySpec(VocabularyDto.class)).get(0);
    assertEquals("degreeOfEstablishment", vocabularyConfigurationDto.getId());
    assertEquals("native", vocabularyConfigurationDto.getVocabularyElements().get(0).getName());
    assertEquals("https://dwc.tdwg.org/doe/#dwcdoe_d001", vocabularyConfigurationDto.getVocabularyElements().get(0).getTerm());
    assertEquals("native", vocabularyConfigurationDto.getVocabularyElements().get(0).getLabels().get("en"));
    assertEquals("Indigène", vocabularyConfigurationDto.getVocabularyElements().get(0).getLabels().get("fr"));
    }
  
}
