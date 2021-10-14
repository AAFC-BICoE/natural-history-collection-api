package ca.gc.aafc.collection.api.exceptionmapping;

import io.crnk.core.exception.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

import java.util.Objects;
import java.util.function.Function;

/**
 * Dina Hierarchy exception are thrown by the database with the SQLCode defined by {@link #SQL_STATE_CODE}.
 * The hint contains a key to the message bundle used to provided the error message.
 */
public final class HierarchyExceptionMappingUtils {

  // Custom Code used by Dina to report hierarchy related issues
  public static final String SQL_STATE_CODE = "23d66";

  private HierarchyExceptionMappingUtils() {
  }

  /**
   *
   * @param exception exception thrown
   * @param messageProvider function to get a message by key
   */
  public static void throwIfHierarchyViolation(Throwable exception, Function<String, String> messageProvider) {
    Objects.requireNonNull(messageProvider);

    Throwable nestedCause = findRootCause(exception);
    if (nestedCause instanceof PSQLException) {
      PSQLException p = (PSQLException) nestedCause;
      if (p.getSQLState().equalsIgnoreCase(SQL_STATE_CODE)) {
        String message = p.getMessage();
        ServerErrorMessage serverErrorMessage = p.getServerErrorMessage();
        if (serverErrorMessage != null) {
          String messageKey = serverErrorMessage.getHint();
          if (StringUtils.isNotBlank(messageKey)) {
            message = messageProvider.apply(messageKey);
          }
        }
        throw new BadRequestException(message);
      }
    }
  }

  private static Throwable findRootCause(Throwable e) {
    Throwable nestedCause = e;
    while (nestedCause.getCause() != null) {
      nestedCause = nestedCause.getCause();
    }
    return nestedCause;
  }
}
