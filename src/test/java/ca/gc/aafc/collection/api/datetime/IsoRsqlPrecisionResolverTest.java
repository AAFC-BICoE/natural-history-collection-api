package ca.gc.aafc.collection.api.datetime;

import cz.jirutka.rsql.parser.RSQLParser;
import org.junit.jupiter.api.Test;

import java.util.Set;

class IsoRsqlPrecisionResolverTest {

  public static final RSQLParser RSQL_PARSER = new RSQLParser();
  public static final Set<String> FIELD_LIST = Set.of("startEventDateTime");

  @Test
  void highestPercision() {
    IsoRsqlPrecisionResolver resolver = new IsoRsqlPrecisionResolver(FIELD_LIST);
    System.out.println(RSQL_PARSER.parse("startEventDateTime==1800-01").accept(resolver, FIELD_LIST));
  }
}