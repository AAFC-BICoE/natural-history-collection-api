package ca.gc.aafc.collection.api.entities;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.MultilingualDescriptionFactory;
import ca.gc.aafc.collection.api.testsupport.factories.SiteFactory;
import ca.gc.aafc.dina.i18n.MultilingualDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SiteCRUDIT extends CollectionModuleBaseIT {

  private static final String EXPECTED_NAME = "name";
  private static final String EXPECTED_GROUP = "dina-group";
  private static final String EXPECTED_CREATED_BY = "createdBy";
  private static final String EXPECTED_CODE = "LTAE-M";
  private static final MultilingualDescription MULTILINGUAL_DESCRIPTION = MultilingualDescriptionFactory
      .newMultilingualDescription();
  private final List<UUID> EXPECT_ATTACHMENTS = List.of(UUID.randomUUID(), UUID.randomUUID());

  @Test
  void create() {
    Site site = buildExpectedSite();

    siteService.create(site);

    assertNotNull(site.getId());
    assertNotNull(site.getCreatedOn());
    assertNotNull(site.getUuid());
  }

  @Test
  void find() {
    Site site = buildExpectedSite();

    siteService.create(site);

    Site result = siteService.findOne(
        site.getUuid(),
        Site.class);
    assertEquals(EXPECTED_NAME, result.getName());
    assertEquals(EXPECTED_GROUP, result.getGroup());
    assertEquals(EXPECTED_CREATED_BY, result.getCreatedBy());
    assertEquals(EXPECTED_CODE, result.getCode());
    assertEquals(MULTILINGUAL_DESCRIPTION.getDescriptions(),
        result.getMultilingualDescription().getDescriptions());
    assertEquals(EXPECT_ATTACHMENTS, result.getAttachment());
  }

  private Site buildExpectedSite() {
    return SiteFactory.newSite()
        .name(EXPECTED_NAME)
        .group(EXPECTED_GROUP)
        .createdBy(EXPECTED_CREATED_BY)
        .multilingualDescription(MULTILINGUAL_DESCRIPTION)
        .code(EXPECTED_CODE)
        .attachment(EXPECT_ATTACHMENTS)
        .build();
  }
}
