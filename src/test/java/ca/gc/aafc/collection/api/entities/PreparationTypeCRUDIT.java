package ca.gc.aafc.collection.api.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;

public class PreparationTypeCRUDIT extends CollectionModuleBaseIT {

  public static final String EXPECTED_NAME = "name";
  public static final String EXPECTED_GROUP = "DINA GROUP";
  public static final String EXPECTED_CREATED_BY = "createdBy";

  private PreparationType preparationType;

  @BeforeEach
  void setup() {
    preparationType = newPreparationType();
    preparationTypeService.create(preparationType);
  }

  @Test
  void create() {
    Assertions.assertNotNull(preparationType.getId());
    Assertions.assertNotNull(preparationType.getCreatedOn());
    Assertions.assertNotNull(preparationType.getUuid());
  }

  @Test
  void find() {
    PreparationType result = preparationTypeService.findOne(
      preparationType.getUuid(),
      PreparationType.class);
    Assertions.assertEquals(EXPECTED_NAME, result.getName());
    Assertions.assertEquals(EXPECTED_GROUP, result.getGroup());
    Assertions.assertEquals(EXPECTED_CREATED_BY, result.getCreatedBy());
  }

  private static PreparationType newPreparationType() {
    return PreparationType.builder()
      .name(EXPECTED_NAME)
      .group(EXPECTED_GROUP)
      .createdBy(EXPECTED_CREATED_BY)
      .build();
  }
}
