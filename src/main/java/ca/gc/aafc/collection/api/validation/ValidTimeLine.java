package ca.gc.aafc.collection.api.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidTimeLineValidator.class)
@Documented
public @interface ValidTimeLine {

  String message() default "The start and end dates do not create a valid time line.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
