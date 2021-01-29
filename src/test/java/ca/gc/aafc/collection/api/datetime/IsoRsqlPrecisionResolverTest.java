package ca.gc.aafc.collection.api.datetime;

import cz.jirutka.rsql.parser.RSQLParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class IsoRsqlPrecisionResolverTest {

  public static final RSQLParser RSQL_PARSER = new RSQLParser();
  public static final List<String> FIELD_LIST = List.of("startEventDateTime");

  @Test
  void highestPercision() {
    IsoRsqlPrecisionResolver resolver = new IsoRsqlPrecisionResolver(FIELD_LIST);
    Assertions.assertEquals(6, RSQL_PARSER.parse(
      "startEventDateTime=ge=1800-01")
      .accept(resolver, FIELD_LIST).getHighestPrecision());
    Assertions.assertEquals(6, RSQL_PARSER.parse(
      "startEventDateTime=ge=1800-01 and startEventDateTime=le=1800-02")
      .accept(resolver, FIELD_LIST).getHighestPrecision());
    Assertions.assertEquals(6, RSQL_PARSER.parse(
      "startEventDateTime=ge=1800-01 or startEventDateTime=le=1800-02")
      .accept(resolver, FIELD_LIST).getHighestPrecision());
  }
}