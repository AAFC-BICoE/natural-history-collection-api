package ca.gc.aafc.collection.api.exceptionmapping;

import java.time.format.DateTimeParseException;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.toedter.spring.hateoas.jsonapi.JsonApiError;
import com.toedter.spring.hateoas.jsonapi.JsonApiErrors;

import ca.gc.aafc.dina.repository.DinaRepositoryV2;

/**
 * Dina Hierarchy exception are thrown by the database with the SQLCode defined by {@link #SQL_STATE_CODE}.
 * The hint contains a key to the message bundle used to provide the error message.
 */
@RestControllerAdvice(assignableTypes = DinaRepositoryV2.class)
public class JsonApiExceptionControllerAdviceCollection {

  // Custom Code used by Dina to report hierarchy related issues
  public static final String SQL_STATE_CODE = "23d66";

  @Inject
  private MessageSource messageSource;

  @ExceptionHandler
  public ResponseEntity<JsonApiErrors> handlePSQLException(PSQLException psqlEx) {

    String details = psqlEx.getMessage();

    if (psqlEx.getSQLState().equalsIgnoreCase(SQL_STATE_CODE)) {
      ServerErrorMessage serverErrorMessage = psqlEx.getServerErrorMessage();
      if (serverErrorMessage != null) {
        String messageKey = serverErrorMessage.getHint();
        if (StringUtils.isNotBlank(messageKey)) {
          details = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        }
      }
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      JsonApiErrors.create().withError(
        JsonApiError.create()
          .withCode(Integer.toString(HttpStatus.BAD_REQUEST.value()))
          .withStatus(HttpStatus.BAD_REQUEST.toString())
          .withTitle("Bad Request")
          .withDetail(details))
    );
  }

  @ExceptionHandler
  public ResponseEntity<JsonApiErrors> handleDateTimeParseException(DateTimeParseException dtpEx) {

    String detail = messageSource.getMessage("exception.dateTimeParse.message",
      new Object[]{dtpEx.getParsedString(), dtpEx.getErrorIndex()}, LocaleContextHolder.getLocale());

    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
      JsonApiErrors.create().withError(
        JsonApiError.create()
          .withCode(Integer.toString(HttpStatus.UNPROCESSABLE_ENTITY.value()))
          .withStatus(HttpStatus.UNPROCESSABLE_ENTITY.toString())
          .withTitle("Un-processable Entity")
          .withDetail(detail))
    );
  }
}
