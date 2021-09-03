package ca.gc.aafc.collection.api.exceptionmapping;

import io.crnk.core.engine.error.ErrorResponse;
import io.crnk.core.engine.error.ExceptionMapper;
import org.hibernate.exception.ConstraintViolationException;

public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

  @Override
  public ErrorResponse toErrorResponse(ConstraintViolationException e) {
    //TODO
    return null;
  }

  @Override
  public ConstraintViolationException fromErrorResponse(ErrorResponse errorResponse) {
    throw new UnsupportedOperationException("Crnk client not supported");
  }

  @Override
  public boolean accepts(ErrorResponse errorResponse) {
    throw new UnsupportedOperationException("Crnk client not supported");
  }
}
