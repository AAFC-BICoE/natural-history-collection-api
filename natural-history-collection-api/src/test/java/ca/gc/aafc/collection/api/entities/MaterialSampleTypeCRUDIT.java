package ca.gc.aafc.collection.api.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;

public class MaterialSampleTypeCRUDIT extends CollectionModuleBaseIT {

  public static final String EXPECTED_NAME = "name";
  public static final String EXPECTED_GROUP = "DINA GROUP";
  public static final String EXPECTED_CREATED_BY = "createdBy";

  private MaterialSampleType materialSampleType;

  @BeforeEach
  void setup() {
    materialSampleType = newMaterialSampleType();
    materialSampleTypeService.create(materialSampleType);
  }

  @Test
  void create() {
    Assertions.assertNotNull(materialSampleType.getId());
    Assertions.assertNotNull(materialSampleType.getCreatedOn());
    Assertions.assertNotNull(materialSampleType.getUuid());
  }

  @Test
  void find() {
    MaterialSampleType result = materialSampleTypeService.findOne(
      materialSampleType.getUuid(),
      MaterialSampleType.class);
    Assertions.assertEquals(EXPECTED_NAME, result.getName());
    Assertions.assertEquals(EXPECTED_GROUP, result.getGroup());
    Assertions.assertEquals(EXPECTED_CREATED_BY, result.getCreatedBy());
  }

  private static MaterialSampleType newMaterialSampleType() {
    return MaterialSampleType.builder()
      .name(EXPECTED_NAME)
      .group(EXPECTED_GROUP)
      .createdBy(EXPECTED_CREATED_BY)
      .build();
  }
}
