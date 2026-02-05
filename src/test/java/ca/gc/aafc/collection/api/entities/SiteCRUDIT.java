package ca.gc.aafc.collection.api.entities;

import java.time.LocalDate;
import javax.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.MultilingualDescriptionFactory;
import ca.gc.aafc.collection.api.testsupport.factories.SiteFactory;
import ca.gc.aafc.dina.i18n.MultilingualDescription;

public class SiteCRUDIT extends CollectionModuleBaseIT {

  private static final String EXPECTED_NAME = "name";
  private static final String EXPECTED_GROUP = "dina-group";
  private static final String EXPECTED_CREATED_BY = "createdBy";
  private static final LocalDate EXPECTED_START_DATE = LocalDate.of(1991, 01, 01);
  private static final LocalDate EXPECTED_END_DATE = LocalDate.now();
  private static final String EXPECTED_CODE = "LTAE-M";
  private static final MultilingualDescription MULTILINGUAL_DESCRIPTION = MultilingualDescriptionFactory
      .newMultilingualDescription();

  @Test
  void create() {
    Site site = buildExpectedSite();

    siteService.create(site);

    Assertions.assertNotNull(site.getId());
    Assertions.assertNotNull(site.getCreatedOn());
    Assertions.assertNotNull(site.getUuid());
  }

  @Test
  void find() {
    Site site = buildExpectedSite();

    siteService.create(site);

    Site result = siteService.findOne(
        site.getUuid(),
        Site.class);
    Assertions.assertEquals(EXPECTED_NAME, result.getName());
    Assertions.assertEquals(EXPECTED_GROUP, result.getGroup());
    Assertions.assertEquals(EXPECTED_CREATED_BY, result.getCreatedBy());
    Assertions.assertEquals(EXPECTED_START_DATE, result.getStartDate());
    Assertions.assertEquals(EXPECTED_END_DATE, result.getEndDate());
    Assertions.assertEquals(EXPECTED_CODE, result.getCode());
    Assertions.assertEquals(MULTILINGUAL_DESCRIPTION.getDescriptions(),
        result.getMultilingualDescription().getDescriptions());
  }

  @Test
  public void nullStartTimeNonNullEndTime_throwsValidationException() {
    Site site = buildExpectedSite();
    site.setStartDate(null);
    site.setEndDate(LocalDate.now());

    ValidationException exception = Assertions.assertThrows(
        ValidationException.class,
        () -> siteService.create(site));

    String expectedMessage = "The start and end dates do not create a valid timeline";
    String actualMessage = exception.getMessage();

    Assertions.assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void startTimeAfterEndTime_throwsValidationException() {
    Site site = buildExpectedSite();
    site.setStartDate(LocalDate.of(20201, 01, 01));
    site.setEndDate(LocalDate.of(2020, 01, 01));
    ValidationException exception = Assertions.assertThrows(
        ValidationException.class,
        () -> siteService.create(site));

    String expectedMessage = "The start and end dates do not create a valid timeline";
    String actualMessage = exception.getMessage();

    Assertions.assertTrue(actualMessage.contains(expectedMessage));
  }

  private Site buildExpectedSite() {
    return SiteFactory.newSite()
        .name(EXPECTED_NAME)
        .group(EXPECTED_GROUP)
        .createdBy(EXPECTED_CREATED_BY)
        .multilingualDescription(MULTILINGUAL_DESCRIPTION)
        .startDate(EXPECTED_START_DATE)
        .endDate(EXPECTED_END_DATE)
        .code(EXPECTED_CODE)
        .build();
  }

}
