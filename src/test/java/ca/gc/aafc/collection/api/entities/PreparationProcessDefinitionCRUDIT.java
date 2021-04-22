package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.PreparationProcessDefinitionService;
import ca.gc.aafc.dina.jpa.BaseDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

class PreparationProcessDefinitionCRUDIT extends CollectionModuleBaseIT {

  public static final String EXPECTED_NAME = "name";
  public static final String EXPECTED_GROUP = "DINA GROUP";
  public static final String EXPECTED_CREATED_BY = "createdBy";

  @Inject
  private BaseDAO baseDAO;
  private PreparationProcessDefinitionService processService;
  private PreparationProcessDefinition definition;

  @BeforeEach
  void setUp() {
    processService = new PreparationProcessDefinitionService(baseDAO);
    definition = newDefinition();
    processService.create(definition);
  }

  @Test
  void create() {
    Assertions.assertNotNull(definition.getId());
    Assertions.assertNotNull(definition.getCreatedOn());
    Assertions.assertNotNull(definition.getUuid());
  }

  @Test
  void find() {
    PreparationProcessDefinition result = processService.findOne(
      definition.getUuid(),
      PreparationProcessDefinition.class);
    Assertions.assertEquals(EXPECTED_NAME, result.getName());
    Assertions.assertEquals(EXPECTED_GROUP, result.getGroup());
    Assertions.assertEquals(EXPECTED_CREATED_BY, result.getCreatedBy());
  }

  private static PreparationProcessDefinition newDefinition() {
    return PreparationProcessDefinition.builder()
      .name(EXPECTED_NAME)
      .group(EXPECTED_GROUP)
      .createdBy(EXPECTED_CREATED_BY)
      .build();
  }
}