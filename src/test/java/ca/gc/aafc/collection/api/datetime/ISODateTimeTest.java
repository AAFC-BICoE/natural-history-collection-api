package ca.gc.aafc.collection.api.datetime;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ISODateTimeTest {

  @ParameterizedTest @ValueSource(strings = { "2019", "2019-08", "2019-08-23",
      "2019-08-23T12:24:34", "2020-12-23T05:01:02.333" })
  public void testRoundTrip(String input) {
    assertEquals(input, ISODateTime.parse(input).toString());
  }

  @ParameterizedTest @ValueSource(strings = { "abc", "2019-45", "2019-08-32", "2019-08-23T12:24:75" })
  public void testWrongDate(String input) {
    assertThrows(DateTimeParseException.class, () -> ISODateTime.parse(input));
  }

}
