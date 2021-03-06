package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.CollectingEvent.GeographicPlaceNameSource;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail.SourceAdministrativeLevel;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Set;

@Component
public class CollectingEventValidator implements Validator {

  private static final Set<String> OSM_ELEMENTS = Set.of("N", "W", "R");

  static final String VALID_PRIMARY_KEY = "validation.constraint.violation.validPrimaryAssertions";
  static final String VALID_EVENT_DATE_KEY = "validation.constraint.violation.validEventDateTime";
  static final String VALID_GEOGRAPHIC_PLACE_NAME_SOURCE_KEY = "validation.constraint.violation.validGeographicPlaceNameSource";
  static final String VALID_GEOGRAPHIC_PLACE_NAME_SOURCE_DETAIL_KEY = "validation.constraint.violation.validGeoGraphicPlaceNameSourceDetail";
  static final String VALID_SOURCE_ADMINISTRATION_LEVEL_KEY = "validation.constraint.violation.validSourceAdministrativeLevel";

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

  private void validatePrimaryAssertion(Errors errors, List<GeoreferenceAssertionDto> geoReferenceAssertions) {
    if (CollectionUtils.isNotEmpty(geoReferenceAssertions) && countPrimaries(geoReferenceAssertions) != 1) {
      addError(errors,  VALID_PRIMARY_KEY);
    }
  }

  private void validateDateTimes(Errors errors, CollectingEvent collectingEvent) {
    if (collectingEvent.getStartEventDateTime() == null && collectingEvent.getEndEventDateTime() != null ||
        collectingEvent.getEndEventDateTime() != null && collectingEvent.getStartEventDateTime()
            .isAfter(collectingEvent.getEndEventDateTime())) {
      addError(errors, VALID_EVENT_DATE_KEY);
    }
  }

  private void validateGeographicPlaceNameSourceDetail(Errors errors, CollectingEvent collectingEvent) {
    GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail = collectingEvent.getGeographicPlaceNameSourceDetail();
    if (geographicPlaceNameSourceDetail != null) {
      if (collectingEvent.getGeographicPlaceNameSource() == null ||
          !collectingEvent.getGeographicPlaceNameSource().equals(GeographicPlaceNameSource.OSM)) {
        addError(errors, VALID_GEOGRAPHIC_PLACE_NAME_SOURCE_KEY);
      }

      if (geographicPlaceNameSourceDetail.getCustomGeographicPlace() != null
          && geographicPlaceNameSourceDetail.getSelectedGeographicPlace() != null) {
        addError(errors, VALID_GEOGRAPHIC_PLACE_NAME_SOURCE_DETAIL_KEY);
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
    if (!OSM_ELEMENTS.contains(sal.getElement())) {
      addError(errors, VALID_SOURCE_ADMINISTRATION_LEVEL_KEY);
    }
  }

  private static long countPrimaries(List<GeoreferenceAssertionDto> geoReferenceAssertions) {
    if (CollectionUtils.isEmpty(geoReferenceAssertions)) {
      return 0;
    }
    return geoReferenceAssertions.stream().filter(g -> g.getIsPrimary() != null && g.getIsPrimary()).count();
  }

  /**
   * Internal method to add an error to the provided Errors object with a message from the
   * message bundle.
   * @param errors
   * @param messageBundleKey
   */
  private void addError(Errors errors, String messageBundleKey) {
    String errorMessage = messageSource.getMessage(
        messageBundleKey,
        null,
        LocaleContextHolder.getLocale());
    errors.reject(messageBundleKey, errorMessage);
  }
}
