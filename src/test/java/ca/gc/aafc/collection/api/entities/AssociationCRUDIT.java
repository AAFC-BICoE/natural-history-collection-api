package ca.gc.aafc.collection.api.entities;

import jakarta.validation.ValidationException;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssociationCRUDIT extends CollectionModuleBaseIT {

  @Test
  void testAssociationRelation() {
    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample().build();
    materialSampleService.create(materialSample);
    MaterialSample associated = MaterialSampleFactory.newMaterialSample().build();
    materialSampleService.create(associated);

    Association association = Association.builder()
      .createdBy("test user")
      .sample(materialSample)
      .associatedSample(associated)
      .associationType("parasite_of")
      .build();

    associationService.create(association);
    Association result = associationService.findOne(association.getUuid(), Association.class);

    assertEquals(
      materialSample.getUuid(),
      result.getSample().getUuid());
    assertEquals(
      associated.getUuid(),
      result.getAssociatedSample().getUuid());
  }

    @Test
  void updateMaterialSample_WhenAssociatedWithSelf_Exception() {

      MaterialSample materialSample = MaterialSampleFactory.newMaterialSample().build();
      materialSampleService.create(materialSample);

      Association association = Association.builder()
        .createdBy("test user")
        .sample(materialSample)
        .associatedSample(materialSample)
        .associationType("parasite_of")
        .build();

    assertThrows(ValidationException.class, () -> associationService.update(association));
  }
}
