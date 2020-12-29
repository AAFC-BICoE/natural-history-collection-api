package ca.gc.aafc.collection.api.exceptionmapping;

import io.crnk.core.engine.document.ErrorData;
import io.crnk.core.engine.error.ErrorResponse;
import io.crnk.core.engine.error.ExceptionMapper;

import javax.inject.Named;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

@Named
public class DateTimeParsingExceptionMapper implements ExceptionMapper<DateTimeParseException> {
  private static final int HTTP_ERROR_CODE = 422;

  @Override
  public ErrorResponse toErrorResponse(DateTimeParseException e) {
    return ErrorResponse.builder()
      .setStatus(HTTP_ERROR_CODE)
      .setSingleErrorData(ErrorData.builder().setDetail(e.getMessage()).build())
      .build();
  }

  @Override
  public DateTimeParseException fromErrorResponse(ErrorResponse errorResponse) {
    return new DateTimeParseException(
      errorResponse.getResponse()
        .getErrors()
        .stream().map(ErrorData::getTitle)
        .collect(Collectors.joining("")), "", 0);
  }

  @Override
  public boolean accepts(ErrorResponse errorResponse) {
    return errorResponse.getHttpStatus() == HTTP_ERROR_CODE;
  }
}
