package ca.gc.aafc.collection.api.exceptionmapping;

import io.crnk.core.exception.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public final class HierarchyExceptionMappingUtils {

  public static final String SQL_STATE_CODE = "23d66";

  private HierarchyExceptionMappingUtils() {
  }

  public static void throwIfHierarchyViolation(Throwable cause, MessageSource messageSource) {
    Throwable nestedCause = findNestedCause(cause);
    if (nestedCause instanceof PSQLException) {
      PSQLException p = (PSQLException) nestedCause;
      if (p.getSQLState().equalsIgnoreCase(SQL_STATE_CODE)) {
        String message = p.getMessage();
        ServerErrorMessage serverErrorMessage = p.getServerErrorMessage();
        if (serverErrorMessage != null && messageSource != null) {
          String messageKey = serverErrorMessage.getHint();
          if (StringUtils.isNotBlank(messageKey)) {
            message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
          }
        }
        throw new BadRequestException(message);
      }
    }
  }

  private static Throwable findNestedCause(Throwable e) {
    Throwable nestedCause = e;
    while (nestedCause.getCause() != null) {
      nestedCause = nestedCause.getCause();
    }
    return nestedCause;
  }
}
