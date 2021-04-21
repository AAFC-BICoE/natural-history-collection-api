package ca.gc.aafc.collection.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.Test;
import org.mockito.Mockito;

import io.crnk.core.engine.parser.StringMapper;

@SuppressWarnings("unchecked")
public class ManagedAttributeIdMapperTest {

  @Test
  public void parseId_whenIdIsUuid_returnDefaultMapping() {
    var uuidString = "00000000-0000-0000-0000-000000000000";

    var mockMapper = Mockito.mock(StringMapper.class);
    Mockito.when(mockMapper.parse(uuidString)).thenReturn(UUID.fromString(uuidString));

    var customMapper = new ManagedAttributeIdMapper(mockMapper);

    assertEquals(UUID.fromString(uuidString), customMapper.parse(uuidString));
  }

  @Test
  public void parseId_whenIdIsNotUuid_returnSameString() {
    var key = "collecting_event.attribute_name";

    var mockMapper = Mockito.mock(StringMapper.class);
    var customMapper = new ManagedAttributeIdMapper(mockMapper);

    assertEquals(key, customMapper.parse(key));
  }

  @Test
  public void toString_passInput_callInternalMapper() {
    var input = "test-input";
    var output = "test-output";

    var mockMapper = Mockito.mock(StringMapper.class);
    Mockito.when(mockMapper.toString(input)).thenReturn(output);

    var customMapper = new ManagedAttributeIdMapper(mockMapper);

    assertEquals(output, customMapper.toString(input));
  }

}
