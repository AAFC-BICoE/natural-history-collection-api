package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.InstitutionService;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionMethodTestFixture;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

class InstitutionCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private InstitutionService institutionService;

  @Test
  void find() {
    MultilingualDescription description = CollectionMethodTestFixture.newMulti();
    Institution institution = institutionService.create(Institution.builder()
      .name(RandomStringUtils.randomAlphabetic(3))
      .group(RandomStringUtils.randomAlphabetic(3))
      .createdBy(RandomStringUtils.randomAlphabetic(3))
      .multilingualDescription(description)
      .build());

    Institution result = institutionService.findOne(institution.getUuid(), Institution.class);
    Assertions.assertNotNull(result.getCreatedOn());
    Assertions.assertEquals(institution.getName(), result.getName());
    Assertions.assertEquals(institution.getGroup(), result.getGroup());
    Assertions.assertEquals(institution.getCreatedBy(), result.getCreatedBy());
    Assertions.assertEquals(
      description.getDescriptions().get(0).getLang(),
      result.getMultilingualDescription().getDescriptions().get(0).getLang());
  }
}
