package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.MaterialSampleService;
import ca.gc.aafc.collection.api.service.PreparationProcessDefinitionService;
import ca.gc.aafc.collection.api.service.PreparationProcessService;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.dina.jpa.BaseDAO;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.UUID;

public class PreparationProcessCRUDIT extends CollectionModuleBaseIT {
  public static final UUID AGENT_ID = UUID.randomUUID();
  public static final String CREATED_BY = "dina";
  public static final LocalDateTime START_DATE_TIME = LocalDateTime.now().minusDays(2);
  public static final LocalDateTime END_DATE_TIME = LocalDateTime.now().minusDays(1);

  @Inject
  private BaseDAO baseDAO;
  private PreparationProcessService processService;
  private PreparationProcess prepUnderTest;
  private PreparationProcessDefinition definition;
  private MaterialSample sourceMaterialSample;

  @BeforeEach
  void setUp() {
    this.processService = new PreparationProcessService(baseDAO);
    PreparationProcessDefinitionService definitionService = new PreparationProcessDefinitionService(baseDAO);
    MaterialSampleService materialSampleService = new MaterialSampleService(baseDAO);

    this.definition = newDefinition();
    definitionService.create(definition);

    this.sourceMaterialSample = MaterialSampleFactory.newMaterialSample()
      .dwcCatalogNumber("dwcCatalogNumber")
      .createdBy("expectedCreatedBy")
      .build();
    materialSampleService.create(sourceMaterialSample);

    this.prepUnderTest = persistPrepProcess(definition, sourceMaterialSample);
  }

  @Test
  void create() {
    Assertions.assertNotNull(prepUnderTest.getId());
    Assertions.assertNotNull(prepUnderTest.getCreatedOn());
    Assertions.assertNotNull(prepUnderTest.getUuid());
  }

  @Test
  void find() {
    PreparationProcess result = processService.findOne(prepUnderTest.getUuid(), PreparationProcess.class);
    Assertions.assertEquals(AGENT_ID, result.getAgentId());
    Assertions.assertEquals(CREATED_BY, result.getCreatedBy());
    Assertions.assertEquals(definition.getUuid(), result.getPreparationProcessDefinition().getUuid());
    Assertions.assertEquals(sourceMaterialSample.getUuid(), result.getSourceMaterialSample().getUuid());
    Assertions.assertEquals(START_DATE_TIME, result.getStartDateTime());
    Assertions.assertEquals(END_DATE_TIME, result.getEndDateTime());
  }

  private PreparationProcess persistPrepProcess(
    PreparationProcessDefinition def,
    MaterialSample materialSample
  ) {
    PreparationProcess build = PreparationProcess.builder()
      .createdBy(CREATED_BY)
      .agentId(AGENT_ID)
      .sourceMaterialSample(materialSample)
      .preparationProcessDefinition(def)
      .startDateTime(START_DATE_TIME)
      .endDateTime(END_DATE_TIME)
      .build();
    processService.create(build);
    return build;
  }

  private static PreparationProcessDefinition newDefinition() {
    return PreparationProcessDefinition.builder()
      .name(RandomStringUtils.randomAlphabetic(5))
      .group(RandomStringUtils.randomAlphabetic(5))
      .createdBy(RandomStringUtils.randomAlphabetic(5))
      .build();
  }
}
