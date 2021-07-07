package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

class DeterminationIT extends CollectionModuleBaseIT {

  @Inject
  private MaterialSampleRepository materialSampleRepository;

  @Test
  void find() {
    Determination.DeterminationDetail detail = newDetail();
    Determination determination = newDetermination(detail);

    MaterialSampleDto dto = MaterialSampleTestFixture.newMaterialSample();
    dto.setDetermination(determination);
    Determination resultDetermination = materialSampleRepository.findOne(
      materialSampleRepository.create(dto).getUuid(), new QuerySpec(MaterialSampleDto.class))
      .getDetermination();

    // Assert determination
    Assertions.assertNotNull(resultDetermination);
    Assertions.assertEquals(determination.getVerbatimAgent(), resultDetermination.getVerbatimAgent());
    Assertions.assertEquals(determination.getVerbatimDate(), resultDetermination.getVerbatimDate());
    Assertions.assertEquals(
      determination.getVerbatimScientificName(), resultDetermination.getVerbatimScientificName());

    // Assert determination detail
    Determination.DeterminationDetail resultDetail = resultDetermination.getDetails().get(0);
    Assertions.assertNotNull(resultDetail);
    Assertions.assertEquals(resultDetail.getDeterminer().get(0), resultDetail.getDeterminer().get(0));
    Assertions.assertEquals(resultDetail.getDeterminedOn(), resultDetail.getDeterminedOn());
    Assertions.assertEquals(resultDetail.getQualifier(), resultDetail.getQualifier());
    Assertions.assertEquals(resultDetail.getScientificNameDetails(), resultDetail.getScientificNameDetails());
    Assertions.assertEquals(resultDetail.getScientificNameSource(), resultDetail.getScientificNameSource());
    Assertions.assertEquals(resultDetail.getTypeStatus(), resultDetail.getTypeStatus());
    Assertions.assertEquals(resultDetail.getTypeStatusEvidence(), resultDetail.getTypeStatusEvidence());
  }

  private Determination newDetermination(Determination.DeterminationDetail detail) {
    return Determination.builder()
      .verbatimAgent(RandomStringUtils.randomAlphabetic(3))
      .verbatimDate(LocalDate.now().toString())
      .verbatimScientificName(RandomStringUtils.randomAlphabetic(3))
      .details(List.of(detail))
      .build();
  }

  private Determination.DeterminationDetail newDetail() {
    return Determination.DeterminationDetail.builder()
      .determiner(List.of(UUID.randomUUID()))
      .determinedOn(LocalDate.now())
      .qualifier(RandomStringUtils.randomAlphabetic(3))
      .scientificNameDetails(RandomStringUtils.randomAlphabetic(3))
      .scientificNameSource(RandomStringUtils.randomAlphabetic(3))
      .typeStatus(RandomStringUtils.randomAlphabetic(3))
      .typeStatusEvidence(RandomStringUtils.randomAlphabetic(3))
      .build();
  }

}
