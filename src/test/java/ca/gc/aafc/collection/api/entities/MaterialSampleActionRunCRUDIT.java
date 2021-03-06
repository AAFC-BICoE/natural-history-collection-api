package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleActionDefinitionFactory;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleActionRunFactory;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

public class MaterialSampleActionRunCRUDIT extends CollectionModuleBaseIT {
  public static final UUID AGENT_ID = UUID.randomUUID();
  public static final String CREATED_BY = "dina";
  public static final LocalDateTime START_DATE_TIME = LocalDateTime.now().minusDays(2);
  public static final LocalDateTime END_DATE_TIME = LocalDateTime.now().minusDays(1);
  public static final String GROUP = "group";

  private MaterialSampleActionRun prepUnderTest;
  private MaterialSampleActionDefinition definition;
  private MaterialSample sourceMaterialSample;

  @BeforeEach
  void setUp() {

    this.definition = MaterialSampleActionDefinitionFactory.newMaterialSampleActionDefinition().build();
    materialSampleActionDefinitionService.create(definition);

    this.sourceMaterialSample = MaterialSampleFactory.newMaterialSample()
      .dwcCatalogNumber("dwcCatalogNumber")
      .createdBy("expectedCreatedBy")
      .build();
    materialSampleService.create(sourceMaterialSample);

    this.prepUnderTest = persistMaterialSampleActionRun(definition, sourceMaterialSample);
  }

  @Test
  void create() {
    Assertions.assertNotNull(prepUnderTest.getId());
    Assertions.assertNotNull(prepUnderTest.getCreatedOn());
    Assertions.assertNotNull(prepUnderTest.getUuid());
  }

  @Test
  void find() {
    MaterialSampleActionRun result = materialSampleActionRunService.findOne(prepUnderTest.getUuid(), MaterialSampleActionRun.class);
    Assertions.assertEquals(AGENT_ID, result.getAgentId());
    Assertions.assertEquals(CREATED_BY, result.getCreatedBy());
    Assertions.assertEquals(definition.getUuid(), result.getMaterialSampleActionDefinition().getUuid());
    Assertions.assertEquals(sourceMaterialSample.getUuid(), result.getSourceMaterialSample().getUuid());
    Assertions.assertEquals(START_DATE_TIME, result.getStartDateTime());
    Assertions.assertEquals(END_DATE_TIME, result.getEndDateTime());
    Assertions.assertEquals(GROUP, result.getGroup());
  }

  private MaterialSampleActionRun persistMaterialSampleActionRun(
    MaterialSampleActionDefinition def,
    MaterialSample materialSample
  ) {
    MaterialSampleActionRun build = MaterialSampleActionRunFactory.newMaterialSampleActionRun()
      .createdBy(CREATED_BY)
      .group(GROUP)
      .agentId(AGENT_ID)
      .sourceMaterialSample(materialSample)
      .materialSampleActionDefinition(def)
      .startDateTime(START_DATE_TIME)
      .endDateTime(END_DATE_TIME)
      .build();
        
    materialSampleActionRunService.create(build);
    return build;
  }
}
