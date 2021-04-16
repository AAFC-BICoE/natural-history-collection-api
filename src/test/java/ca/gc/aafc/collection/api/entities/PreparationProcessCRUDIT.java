package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.PreparationProcessDefinitionService;
import ca.gc.aafc.collection.api.service.PreparationProcessService;
import ca.gc.aafc.dina.jpa.BaseDAO;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.UUID;

public class PreparationProcessCRUDIT extends CollectionModuleBaseIT {
  public static final UUID AGENT_ID = UUID.randomUUID();
  public static final String CREATED_BY = "dina";

  @Inject
  private BaseDAO baseDAO;
  private PreparationProcessService processService;
  private PreparationProcessDefinitionService definitionService;
  private PreparationProcess prepUnderTest;
  private PreparationProcessDefinition definition;

  @BeforeEach
  void setUp() {
    this.processService = new PreparationProcessService(baseDAO);
    this.definitionService = new PreparationProcessDefinitionService(baseDAO);

    this.definition = newDefinition();
    definitionService.create(definition);

    this.prepUnderTest = persistPrepProcess(definition);
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
  }

  private PreparationProcess persistPrepProcess(PreparationProcessDefinition def) {
    PreparationProcess build = PreparationProcess.builder()
      .createdBy(CREATED_BY)
      .agentId(AGENT_ID)
      .preparationProcessDefinition(def)
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
