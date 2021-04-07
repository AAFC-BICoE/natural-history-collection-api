package ca.gc.aafc.collection.api.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidGeoreferenceVerificationStatusValidator.class)
@Documented
public @interface ValidGeoreferenceVerificationStatus {

    //String message() default "{validation.constraint.violation.validGeoreferenceVerificationStatus}";

    String message() default "dwcDecimalLatitude, dwcDecimalLongitude and dwcCoordinateUncertaintyInMeters must be null if dwcGeoreferenceVerificationStatus is GEOREFERENCING_NOT_POSSIBLE";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
}
