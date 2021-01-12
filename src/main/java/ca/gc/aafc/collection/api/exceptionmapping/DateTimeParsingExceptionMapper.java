package ca.gc.aafc.collection.api.exceptionmapping;

import io.crnk.core.engine.document.ErrorData;
import io.crnk.core.engine.error.ErrorResponse;
import io.crnk.core.engine.error.ExceptionMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

@Named
public class DateTimeParsingExceptionMapper implements ExceptionMapper<DateTimeParseException> {
  private static final int HTTP_ERROR_CODE = 422;

  @Inject
  private MessageSource messageSource;

  @Override
  public ErrorResponse toErrorResponse(DateTimeParseException e) {
    return ErrorResponse.builder().setStatus(HTTP_ERROR_CODE).setSingleErrorData(
      ErrorData.builder()
        .setDetail(messageSource.getMessage(
          "exception.dateTimeParse.message",
          new Object[]{e.getParsedString(), e.getErrorIndex()},
          LocaleContextHolder.getLocale()))
        .setTitle("Un-processable Entity")
        .setStatus(Integer.toString(HTTP_ERROR_CODE))
        .build())
      .build();
  }

  @Override
  public DateTimeParseException fromErrorResponse(ErrorResponse errorResponse) {
    return new DateTimeParseException(
      errorResponse.getResponse()
        .getErrors()
        .stream().map(ErrorData::getDetail)
        .collect(Collectors.joining(System.lineSeparator())), "", 0);
  }

  @Override
  public boolean accepts(ErrorResponse errorResponse) {
    return errorResponse.getHttpStatus() == HTTP_ERROR_CODE;
  }
}
