package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

class PreparationProcessElementCRUDIT extends CollectionModuleBaseIT {
  public static final String CREATED_BY = "d1af";

  private PreparationProcess preparation;
  private PreparationProcessElement elementUnderTest;
  private MaterialSample materialSample;

  @BeforeEach
  void setUp() {

    PreparationProcessDefinition definition = newDefinition();
    preparationProcessDefinitionService.create(definition);

    MaterialSample sourceMaterialSample = newPhysical();
    materialSampleService.create(sourceMaterialSample);

    this.preparation = newPrep(definition, sourceMaterialSample);
    preparationProcessService.create(preparation);

    this.materialSample = newPhysical();
    materialSampleService.create(materialSample);

    this.elementUnderTest = PreparationProcessElement.builder()
      .createdBy(CREATED_BY)
      .preparationProcess(this.preparation)
      .materialSample(this.materialSample)
      .build();
    preparationProcessElementService.create(this.elementUnderTest);
  }

  private MaterialSample newPhysical() {
    return MaterialSampleFactory.newMaterialSample()
      .dwcCatalogNumber("dwcCatalogNumber")
      .createdBy("expectedCreatedBy")
      .build();
  }

  @Test
  void create() {
    Assertions.assertNotNull(elementUnderTest.getId());
    Assertions.assertNotNull(elementUnderTest.getCreatedOn());
    Assertions.assertNotNull(elementUnderTest.getUuid());
  }

  @Test
  void find() {
    PreparationProcessElement result = preparationProcessElementService.findOne(
      elementUnderTest.getUuid(),
      PreparationProcessElement.class);
    Assertions.assertEquals(preparation.getUuid(), result.getPreparationProcess().getUuid());
    Assertions.assertEquals(materialSample.getUuid(), result.getMaterialSample().getUuid());
    Assertions.assertEquals(CREATED_BY, result.getCreatedBy());
  }

  private static PreparationProcess newPrep(
    PreparationProcessDefinition def,
    MaterialSample materialSample
  ) {
    return PreparationProcess.builder()
      .createdBy("CREATED_BY")
      .agentId(UUID.randomUUID())
      .sourceMaterialSample(materialSample)
      .preparationProcessDefinition(def)
      .startDateTime(LocalDateTime.now().minusYears(1))
      .endDateTime(LocalDateTime.now().minusDays(1))
      .build();
  }

  private static PreparationProcessDefinition newDefinition() {
    return PreparationProcessDefinition.builder()
      .name(RandomStringUtils.randomAlphabetic(5))
      .group(RandomStringUtils.randomAlphabetic(5))
      .createdBy(RandomStringUtils.randomAlphabetic(5))
      .build();
  }
}