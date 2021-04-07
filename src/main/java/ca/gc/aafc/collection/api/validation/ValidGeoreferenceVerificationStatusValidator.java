package ca.gc.aafc.collection.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion.GeoreferenceVerificationStatus;

public class ValidGeoreferenceVerificationStatusValidator implements ConstraintValidator<ValidGeoreferenceVerificationStatus, GeoreferenceAssertion> {
    
    @Override
    public boolean isValid(GeoreferenceAssertion georeferenceAssertion, ConstraintValidatorContext context) {
        if (georeferenceAssertion.getDwcGeoreferenceVerificationStatus() == GeoreferenceVerificationStatus.GEOREFERENCING_NOT_POSSIBLE) {
            return georeferenceAssertion.getDwcDecimalLatitude() == null && georeferenceAssertion.getDwcDecimalLongitude() == null && georeferenceAssertion.getDwcCoordinateUncertaintyInMeters() == null;
        } else {
            return true;
        }
    }
}
