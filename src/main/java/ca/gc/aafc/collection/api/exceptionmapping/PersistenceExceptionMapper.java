package ca.gc.aafc.collection.api.exceptionmapping;

import org.postgresql.util.PSQLException;

import javax.persistence.PersistenceException;

public final class PersistenceExceptionMapper {

  private PersistenceExceptionMapper() {
  }

  public static final String SQL_STATE_CODE = "23d66";

  public static boolean isHierarchyViolation(PersistenceException e) {
    Throwable nestedCause = e;
    while (nestedCause.getCause() != null) {
      nestedCause = nestedCause.getCause();
    }
    if (nestedCause instanceof PSQLException) {
      PSQLException p = (PSQLException) nestedCause;
      return p.getSQLState().equalsIgnoreCase(SQL_STATE_CODE);
    }
    return false;
  }

}
