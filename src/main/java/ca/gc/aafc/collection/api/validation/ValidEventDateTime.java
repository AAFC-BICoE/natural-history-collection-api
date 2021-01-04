package ca.gc.aafc.collection.api.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used for bean validation of the {@link ca.gc.aafc.collection.api.entities.CollectingEvent}.
 * Validation is done through the {@link ValidEventDateTimeValidator}
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidEventDateTimeValidator.class)
@Documented
public @interface ValidEventDateTime {

  String message() default "{validation.constraint.violation.validEventDateTime}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
