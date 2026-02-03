package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import ca.gc.aafc.collection.api.entities.Site;
import ca.gc.aafc.dina.validation.DinaBaseValidator;

@Component
public class SiteValidator extends DinaBaseValidator<Site> {
    static final String VALID_EVENT_DATE_KEY = "validation.constraint.violation.validEventDateTime";

    public SiteValidator(MessageSource messageSource) {
        super(Site.class, messageSource);
    }

    @Override
    public void validateTarget(Site target, Errors errors) {
        validateStartEndDate(target, errors);
    }

    private void validateStartEndDate(Site project, Errors errors) {
        if (project.getStartDate() == null && project.getEndDate() != null ||
                project.getEndDate() != null && project.getStartDate()
                        .isAfter(project.getEndDate())) {
            addError(errors, VALID_EVENT_DATE_KEY);
        }
    }

    private void addError(Errors errors, String messageBundleKey) {
        String errorMessage = getMessage(messageBundleKey);
        errors.reject(messageBundleKey, errorMessage);
    }
}
