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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class DeterminationIT extends CollectionModuleBaseIT {

  @Inject
  private MaterialSampleRepository materialSampleRepository;
  private Determination determination;

  @BeforeEach
  void setUp() {
    determination = newDetermination();
  }

  @Test
  void find() {
    MaterialSampleDto dto = MaterialSampleTestFixture.newMaterialSample();
    dto.setDetermination(new ArrayList<>(List.of(determination)));
    Determination result = materialSampleRepository.findOne(
      materialSampleRepository.create(dto).getUuid(), new QuerySpec(MaterialSampleDto.class)
    ).getDetermination().get(0);

    // Assert determination
    Assertions.assertNotNull(result);
    Assertions.assertEquals(determination.getVerbatimAgent(), result.getVerbatimAgent());
    Assertions.assertEquals(determination.getVerbatimDate(), result.getVerbatimDate());
    Assertions.assertEquals(determination.getVerbatimScientificName(), result.getVerbatimScientificName());
    Assertions.assertEquals(determination.getDeterminer().get(0), result.getDeterminer().get(0));
    Assertions.assertEquals(determination.getDeterminedOn(), result.getDeterminedOn());
    Assertions.assertEquals(determination.getQualifier(), result.getQualifier());
    Assertions.assertEquals(determination.getScientificNameDetails(), result.getScientificNameDetails());
    Assertions.assertEquals(determination.getScientificNameSource(), result.getScientificNameSource());
    Assertions.assertEquals(determination.getTypeStatus(), result.getTypeStatus());
    Assertions.assertEquals(determination.getTypeStatusEvidence(), result.getTypeStatusEvidence());
    Assertions.assertEquals(determination.getScientificName(), result.getScientificName());
  }

  @Test
  void create_WhenDeterminationHasValidationConstraintViolation_ThrowsValidationException() {
    MaterialSampleDto dto = MaterialSampleTestFixture.newMaterialSample();
    dto.setDetermination(new ArrayList<>(List.of(Determination.builder()
      .verbatimScientificName(RandomStringUtils.randomAlphabetic(350)) // name to long
      .build())));
    Assertions.assertThrows(ValidationException.class, () -> materialSampleRepository.create(dto));
  }

  private Determination newDetermination() {
    return Determination.builder()
      .verbatimAgent(RandomStringUtils.randomAlphabetic(3))
      .verbatimDate(LocalDate.now().toString())
      .verbatimScientificName(RandomStringUtils.randomAlphabetic(3))
      .transcriberRemarks(RandomStringUtils.randomAlphabetic(50))
      .details(List.of(detail))
      .build();
  }

}
