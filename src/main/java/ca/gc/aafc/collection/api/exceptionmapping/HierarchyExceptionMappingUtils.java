package ca.gc.aafc.collection.api.exceptionmapping;

import io.crnk.core.exception.BadRequestException;
import org.postgresql.util.PSQLException;

public final class HierarchyExceptionMappingUtils {

  public static final String SQL_STATE_CODE = "23d66";

  private HierarchyExceptionMappingUtils() {
  }

  public static void throwIfHierarchyViolation(Throwable cause) {
    Throwable nestedCause = findNestedCause(cause);
    if (nestedCause instanceof PSQLException) {
      PSQLException p = (PSQLException) nestedCause;
      if (p.getSQLState().equalsIgnoreCase(SQL_STATE_CODE)) {
        throw new BadRequestException(p.getMessage());
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
