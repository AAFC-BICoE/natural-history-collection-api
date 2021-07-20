package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.VocabularyConfigurationDto;
import io.crnk.core.queryspec.QuerySpec;

public class VocabularyConfigurationRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private VocabularyConfigurationRepository vocabularyConfigurationRepository;

  @Test
  public void findAll_VocabularyConfiguration() {
    VocabularyConfigurationDto vocabularyConfigurationDto =
      vocabularyConfigurationRepository.findAll(new QuerySpec(VocabularyConfigurationDto.class)).get(0);
    assertEquals("native", vocabularyConfigurationDto.getName());
    assertEquals("https://dwc.tdwg.org/doe/#dwcdoe_d001", vocabularyConfigurationDto.getTerm());
    assertEquals("native", vocabularyConfigurationDto.getLabels().get("en"));
    assertEquals("Indigène", vocabularyConfigurationDto.getLabels().get("fr"));
    }
  
}
