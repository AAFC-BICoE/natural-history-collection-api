package ca.gc.aafc.collection.api.datetime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * WORK-IN-PROGRESS
 * ISO DateTime representation with support for partial dates.
 * This class is immutable.
 */
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ISODateTime {

  private static final int YYYY_PRECISION = 4;
  private static final int YYYY_MM_PRECISION = 6;
  private static final int YYYY_MM_DD_PRECISION = 8;

  public enum Format {
    YYYY(YYYY_PRECISION),
    YYYY_MM(YYYY_MM_PRECISION),
    YYYY_MM_DD(YYYY_MM_DD_PRECISION);

    private final int precision;
    Format(int precision) {
      this.precision = precision;
    }

    public static Optional<Format> fromPrecision(int precision) {
      switch(precision) {
        case YYYY_PRECISION : return Optional.of(YYYY);
        case YYYY_MM_PRECISION : return Optional.of(YYYY_MM);
        case YYYY_MM_DD_PRECISION : return Optional.of(YYYY_MM_DD);
        default:
          break;
      }
      return Optional.empty();
    }

    public int getPrecision() {
      return precision;
    }

  }

  private final LocalDateTime localDateTime;
  private final Format format;

  /**
   * Should probably throw DateTimeParseException
   * @param date
   * @return
   */
  public static ISODateTime parse(String date) {
    int numberOfNumericChar = StringUtils.remove(date, "-").length();

    Format format = Format.fromPrecision(numberOfNumericChar)
        .orElseThrow(() -> new DateTimeParseException("unknown format", date, 0));
    LocalDateTime parsedLocalDateTime = null;

    switch (format) {
    case YYYY:
      parsedLocalDateTime = Year.parse(date).atDay(1).atStartOfDay();
      break;
    case YYYY_MM:
      parsedLocalDateTime = YearMonth.parse(date).atDay(1).atStartOfDay();
      break;
    case YYYY_MM_DD:
      parsedLocalDateTime = LocalDate.parse(date).atStartOfDay();
      break;
    default:
      break;
    }
    return new ISODateTime(parsedLocalDateTime, format);
  }

  public String toString() {
    switch (format) {
      case YYYY:
        return Year.from(localDateTime).toString();
      case YYYY_MM:
        return YearMonth.from(localDateTime).toString();
      case YYYY_MM_DD:
        return LocalDate.from(localDateTime).toString();
      default:
        break;
    }
    return "";
  }

}
