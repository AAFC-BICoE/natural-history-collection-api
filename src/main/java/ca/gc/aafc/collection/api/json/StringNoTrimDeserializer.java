package ca.gc.aafc.collection.api.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class StringNoTrimDeserializer extends StdDeserializer<String> {

  public StringNoTrimDeserializer() {
    super(String.class);
  }

  @Override
  public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws
    IOException {
    return jsonParser.getValueAsString();
  }
}
