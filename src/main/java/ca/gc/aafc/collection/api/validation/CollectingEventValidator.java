package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.collection.api.entities.CollectingEvent.GeographicPlaceNameSource;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail.SourceAdministrativeLevel;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class CollectingEventValidator implements Validator {

  public static final String VALID_PRIMARY_KEY = "validation.constraint.violation.validPrimaryAssertions";
  public static final String VALID_EVENT_DATE_KEY = "validation.constraint.violation.validEventDateTime";
  public static final String VALID_GEOGRAPHIC_PLACE_NAME_SOURCE = "validation.constraint.violation.validGeographicPlaceNameSource";
  public static final String VALID_GEOGRAPHIC_PLACE_NAME_SOURCE_DETAIL = "validation.constraint.violation.validGeoGraphicPlaceNameSourceDetail";
  public static final String VALID_SOURCE_ADMINISTRATION_LEVEL = "validation.constraint.violation.validSourceAdministrativeLevel";



  private final MessageSource messageSource;

  public CollectingEventValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return CollectingEvent.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("CollectingEventValidator not supported for class " + target.getClass());
    }
    CollectingEvent collectingEvent = (CollectingEvent) target;
    validateDateTimes(errors, collectingEvent);
    validatePrimaryAssertion(errors, collectingEvent.getGeoReferenceAssertions());
    validateGeographicPlaceNameSourceDetail(errors, collectingEvent);
  }

  private void validatePrimaryAssertion(Errors errors, List<GeoreferenceAssertion> geoReferenceAssertions) {
    if (CollectionUtils.isNotEmpty(geoReferenceAssertions) && countPrimaries(geoReferenceAssertions) != 1) {
      String errorMessage = messageSource.getMessage(
        VALID_PRIMARY_KEY,
        null,
        LocaleContextHolder.getLocale());
      errors.reject(VALID_PRIMARY_KEY, errorMessage);
    }
  }

  private void validateDateTimes(Errors errors, CollectingEvent collectingEvent) {
    if ((collectingEvent.getStartEventDateTime() == null && collectingEvent.getEndEventDateTime() != null)
      || (collectingEvent.getEndEventDateTime() != null
      && collectingEvent.getStartEventDateTime().isAfter(collectingEvent.getEndEventDateTime()))) {
      String errorMessage = messageSource.getMessage(
        VALID_EVENT_DATE_KEY,
        null,
        LocaleContextHolder.getLocale());
      errors.reject(VALID_EVENT_DATE_KEY, errorMessage);
    }
  }

  private void validateGeographicPlaceNameSourceDetail(Errors errors, CollectingEvent collectingEvent) {
    GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail = collectingEvent.getGeographicPlaceNameSourceDetail();
    if (geographicPlaceNameSourceDetail != null) {
      if (!collectingEvent.getGeographicPlaceNameSource().equals(GeographicPlaceNameSource.OSM)) {
        String errorMessage = messageSource.getMessage(
          VALID_GEOGRAPHIC_PLACE_NAME_SOURCE,
          null,
          LocaleContextHolder.getLocale());
        errors.reject(VALID_EVENT_DATE_KEY, errorMessage);
      }
      if (geographicPlaceNameSourceDetail.getCustomGeographicPlace() != null
        && geographicPlaceNameSourceDetail.getSelectedGeographicPlace() != null 
        || (geographicPlaceNameSourceDetail.getCustomGeographicPlace() == null 
        || geographicPlaceNameSourceDetail.getSelectedGeographicPlace() == null)) {
        String errorMessage = messageSource.getMessage(
          VALID_GEOGRAPHIC_PLACE_NAME_SOURCE_DETAIL,
          null,
          LocaleContextHolder.getLocale());
        errors.reject(VALID_EVENT_DATE_KEY, errorMessage);
        } 
      if (geographicPlaceNameSourceDetail.getSelectedGeographicPlace() != null) {
        validateSourceAdministrativeLevel(errors, geographicPlaceNameSourceDetail.getSelectedGeographicPlace());
      }
      if (CollectionUtils.isNotEmpty(geographicPlaceNameSourceDetail.getHigherGeographicPlaces())) {
        for (SourceAdministrativeLevel sal : geographicPlaceNameSourceDetail.getHigherGeographicPlaces()) {
          validateSourceAdministrativeLevel(errors, sal);
        }
      }
    }
  }

  private void validateSourceAdministrativeLevel(Errors errors, SourceAdministrativeLevel sal) {
    switch(sal.getElement()) {
      case "N":
      case "W":
      case "R":
        break;
      default:
        String errorMessage = messageSource.getMessage(
          VALID_SOURCE_ADMINISTRATION_LEVEL,
          null,
          LocaleContextHolder.getLocale());
        errors.reject(VALID_EVENT_DATE_KEY, errorMessage);
        break;
    }
  }

  private static long countPrimaries(List<GeoreferenceAssertion> geoReferenceAssertions) {
    if (CollectionUtils.isEmpty(geoReferenceAssertions)) {
      return 0;
    }
    return geoReferenceAssertions.stream().filter(g -> g.getIsPrimary() != null && g.getIsPrimary()).count();
  }
}
