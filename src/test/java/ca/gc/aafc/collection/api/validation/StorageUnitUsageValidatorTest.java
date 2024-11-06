package ca.gc.aafc.collection.api.validation;

import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.entities.StorageUnitUsage;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitFactory;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitTypeFactory;
import ca.gc.aafc.dina.entity.StorageGridLayout;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

public class StorageUnitUsageValidatorTest extends CollectionModuleBaseIT {

  @Inject
  private StorageUnitUsageValidator storageUnitUsageValidator;

  private static final StorageGridLayout TEST_GRID_LAYOUT = StorageGridLayout.builder()
    .numberOfColumns(8)
    .numberOfRows(8)
    .fillDirection(StorageGridLayout.FillDirection.BY_ROW)
    .build();

  @Test
  void validate_WhenValidUnit_NoErrors() {
    StorageUnitUsage coordinates = new StorageUnitUsage();
    Errors errors = ValidationErrorsHelper.newErrorsObject(coordinates);

    StorageUnitType sut = StorageUnitTypeFactory.newStorageUnitType()
      .gridLayoutDefinition(TEST_GRID_LAYOUT)
      .build();
    StorageUnit su = StorageUnitFactory.newStorageUnit()
      .storageUnitType(sut).build();

    coordinates.setWellRow("A");
    coordinates.setWellColumn(1);
    coordinates.setStorageUnit(su);

    storageUnitUsageValidator.validate(coordinates, errors);
    assertFalse(errors.hasErrors());
  }

  @Test
  void validate_WhenValidType_NoErrors() {
    StorageUnitUsage coordinates = new StorageUnitUsage();
    Errors errors = ValidationErrorsHelper.newErrorsObject(coordinates);

    StorageUnitType sut = StorageUnitTypeFactory.newStorageUnitType()
      .gridLayoutDefinition(TEST_GRID_LAYOUT)
      .build();

    coordinates.setWellRow("A");
    coordinates.setWellColumn(1);
    coordinates.setStorageUnitType(sut);

    storageUnitUsageValidator.validate(coordinates, errors);
    assertFalse(errors.hasErrors());
  }

  @Test
  void validate_onInvalidLocation_error() {
    StorageUnitUsage coordinates = new StorageUnitUsage();
    Errors errors = ValidationErrorsHelper.newErrorsObject(coordinates);

    StorageUnitType sut = StorageUnitTypeFactory.newStorageUnitType()
      .gridLayoutDefinition(TEST_GRID_LAYOUT)
      .build();
    StorageUnit su = StorageUnitFactory.newStorageUnit()
        .storageUnitType(sut).build();

    coordinates.setWellRow("Z");
    coordinates.setWellColumn(1);
    coordinates.setStorageUnit(su);

    storageUnitUsageValidator.validate(coordinates, errors);
    assertTrue(errors.hasErrors());

   assertTrue(errors.getAllErrors().get(0).getDefaultMessage().contains("Invalid well row"));
  }

  @Test
  void validate_onNoStorage_error() {
    StorageUnitUsage coordinates = new StorageUnitUsage();
    Errors errors = ValidationErrorsHelper.newErrorsObject(coordinates);

    coordinates.setWellRow("Z");
    coordinates.setWellColumn(1);

    storageUnitUsageValidator.validate(coordinates, errors);
    assertTrue(errors.hasErrors());
    assertTrue(errors.getAllErrors().get(0).getDefaultMessage().contains("Storage Unit or Storage Unit Type must be provided but not both"));
  }

  @Test
  void validate_onStorageAndType_error() {
    StorageUnitUsage coordinates = new StorageUnitUsage();
    Errors errors = ValidationErrorsHelper.newErrorsObject(coordinates);

    StorageUnitType sut = StorageUnitTypeFactory.newStorageUnitType()
      .gridLayoutDefinition(TEST_GRID_LAYOUT)
      .build();
    StorageUnit su = StorageUnitFactory.newStorageUnit()
      .storageUnitType(sut).build();

    coordinates.setWellRow("Z");
    coordinates.setWellColumn(1);
    coordinates.setStorageUnit(su);
    coordinates.setStorageUnitType(sut);

    storageUnitUsageValidator.validate(coordinates, errors);
    assertTrue(errors.hasErrors());
    assertTrue(errors.getAllErrors().get(0).getDefaultMessage().contains("Storage Unit or Storage Unit Type must be provided but not both"));
  }


}

