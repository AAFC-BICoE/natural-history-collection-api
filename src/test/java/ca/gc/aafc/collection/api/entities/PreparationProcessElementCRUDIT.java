package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.PhysicalEntityService;
import ca.gc.aafc.collection.api.service.PreparationProcessDefinitionService;
import ca.gc.aafc.collection.api.service.PreparationProcessElementService;
import ca.gc.aafc.collection.api.service.PreparationProcessService;
import ca.gc.aafc.collection.api.testsupport.factories.PhysicalEntityFactory;
import ca.gc.aafc.dina.jpa.BaseDAO;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.UUID;

class PreparationProcessElementCRUDIT extends CollectionModuleBaseIT {
  public static final String CREATED_BY = "d1af";
  @Inject
  private BaseDAO baseDAO;
  private PreparationProcessElementService elementService;
  private PreparationProcess preparation;
  private PreparationProcessElement elementUnderTest;
  private PhysicalEntity physicalEntity;

  @BeforeEach
  void setUp() {
    PreparationProcessService processService = new PreparationProcessService(baseDAO);
    PreparationProcessDefinitionService definitionService = new PreparationProcessDefinitionService(baseDAO);
    PhysicalEntityService physicalEntityService = new PhysicalEntityService(baseDAO);
    this.elementService = new PreparationProcessElementService(baseDAO);

    PreparationProcessDefinition definition = newDefinition();
    definitionService.create(definition);

    PhysicalEntity sourcePhysicalEntity = newPhysical();
    physicalEntityService.create(sourcePhysicalEntity);

    this.preparation = newPrep(definition, sourcePhysicalEntity);
    processService.create(preparation);

    this.physicalEntity = newPhysical();
    physicalEntityService.create(physicalEntity);

    this.elementUnderTest = PreparationProcessElement.builder()
      .createdBy(CREATED_BY)
      .preparationProcess(this.preparation)
      .physicalEntity(this.physicalEntity)
      .build();
    this.elementService.create(this.elementUnderTest);
  }

  private PhysicalEntity newPhysical() {
    return PhysicalEntityFactory.newPhysicalEntity()
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
    PreparationProcessElement result = elementService.findOne(
      elementUnderTest.getUuid(),
      PreparationProcessElement.class);
    Assertions.assertEquals(preparation.getUuid(), result.getPreparationProcess().getUuid());
    Assertions.assertEquals(physicalEntity.getUuid(), result.getPhysicalEntity().getUuid());
    Assertions.assertEquals(CREATED_BY, result.getCreatedBy());
  }

  private static PreparationProcess newPrep(
    PreparationProcessDefinition def,
    PhysicalEntity physicalEntity
  ) {
    return PreparationProcess.builder()
      .createdBy("CREATED_BY")
      .agentId(UUID.randomUUID())
      .sourcePhysicalEntity(physicalEntity)
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