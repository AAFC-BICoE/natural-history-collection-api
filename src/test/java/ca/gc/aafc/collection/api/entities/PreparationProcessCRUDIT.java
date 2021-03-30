package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.PreparationProcessService;
import ca.gc.aafc.dina.jpa.BaseDAO;
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
  private PreparationProcessService maService;
  private PreparationProcess prepUnderTest;

  @BeforeEach
  void setUp() {
    maService = new PreparationProcessService(baseDAO);
    prepUnderTest = persistPrepProcess();
  }

  @Test
  void create() {
    Assertions.assertNotNull(prepUnderTest.getId());
    Assertions.assertNotNull(prepUnderTest.getCreatedOn());
    Assertions.assertNotNull(prepUnderTest.getUuid());
  }

  @Test
  void find() {
    PreparationProcess result = maService.findOne(prepUnderTest.getUuid(), PreparationProcess.class);
    Assertions.assertEquals(AGENT_ID, result.getAgentId());
    Assertions.assertEquals(CREATED_BY, result.getCreatedBy());
  }

  private PreparationProcess persistPrepProcess() {
    PreparationProcess build = PreparationProcess.builder()
      .createdBy(CREATED_BY)
      .agentId(AGENT_ID)
      .build();
    maService.create(build);
    return build;
  }
}
