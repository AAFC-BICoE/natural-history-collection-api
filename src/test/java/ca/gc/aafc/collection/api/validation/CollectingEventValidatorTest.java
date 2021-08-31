package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.CollectingEvent.GeographicPlaceNameSource;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail.Country;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail.SourceAdministrativeLevel;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

class CollectingEventValidatorTest extends CollectionModuleBaseIT {

  @Inject
  private CollectingEventValidator validator;

  @Inject
  private MessageSource messageSource;

  @Test
  void validate_WhenNoAssertions_ValidationSuccess() {
    CollectingEvent event = newEvent();

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(0, errors.getAllErrors().size());
  }

  @Test
  void validate_WhenOneAssertionIsPrimary_ValidationSuccess() {
    GeoreferenceAssertionDto assertion = newAssertion();
    assertion.setIsPrimary(true);
    GeoreferenceAssertionDto assertion2 = newAssertion();
    assertion2.setIsPrimary(false);

    CollectingEvent event = newEvent();
    event.setGeoReferenceAssertions(List.of(assertion, assertion2));

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(0, errors.getAllErrors().size());
  }

  @Test
  void validate_AssertionSizeIsOneAndPrimary_ValidationSuccess() {
    GeoreferenceAssertionDto assertion = newAssertion();
    assertion.setIsPrimary(true);

    CollectingEvent event = newEvent();
    event.setGeoReferenceAssertions(List.of(assertion));

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(0, errors.getAllErrors().size());
  }

