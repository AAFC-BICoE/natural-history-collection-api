package ca.gc.aafc.collection.api.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.validation.ValidationException;

import ca.gc.aafc.collection.api.testsupport.factories.MultilingualDescriptionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.ExpeditionFactory;
import ca.gc.aafc.dina.i18n.MultilingualDescription;

public class ExpeditionCRUDIT extends CollectionModuleBaseIT {

  private static final String EXPECTED_NAME = "name";
  private static final String EXPECTED_GROUP = "dina-group";
  private static final String EXPECTED_CREATED_BY = "createdBy";
  private static final String EXPECTED_GEOGRAPHIC_CONTEXT= "Expedition region";
  private static final LocalDate EXPECTED_START_DATE = LocalDate.of(1991, 01, 01);
  private static final LocalDate EXPECTED_END_DATE = LocalDate.now();
  private final List<UUID> EXPECTED_PARTICIPANTS = List.of(UUID.randomUUID(), UUID.randomUUID());

  private static final MultilingualDescription MULTILINGUAL_DESCRIPTION =
          MultilingualDescriptionFactory.newMultilingualDescription();

  @Test
  void create() {
    Expedition expedition = buildExpectedExpedition();

    expeditionService.create(expedition);

    Assertions.assertNotNull(expedition.getId());
    Assertions.assertNotNull(expedition.getCreatedOn());
    Assertions.assertNotNull(expedition.getUuid());
  }

  @Test
  void find() {
    Expedition expedition = buildExpectedExpedition();

    expeditionService.create(expedition);

    Expedition result = expeditionService.findOne(
      expedition.getUuid(),
      Expedition.class);
    Assertions.assertEquals(EXPECTED_NAME, result.getName());
    Assertions.assertEquals(EXPECTED_GROUP, result.getGroup());
    Assertions.assertEquals(EXPECTED_CREATED_BY, result.getCreatedBy());
    Assertions.assertEquals(EXPECTED_GEOGRAPHIC_CONTEXT, result.getGeographicContext());
    Assertions.assertEquals(EXPECTED_START_DATE, result.getStartDate());
    Assertions.assertEquals(EXPECTED_END_DATE, result.getEndDate());
    Assertions.assertEquals(EXPECTED_PARTICIPANTS, result.getParticipants());
    Assertions.assertEquals(MULTILINGUAL_DESCRIPTION.getDescriptions(), result.getMultilingualDescription().getDescriptions());
  }

  @Test
  public void nullStartTimeNonNullEndTime_throwsValidationException() {
    Expedition expedition = buildExpectedExpedition();
    expedition.setStartDate(null);
    expedition.setEndDate(LocalDate.now());

    ValidationException exception = Assertions.assertThrows(
      ValidationException.class,
      () -> expeditionService.create(expedition));

    String expectedMessage = "The start and end dates do not create a valid timeline";
    String actualMessage = exception.getMessage();

    Assertions.assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void startTimeAfterEndTime_throwsValidationException() {
    Expedition expedition = buildExpectedExpedition();
    expedition.setStartDate(LocalDate.of(20201, 01, 01));
    expedition.setEndDate(LocalDate.of(2020, 01, 01));
    ValidationException exception = Assertions.assertThrows(
      ValidationException.class,
      () -> expeditionService.create(expedition));

    String expectedMessage = "The start and end dates do not create a valid timeline";
    String actualMessage = exception.getMessage();

    Assertions.assertTrue(actualMessage.contains(expectedMessage));
  }

  private Expedition buildExpectedExpedition() {
    return ExpeditionFactory.newExpedition()
      .name(EXPECTED_NAME)
      .group(EXPECTED_GROUP)
      .createdBy(EXPECTED_CREATED_BY)
      .multilingualDescription(MULTILINGUAL_DESCRIPTION)
      .geographicContext(EXPECTED_GEOGRAPHIC_CONTEXT)
      .startDate(EXPECTED_START_DATE)
      .endDate(EXPECTED_END_DATE)
      .participants(EXPECTED_PARTICIPANTS)
      .build();
  }

}
