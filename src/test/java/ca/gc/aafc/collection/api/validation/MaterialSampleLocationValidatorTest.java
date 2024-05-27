package ca.gc.aafc.collection.api.validation;

import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
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
    MaterialSample sample = MaterialSampleFactory.newMaterialSample().build();
    Errors errors = ValidationErrorsHelper.newErrorsObject(sample);

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

    sample.setWellRow("A");
    sample.setWellColumn(1);
    sample.setStorageUnit(su);

    sampleLocationValidator.validate(sample, errors);
    assertFalse(errors.hasErrors());
  }

  @Test
  void validate_onInvalidLocation_error() {
    MaterialSample sample = MaterialSampleFactory.newMaterialSample().build();
    Errors errors = ValidationErrorsHelper.newErrorsObject(sample);

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

    sample.setWellRow("Z");
    sample.setWellColumn(1);
    sample.setStorageUnit(su);

    sampleLocationValidator.validate(sample, errors);
    assertTrue(errors.hasErrors());

   assertTrue(errors.getAllErrors().get(0).getDefaultMessage().contains("Invalid well row"));
  }

  @Test
  void validate_onNoStorage_error() {
    MaterialSample sample = MaterialSampleFactory.newMaterialSample().build();
    Errors errors = ValidationErrorsHelper.newErrorsObject(sample);

    sample.setWellRow("Z");
    sample.setWellColumn(1);

    sampleLocationValidator.validate(sample, errors);
    assertTrue(errors.hasErrors());
    assertTrue(errors.getAllErrors().get(0).getDefaultMessage().contains("StorageUnit must be set"));
  }


}

