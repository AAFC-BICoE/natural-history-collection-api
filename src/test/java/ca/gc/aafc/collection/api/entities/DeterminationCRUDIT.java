package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.DeterminationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.UUID;

class DeterminationCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private DeterminationService service;

  @Test
  void find() {
    Determination expected = Determination.builder()
      .createdBy(RandomStringUtils.randomAlphabetic(3))
      .determinedOn(LocalDate.now().minusMonths(2))
      .determiner(UUID.randomUUID())
      .qualifier(RandomStringUtils.randomAlphabetic(3))
      .scientificNameDetails(RandomStringUtils.randomAlphabetic(3))
      .typeStatus(Determination.TypeStatus.TypeStatus)
      .scientificNameSource(Determination.ScientificNameSource.ColPlus)
      .typeStatusEvidence(RandomStringUtils.randomAlphabetic(3))
      .build();
    service.create(expected);

    Determination result = service.findOne(expected.getUuid(), Determination.class);
    Assertions.assertNotNull(result.getCreatedOn());
    Assertions.assertEquals(expected.getCreatedBy(), result.getCreatedBy());
    Assertions.assertEquals(expected.getDeterminer(), result.getDeterminer());
    Assertions.assertEquals(expected.getDeterminedOn(), result.getDeterminedOn());
    Assertions.assertEquals(expected.getQualifier(), result.getQualifier());
    Assertions.assertEquals(expected.getScientificNameDetails(), result.getScientificNameDetails());
    Assertions.assertEquals(expected.getScientificNameSource(), result.getScientificNameSource());
    Assertions.assertEquals(expected.getTypeStatus(), result.getTypeStatus());
    Assertions.assertEquals(expected.getTypeStatusEvidence(), result.getTypeStatusEvidence());
  }
}
