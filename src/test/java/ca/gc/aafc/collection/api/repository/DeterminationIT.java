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

class DeterminationIT extends CollectionModuleBaseIT {
  @Inject
  private MaterialSampleRepository materialSampleRepository;

  @Test
  void find() {
    Determination.DeterminationDetail detail = Determination.DeterminationDetail.builder()
      .determinedOn(LocalDate.now())
      .build();

    Determination determination = Determination.builder()
      .verbatimAgent(RandomStringUtils.randomAlphabetic(3))
      .verbatimDate(LocalDate.now().toString())
      .verbatimScientificName(RandomStringUtils.randomAlphabetic(3))
      .details(List.of(detail))
      .build();

    MaterialSampleDto dto = MaterialSampleTestFixture.newMaterialSample();
    dto.setDetermination(determination);

    Determination resultDetermination = materialSampleRepository.findOne(
      materialSampleRepository.create(dto).getUuid(), new QuerySpec(MaterialSampleDto.class)).getDetermination();
    Assertions.assertNotNull(resultDetermination);
    Assertions.assertEquals(determination.getVerbatimAgent(), resultDetermination.getVerbatimAgent());
    Assertions.assertEquals(determination.getVerbatimDate(), resultDetermination.getVerbatimDate());
    Assertions.assertEquals(
      determination.getVerbatimScientificName(), resultDetermination.getVerbatimScientificName());

    Determination.DeterminationDetail resultDetail = resultDetermination.getDetails().get(0);
    Assertions.assertNotNull(resultDetail);

  }

}
