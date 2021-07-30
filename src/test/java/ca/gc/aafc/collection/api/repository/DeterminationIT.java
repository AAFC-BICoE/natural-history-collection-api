package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

class DeterminationIT extends CollectionModuleBaseIT {

  @Inject
  private MaterialSampleRepository materialSampleRepository;
  private Determination.DeterminationDetail detail;
  private Determination determination;

  @BeforeEach
  void setUp() {
    detail = newDetail();
    determination = newDetermination(detail);
  }

  @Test
  void find() {
    MaterialSampleDto dto = MaterialSampleTestFixture.newMaterialSample();
    dto.setDetermination(determination);
    Determination resultDetermination = materialSampleRepository.findOne(
      materialSampleRepository.create(dto).getUuid(), new QuerySpec(MaterialSampleDto.class))
      .getDetermination();

    // Assert determination
    Assertions.assertNotNull(resultDetermination);
    Assertions.assertEquals(determination.getVerbatimAgent(), resultDetermination.getVerbatimAgent());
    Assertions.assertEquals(determination.getVerbatimDate(), resultDetermination.getVerbatimDate());
    Assertions.assertEquals(determination.getTranscriberRemarks(), resultDetermination.getTranscriberRemarks());
    Assertions.assertEquals(
      determination.getVerbatimScientificName(), resultDetermination.getVerbatimScientificName());

    // Assert determination detail
    Determination.DeterminationDetail resultDetail = resultDetermination.getDetails().get(0);
    Assertions.assertNotNull(resultDetail);
    Assertions.assertEquals(detail.getDeterminer().get(0), resultDetail.getDeterminer().get(0));
    Assertions.assertEquals(detail.getDeterminedOn(), resultDetail.getDeterminedOn());
    Assertions.assertEquals(detail.getQualifier(), resultDetail.getQualifier());
    Assertions.assertEquals(detail.getScientificNameDetails(), resultDetail.getScientificNameDetails());
    Assertions.assertEquals(detail.getScientificNameSource(), resultDetail.getScientificNameSource());
    Assertions.assertEquals(detail.getTypeStatus(), resultDetail.getTypeStatus());
    Assertions.assertEquals(detail.getTypeStatusEvidence(), resultDetail.getTypeStatusEvidence());
    Assertions.assertEquals(detail.getScientificName(), resultDetail.getScientificName());
  }

  @Test
  void create_WhenDetailHasValidationConstraintViolation_ThrowsValidationException() {
    Determination.DeterminationDetail invalidDetail = Determination.DeterminationDetail.builder()
      .typeStatus(RandomStringUtils.randomAlphabetic(100)) // Status to long
      .build();
    MaterialSampleDto dto = MaterialSampleTestFixture.newMaterialSample();
    dto.setDetermination(Determination.builder().details(List.of(invalidDetail)).build());
    Assertions.assertThrows(ValidationException.class, () -> materialSampleRepository.create(dto));
  }

  @Test
  void create_WhenDeterminationHasValidationConstraintViolation_ThrowsValidationException() {
    MaterialSampleDto dto = MaterialSampleTestFixture.newMaterialSample();
    dto.setDetermination(Determination.builder()
      .verbatimScientificName(RandomStringUtils.randomAlphabetic(350)) // name to long
      .build());
    Assertions.assertThrows(ValidationException.class, () -> materialSampleRepository.create(dto));
  }

  private Determination newDetermination(Determination.DeterminationDetail detail) {
    return Determination.builder()
      .verbatimAgent(RandomStringUtils.randomAlphabetic(3))
      .verbatimDate(LocalDate.now().toString())
      .verbatimScientificName(RandomStringUtils.randomAlphabetic(3))
      .transcriberRemarks(RandomStringUtils.randomAlphabetic(50))
      .details(List.of(detail))
      .build();
  }

  private Determination.DeterminationDetail newDetail() {
    return Determination.DeterminationDetail.builder()
      .scientificName(RandomStringUtils.randomAlphabetic(4))
      .determiner(List.of(UUID.randomUUID()))
      .determinedOn(LocalDate.now())
      .qualifier(RandomStringUtils.randomAlphabetic(3))
      .scientificNameDetails(RandomStringUtils.randomAlphabetic(3))
      .scientificNameSource(Determination.ScientificNameSource.COLPLUS)
      .typeStatus(RandomStringUtils.randomAlphabetic(3))
      .typeStatusEvidence(RandomStringUtils.randomAlphabetic(3))
      .build();
  }

}