  @Test
  void validate_AssertionSizeIsOneAndNotPrimary_ErrorsReturned() {
    String expectedErrorMessage = getExpectedErrorMessage(CollectingEventValidator.VALID_PRIMARY_KEY);

    GeoreferenceAssertionDto assertion = newAssertion();
    assertion.setIsPrimary(false);

    CollectingEvent event = newEvent();
    event.setGeoReferenceAssertions(List.of(assertion));

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_MoreThenOnePrimaryAssertion_ErrorsReturned() {
    String expectedErrorMessage = getExpectedErrorMessage(CollectingEventValidator.VALID_PRIMARY_KEY);

    GeoreferenceAssertionDto assertion = newAssertion();
    assertion.setIsPrimary(true);

    CollectingEvent event = newEvent();
    event.setGeoReferenceAssertions(List.of(assertion, assertion));

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_NoAssertionsArePrimary_ErrorsReturned() {
    String expectedErrorMessage = getExpectedErrorMessage(CollectingEventValidator.VALID_PRIMARY_KEY);

    GeoreferenceAssertionDto assertion = newAssertion();
    assertion.setIsPrimary(false);

    CollectingEvent event = newEvent();
    event.setGeoReferenceAssertions(List.of(assertion, assertion));

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_GeographicPlaceNameSourceDetailExistsButGeoGraphicPlaceNameSourceNotOSM_ErrorsReturned() {
    String expectedErrorMessage = getExpectedErrorMessage(CollectingEventValidator.VALID_GEOGRAPHIC_PLACE_NAME_SOURCE_KEY);
    
    CollectingEvent event = newEvent();
    event.setGeographicPlaceNameSourceDetail(newGeographicPlaceNameSourceDetail());

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_BothCustomGeographicPlaceAndSelectedGeographicPlaceExist_ErrorsReturned() {
    String expectedErrorMessage = getExpectedErrorMessage(CollectingEventValidator.VALID_GEOGRAPHIC_PLACE_NAME_SOURCE_DETAIL_KEY);

    GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail = newGeographicPlaceNameSourceDetail();
    geographicPlaceNameSourceDetail.setCustomGeographicPlace("custom place");
    geographicPlaceNameSourceDetail.setSelectedGeographicPlace(newSourceAdministrativeLevel());

    CollectingEvent event = newEventWithGeographicPlaceNameSource();
    event.setGeographicPlaceNameSourceDetail(geographicPlaceNameSourceDetail);

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_SourceAdministrativeLevelElementFieldNotNWRSelectedGeographicPlace_ErrorsReturned() {
    String expectedErrorMessage = getExpectedErrorMessage(CollectingEventValidator.VALID_SOURCE_ADMINISTRATION_LEVEL_KEY);

    SourceAdministrativeLevel sourceAdministrativeLevel = newSourceAdministrativeLevel();
    sourceAdministrativeLevel.setElement("not N W or R");

    GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail = newGeographicPlaceNameSourceDetail();
    geographicPlaceNameSourceDetail.setSelectedGeographicPlace(sourceAdministrativeLevel);
    geographicPlaceNameSourceDetail.setHigherGeographicPlaces(Arrays.asList(sourceAdministrativeLevel));
    
    CollectingEvent event = newEventWithGeographicPlaceNameSource();
    event.setGeographicPlaceNameSourceDetail(geographicPlaceNameSourceDetail);

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(2, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(1).getDefaultMessage());
  }

  @Test
  void validate_MaxElevationSetMinElevationNotSet_ErrorsReturned() {
    String expectedErrorMessage = getExpectedErrorMessage(CollectingEventValidator.VALID_MIN_MAX_ELEVATION);

    CollectingEvent event = newEvent();
    event.setDwcMaximumElevationInMeters(100);

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_MaxDepthSetMinDepthNotSet_ErrorsReturned() {
    String expectedErrorMessage = getExpectedErrorMessage(CollectingEventValidator.VALID_MIN_MAX_DEPTH);

    CollectingEvent event = newEvent();
    event.setDwcMaximumDepthInMeters(100);

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_MaxElevationLessThanMinElevation_ErrorsReturned() {
    String expectedErrorMessage = getExpectedErrorMessage(CollectingEventValidator.VALID_MAX_LESS_THAN_MIN_ELEVATION);

    CollectingEvent event = newEvent();
    event.setDwcMaximumElevationInMeters(100);
    event.setDwcMinimumElevationInMeters(101);

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_MaxDepthLessThanMinDepth_ErrorsReturned() {
    String expectedErrorMessage = getExpectedErrorMessage(CollectingEventValidator.VALID_MAX_LESS_THAN_MIN_DEPTH);

    CollectingEvent event = newEvent();
    event.setDwcMaximumDepthInMeters(100);
    event.setDwcMinimumDepthInMeters(101);

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  private String getExpectedErrorMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }

  private static CollectingEvent newEvent() {
    return CollectingEvent.builder()
      .uuid(UUID.randomUUID())
      .startEventDateTime(LocalDateTime.now().minusDays(1))
      .build();
  }

  private static GeoreferenceAssertionDto newAssertion() {
    return GeoreferenceAssertionDto.builder()
      .dwcDecimalLatitude(12.123456)
      .dwcDecimalLongitude(45.01)
      .dwcCoordinateUncertaintyInMeters(10)
      .isPrimary(false)
      .build();
  }

  private static CollectingEvent newEventWithGeographicPlaceNameSource() {
    return CollectingEvent.builder()
      .uuid(UUID.randomUUID())
      .geographicPlaceNameSource(GeographicPlaceNameSource.OSM)
      .startEventDateTime(LocalDateTime.now().minusDays(1))
      .build();
  }

  @SneakyThrows
  private static GeographicPlaceNameSourceDetail newGeographicPlaceNameSourceDetail() {
    URL url = new URL("https://github.com/AAFC-BICoE/natural-history-collection-api");
    return GeographicPlaceNameSourceDetail.builder()
      .sourceUrl(url)
      .stateProvince(newSourceAdministrativeLevel())
      .country(newCountry())
      .build();
  }

  private static SourceAdministrativeLevel newSourceAdministrativeLevel() {
    return SourceAdministrativeLevel.builder()
      .id("F-124")
      .element("N")
      .placeType("city")
      .name("Montreal")
      .build();
    }

  private static Country newCountry() {
    return Country.builder()
      .code("CA")
      .name("Canada")
      .build();
  }
}
