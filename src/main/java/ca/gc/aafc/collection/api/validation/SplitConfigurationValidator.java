package ca.gc.aafc.collection.api.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.entities.SplitConfiguration;

import java.util.Set;
import lombok.NonNull;

@Component
public class SplitConfigurationValidator implements Validator {

  public static final Set<String> SUPPORTED_SEPARATOR = Set.of(" ", "-", "_");

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return SplitConfiguration.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("SplitConfigurationValidator not supported for class " + target.getClass());
    }
    SplitConfiguration splitConfiguration = (SplitConfiguration) target;

    if(!SUPPORTED_SEPARATOR.contains(splitConfiguration.getSeparator())) {
      errors.rejectValue("separator", "invalid.separator", "invalid separator");
    }
  }
}
