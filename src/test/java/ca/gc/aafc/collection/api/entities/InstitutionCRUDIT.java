package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.InstitutionService;
import ca.gc.aafc.collection.api.testsupport.fixtures.InstitutionFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

class InstitutionCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private InstitutionService institutionService;

  @Test
  void find() {
    Institution institution = institutionService.create(InstitutionFixture.newInstitutionEntity().build());
    Institution result = institutionService.findOne(institution.getUuid(), Institution.class);
    Assertions.assertNotNull(result.getCreatedOn());
    Assertions.assertEquals(institution.getName(), result.getName());
    Assertions.assertEquals(institution.getGroup(), result.getGroup());
    Assertions.assertEquals(institution.getCreatedBy(), result.getCreatedBy());
    Assertions.assertEquals(
      institution.getMultilingualDescription().getDescriptions().get(0).getLang(),
      result.getMultilingualDescription().getDescriptions().get(0).getLang());
  }

  @Test
  void newline_address() {
    Institution institution = institutionService.create(InstitutionFixture.newInstitutionEntity()
      .address("line1\nline2")
      .build());

    Assertions.assertTrue(
      institution.getAddress().contains(System.getProperty("line.separator")));

  }

}
