package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.OrganismDto;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.Determination.ScientificNameSource;
import ca.gc.aafc.collection.api.testsupport.fixtures.OrganismTestFixture;
import io.crnk.core.queryspec.QuerySpec;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.apache.commons.lang3.RandomStringUtils;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

class DeterminationIT extends CollectionModuleBaseIT {

  @Inject
  private OrganismRepository organismRepository;

  @Test
  void find() {

    Determination determination = newDetermination()
        .isPrimary(true)
        .determiner(List.of(UUID.randomUUID())).build();

    OrganismDto organismDto = OrganismTestFixture.newOrganism(determination);

    Determination result = organismRepository
        .findOne(organismRepository.create(organismDto).getUuid(),
            new QuerySpec(OrganismDto.class)).getDetermination().get(0);

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

    Determination determination = Determination.builder()
        .verbatimScientificName(RandomStringUtils.randomAlphabetic(350)) // name to long
        .build();

    OrganismDto dto = OrganismTestFixture.newOrganism(determination);

    Assertions.assertThrows(ValidationException.class, () -> organismRepository.create(dto));
  }

  @SneakyThrows
  private Determination.DeterminationBuilder newDetermination() {
    return Determination.builder()
      .verbatimDeterminer(RandomStringUtils.randomAlphabetic(3))
      .verbatimDate(LocalDate.now().toString())
      .isPrimary(false)
      .verbatimScientificName(RandomStringUtils.randomAlphabetic(3))
      .scientificNameSource(ScientificNameSource.CUSTOM)
      .scientificNameDetails(Determination.ScientificNameSourceDetails.builder()
        .currentName("scientificName")
        .isSynonym(false)
        .classificationPath(RandomStringUtils.randomAlphabetic(50))
        .classificationRanks(RandomStringUtils.randomAlphabetic(50))
        .sourceUrl(new URL("https://www.google.com").toString())
        .recordedOn(LocalDate.now().minusDays(1))
        .build())
      .transcriberRemarks(RandomStringUtils.randomAlphabetic(50))
      .verbatimRemarks(RandomStringUtils.randomAlphabetic(50))
      .determinationRemarks(RandomStringUtils.randomAlphabetic(50));
  }

}
