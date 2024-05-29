package ca.gc.aafc.collection.api.validation;

import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.entities.StorageUnitCoordinates;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitFactory;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitTypeFactory;
import ca.gc.aafc.dina.entity.StorageGridLayout;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

public class MaterialSampleLocationValidatorTest extends CollectionModuleBaseIT {

  @Inject
  private MaterialSampleLocationValidator sampleLocationValidator;

  @Test
  void validate_WhenValid_NoErrors() {
    StorageUnitCoordinates coordinates = new StorageUnitCoordinates();
    Errors errors = ValidationErrorsHelper.newErrorsObject(coordinates);

    StorageGridLayout storageGridLayout = StorageGridLayout.builder()
      .numberOfColumns(8)
      .numberOfRows(8)
      .fillDirection(StorageGridLayout.FillDirection.BY_ROW)
      .build();

    StorageUnitType sut = StorageUnitTypeFactory.newStorageUnitType()
      .gridLayoutDefinition(storageGridLayout)
      .build();
    StorageUnit su = StorageUnitFactory.newStorageUnit()
      .storageUnitType(sut).build();

    coordinates.setWellRow("A");
    coordinates.setWellColumn(1);
    coordinates.setStorageUnit(su);

    sampleLocationValidator.validate(coordinates, errors);
    assertFalse(errors.hasErrors());
  }

  @Test
  void validate_onInvalidLocation_error() {
    StorageUnitCoordinates coordinates = new StorageUnitCoordinates();
    Errors errors = ValidationErrorsHelper.newErrorsObject(coordinates);

    StorageGridLayout storageGridLayout = StorageGridLayout.builder()
      .numberOfColumns(8)
      .numberOfRows(8)
      .fillDirection(StorageGridLayout.FillDirection.BY_ROW)
      .build();

    StorageUnitType sut = StorageUnitTypeFactory.newStorageUnitType()
      .gridLayoutDefinition(storageGridLayout)
      .build();
    StorageUnit su = StorageUnitFactory.newStorageUnit()
        .storageUnitType(sut).build();

    coordinates.setWellRow("Z");
    coordinates.setWellColumn(1);
    coordinates.setStorageUnit(su);

    sampleLocationValidator.validate(coordinates, errors);
    assertTrue(errors.hasErrors());

   assertTrue(errors.getAllErrors().get(0).getDefaultMessage().contains("Invalid well row"));
  }

  @Test
  void validate_onNoStorage_error() {
    StorageUnitCoordinates coordinates = new StorageUnitCoordinates();
    Errors errors = ValidationErrorsHelper.newErrorsObject(coordinates);

    coordinates.setWellRow("Z");
    coordinates.setWellColumn(1);

    sampleLocationValidator.validate(coordinates, errors);
    assertTrue(errors.hasErrors());
    assertTrue(errors.getAllErrors().get(0).getDefaultMessage().contains("StorageUnit must be set"));
  }


}

