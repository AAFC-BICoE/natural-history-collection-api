package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class AssociationCRUDIT extends CollectionModuleBaseIT {

  @Test
  void testAssociationRelation() {
    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample().build();
    materialSampleService.create(materialSample);
    MaterialSample associated = MaterialSampleFactory.newMaterialSample().build();
    materialSampleService.create(associated);

    Association association = Association.builder()
      .sample(materialSample)
      .associatedSample(associated)
      .associationType("type 1")
      .build();

    materialSample.setAssociations(List.of(association));

    MaterialSample result = materialSampleService.findOne(materialSample.getUuid(), MaterialSample.class);
    Assertions.assertEquals(materialSample.getUuid(), result.getAssociations().get(0).getSample().getUuid());
    Assertions.assertEquals(
      associated.getUuid(),
      result.getAssociations().get(0).getAssociatedSample().getUuid());
    Assertions.assertEquals(
      association.getAssociationType(),
      result.getAssociations().get(0).getAssociationType());
  }

}