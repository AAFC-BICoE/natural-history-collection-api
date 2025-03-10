package ca.gc.aafc.collection.api.validation;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.CollectingEvent.GeographicPlaceNameSource;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail.SourceAdministrativeLevel;
import ca.gc.aafc.dina.validation.DinaBaseValidator;

import java.util.List;
import java.util.Set;

@Component
public class CollectingEventValidator extends DinaBaseValidator<CollectingEvent> {

  private static final Set<String> OSM_ELEMENTS = Set.of("N", "W", "R");

  static final String VALID_PRIMARY_KEY = "validation.constraint.violation.validPrimaryAssertions";
  static final String VALID_EVENT_DATE_KEY = "validation.constraint.violation.validEventDateTime";
  static final String VALID_GEOGRAPHIC_PLACE_NAME_SOURCE_KEY = "validation.constraint.violation.validGeographicPlaceNameSource";
  static final String VALID_GEOGRAPHIC_PLACE_NAME_SOURCE_DETAIL_KEY = "validation.constraint.violation.validGeoGraphicPlaceNameSourceDetail";
  static final String VALID_SOURCE_ADMINISTRATION_LEVEL_KEY = "validation.constraint.violation.validSourceAdministrativeLevel";
  static final String VALID_MIN_MAX_ELEVATION = "validation.constraint.violation.validMinMaxElevation";
  static final String VALID_MIN_MAX_DEPTH = "validation.constraint.violation.validMinMaxDepth";
  static final String VALID_MAX_LESS_THAN_MIN_ELEVATION = "validation.constraint.violation.validMaxLessThanMinElevation";
  static final String VALID_MAX_LESS_THAN_MIN_DEPTH = "validation.constraint.violation.validMaxLessThanMinDepth";

  public CollectingEventValidator(MessageSource messageSource) {
    super(CollectingEvent.class, messageSource);
  }

  @Override
  public void validateTarget(CollectingEvent target, Errors errors) {
    validateMinMaxElevationDepth(target, errors);
    validateDateTimes(target, errors);
    validatePrimaryAssertion(target.getGeoReferenceAssertions(), errors);
    validateGeographicPlaceNameSourceDetail(target, errors);
  }

  private void validatePrimaryAssertion(List<GeoreferenceAssertionDto> geoReferenceAssertions, Errors errors) {
    if (CollectionUtils.isNotEmpty(geoReferenceAssertions) && countPrimaries(geoReferenceAssertions) != 1) {
      addError(errors,  VALID_PRIMARY_KEY);
    }
  }

  private void validateMinMaxElevationDepth(CollectingEvent collectingEvent, Errors errors) {
    if (collectingEvent.getDwcMaximumDepthInMeters() != null && collectingEvent.getDwcMinimumDepthInMeters() != null &&
        collectingEvent.getDwcMaximumDepthInMeters().compareTo(collectingEvent.getDwcMinimumDepthInMeters()) < 0) {
      addError(errors, VALID_MAX_LESS_THAN_MIN_DEPTH);
    }
    if (collectingEvent.getDwcMaximumDepthInMeters() != null && collectingEvent.getDwcMinimumDepthInMeters() == null) {
      addError(errors, VALID_MIN_MAX_DEPTH);
    }
    if (collectingEvent.getDwcMaximumElevationInMeters() != null && collectingEvent.getDwcMinimumElevationInMeters() != null &&
        collectingEvent.getDwcMaximumElevationInMeters().compareTo(collectingEvent.getDwcMinimumElevationInMeters()) < 0) {
      addError(errors, VALID_MAX_LESS_THAN_MIN_ELEVATION);
    }
    if (collectingEvent.getDwcMaximumElevationInMeters() != null && collectingEvent.getDwcMinimumElevationInMeters() == null) {
      addError(errors, VALID_MIN_MAX_ELEVATION);
    }
  }

  private void validateDateTimes(CollectingEvent collectingEvent, Errors errors) {
    if (collectingEvent.getStartEventDateTime() == null && collectingEvent.getEndEventDateTime() != null ||
        collectingEvent.getEndEventDateTime() != null && collectingEvent.getStartEventDateTime()
            .isAfter(collectingEvent.getEndEventDateTime())) {
      addError(errors, VALID_EVENT_DATE_KEY);
    }
  }

  private void validateGeographicPlaceNameSourceDetail(CollectingEvent collectingEvent, Errors errors) {
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
        validateSourceAdministrativeLevel(geographicPlaceNameSourceDetail.getSelectedGeographicPlace(), errors);
      }
      if (CollectionUtils.isNotEmpty(geographicPlaceNameSourceDetail.getHigherGeographicPlaces())) {
        for (SourceAdministrativeLevel sal : geographicPlaceNameSourceDetail.getHigherGeographicPlaces()) {
          validateSourceAdministrativeLevel(sal, errors);
        }
      }
    }
  }

  private void validateSourceAdministrativeLevel(SourceAdministrativeLevel sal, Errors errors) {
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
    String errorMessage = getMessage(messageBundleKey);
    errors.reject(messageBundleKey, errorMessage);
  }
}
