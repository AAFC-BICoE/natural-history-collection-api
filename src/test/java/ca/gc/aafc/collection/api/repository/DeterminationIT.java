package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import io.crnk.core.queryspec.QuerySpec;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class DeterminationIT extends CollectionModuleBaseIT {

  @Inject
  private MaterialSampleRepository materialSampleRepository;

  @Test
  void find() {
    MaterialSampleDto dto = MaterialSampleTestFixture.newMaterialSample();
    Determination determination = newDetermination()
        .isPrimary(true)
        .determiner(List.of(UUID.randomUUID())).build();
    dto.setDetermination(new ArrayList<>(List.of(determination)));

    Determination result = materialSampleRepository
        .findOne(materialSampleRepository.create(dto).getUuid(),
            new QuerySpec(MaterialSampleDto.class)).getDetermination().get(0);

    // Assert determination
    Assertions.assertNotNull(result);
    Assertions.assertEquals(determination.getVerbatimDeterminer(), result.getVerbatimDeterminer());
    Assertions.assertEquals(determination.getVerbatimDate(), result.getVerbatimDate());
    Assertions.assertEquals(determination.getVerbatimScientificName(), result.getVerbatimScientificName());
    Assertions.assertEquals(determination.getDeterminedOn(), result.getDeterminedOn());
    Assertions.assertEquals(determination.getDeterminer().get(0), result.getDeterminer().get(0));
    Assertions.assertEquals(determination.getQualifier(), result.getQualifier());
    Assertions.assertEquals(determination.getScientificNameSource(), result.getScientificNameSource());
    Assertions.assertEquals(determination.getTypeStatus(), result.getTypeStatus());
    Assertions.assertEquals(determination.getTypeStatusEvidence(), result.getTypeStatusEvidence());
    Assertions.assertEquals(determination.getScientificName(), result.getScientificName());
    Assertions.assertEquals(determination.getIsPrimary(), result.getIsPrimary());
    Assertions.assertEquals(
      determination.getScientificNameDetails().getSourceUrl(),
      result.getScientificNameDetails().getSourceUrl());
    Assertions.assertEquals(
      determination.getScientificNameDetails().getRecordedOn(),
      result.getScientificNameDetails().getRecordedOn());
  }

  @Test
  void create_WhenDeterminationHasValidationConstraintViolation_ThrowsValidationException() {
    MaterialSampleDto dto = MaterialSampleTestFixture.newMaterialSample();
    dto.setDetermination(new ArrayList<>(List.of(Determination.builder()
      .verbatimScientificName(RandomStringUtils.randomAlphabetic(350)) // name to long
      .build())));
    Assertions.assertThrows(ValidationException.class, () -> materialSampleRepository.create(dto));
  }

  @SneakyThrows
  private Determination.DeterminationBuilder newDetermination() {
    return Determination.builder()
      .verbatimDeterminer(RandomStringUtils.randomAlphabetic(3))
      .verbatimDate(LocalDate.now().toString())
      .isPrimary(false)
      .verbatimScientificName(RandomStringUtils.randomAlphabetic(3))
      .scientificNameDetails(Determination.ScientificNameSourceDetails.builder()
        .sourceUrl(new URL("https://www.google.com").toString())
        .recordedOn(LocalDate.now().minusDays(1))
        .build())
      .transcriberRemarks(RandomStringUtils.randomAlphabetic(50))
      .verbatimRemarks(RandomStringUtils.randomAlphabetic(50))
      .determinationRemarks(RandomStringUtils.randomAlphabetic(50));
  }

}
