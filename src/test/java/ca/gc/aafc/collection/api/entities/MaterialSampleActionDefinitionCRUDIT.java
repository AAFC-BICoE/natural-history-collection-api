package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.FormTemplate;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.MaterialSampleFormComponent;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.TemplateField;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MaterialSampleActionDefinitionCRUDIT extends CollectionModuleBaseIT {

  public static final String EXPECTED_NAME = "name";
  public static final String EXPECTED_GROUP = "DINA GROUP";
  public static final String EXPECTED_CREATED_BY = "createdBy";
  public static final MaterialSampleActionDefinition.ActionType ACTION_TYPE = MaterialSampleActionDefinition.ActionType.ADD;

  private MaterialSampleActionDefinition definition;

  @BeforeEach
  void setUp() {
    definition = newDefinition();
    materialSampleActionDefinitionService.create(definition);
  }

  @Test
  void create() {
    Assertions.assertNotNull(definition.getId());
    Assertions.assertNotNull(definition.getCreatedOn());
    Assertions.assertNotNull(definition.getUuid());
  }

  @Test
  void find() {
    MaterialSampleActionDefinition result = materialSampleActionDefinitionService.findOne(
      definition.getUuid(),
      MaterialSampleActionDefinition.class);
    Assertions.assertEquals(EXPECTED_NAME, result.getName());
    Assertions.assertEquals(EXPECTED_GROUP, result.getGroup());
    Assertions.assertEquals(EXPECTED_CREATED_BY, result.getCreatedBy());
    Assertions.assertEquals(ACTION_TYPE, result.getActionType());
  }

  private static MaterialSampleActionDefinition newDefinition() {
    return MaterialSampleActionDefinition.builder()
      .name(EXPECTED_NAME)
      .group(EXPECTED_GROUP)
      .createdBy(EXPECTED_CREATED_BY)
      .actionType(ACTION_TYPE)
      .formTemplates(new HashMap<>(Map.of(MaterialSampleFormComponent.MATERIAL_SAMPLE, FormTemplate.builder()
        .allowNew(true)
        .allowExisting(true)
        .templateFields(new HashMap<>(Map.of("materialSampleName", TemplateField.builder()
          .enabled(true)  
          .defaultValue("test-default-value")
          .build())))
        .build())))
      .build();
  }
}