package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import lombok.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class GeoreferenceAssertionValidator implements Validator {

  private final MessageSource messageSource;

  public GeoreferenceAssertionValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return GeoreferenceAssertionDto.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("GeorefenceAssertionValidator not supported for class " + target.getClass());
    }
    GeoreferenceAssertionDto georeferenceAssertion = (GeoreferenceAssertionDto) target;

    if (georeferenceAssertion.getDwcGeoreferenceVerificationStatus() == GeoreferenceAssertionDto.GeoreferenceVerificationStatus.GEOREFERENCING_NOT_POSSIBLE &&
        (georeferenceAssertion.getDwcDecimalLatitude() != null
            || georeferenceAssertion.getDwcDecimalLongitude() != null
            || georeferenceAssertion.getDwcCoordinateUncertaintyInMeters() != null)) {
      String errorMessage = messageSource.getMessage("georeferenceAssertion.GeoreferenceVerificationStatus.invalid",
        null,
        LocaleContextHolder.getLocale());
      errors.rejectValue(
        "dwcGeoreferenceVerificationStatus",
        "georeferenceAssertion.GeoreferenceVerificationStatus.invalid",
        errorMessage);
    }
  }
}
