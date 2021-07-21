package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.VocabularyConfiguration.VocabularyElement;
import ca.gc.aafc.collection.api.dto.VocabularyDto;
import io.crnk.core.queryspec.QuerySpec;

public class VocabularyRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private VocabularyRepository vocabularyConfigurationRepository;

  @Test
  public void findAll_VocabularyConfiguration() {
    List<VocabularyDto> listOfVocabularies =
      vocabularyConfigurationRepository.findAll(new QuerySpec(VocabularyDto.class));
    assertEquals(2, listOfVocabularies.size());

    VocabularyElement degreeOfEstablishment = listOfVocabularies.get(1).getVocabularyElements().get(0);
    assertEquals("native", degreeOfEstablishment.getName());
    assertEquals("https://dwc.tdwg.org/doe/#dwcdoe_d001", degreeOfEstablishment.getTerm());
    assertEquals("native", degreeOfEstablishment.getLabels().get("en"));
    assertEquals("Indigène", degreeOfEstablishment.getLabels().get("fr"));

    VocabularyElement SRS_WGS84 = listOfVocabularies.get(0).getVocabularyElements().get(0);
    assertEquals("WGS84 (EPSG:4326)", SRS_WGS84.getName());
    assertEquals("https://www.wikidata.org/wiki/Q11902211", SRS_WGS84.getTerm());
    assertEquals("WGS84 (EPSG:4326)", SRS_WGS84.getLabels().get("en"));
    assertEquals("WGS84 (EPSG:4326)", SRS_WGS84.getLabels().get("fr"));

    VocabularyElement SRS_NAD27 = listOfVocabularies.get(0).getVocabularyElements().get(1);
    assertEquals("NAD27 (EPSG:4276)", SRS_NAD27.getName());
    assertEquals("https://www.wikidata.org/wiki/Q100400484", SRS_NAD27.getTerm());
    assertEquals("NAD27 (EPSG:4276)", SRS_NAD27.getLabels().get("en"));
    assertEquals("NAD27 (EPSG:4276)", SRS_NAD27.getLabels().get("fr"));
    }
  
}
