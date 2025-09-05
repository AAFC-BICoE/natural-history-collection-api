package ca.gc.aafc.collection.api.mapper;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.dina.datetime.ISODateTime;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Collecting Event contains some custom mappings using {@link ca.gc.aafc.dina.mapper.DinaFieldAdapter}.
 * This class ensure the mapping is configured to return expected results.
 */
public class CollectingEventMapperTest {

  private static final CollectingEventMapper MAPPER = CollectingEventMapper.INSTANCE;

  @Test
  public void testEventDateMappingToEntity() {
    CollectingEventDto ceDto = new CollectingEventDto();
    ceDto.setStartEventDateTime("2019-08-13");
    ceDto.setEndEventDateTime("2019-08-14");

    CollectingEvent ceEntity = new CollectingEvent();
    MAPPER.patchEntity(ceEntity, ceDto,
      Set.of("startEventDateTime", "endEventDateTime"), null);

    assertEquals(LocalDateTime.of(2019,8,13,0, 0),
        ceEntity.getStartEventDateTime());
    assertEquals(LocalDateTime.of(2019,8,14,0, 0),
        ceEntity.getEndEventDateTime());
    assertEquals((byte)8, ceEntity.getStartEventDateTimePrecision());
    assertEquals((byte)8, ceEntity.getEndEventDateTimePrecision());
  }

  @Test
  public void testEventDateMappingToEntityNullEmpty() {
    CollectingEventDto ceDto = new CollectingEventDto();
    ceDto.setStartEventDateTime("");
    ceDto.setEndEventDateTime(null);

    CollectingEvent ceEntity = new CollectingEvent();
    MAPPER.patchEntity(ceEntity, ceDto,
        Set.of("startEventDateTime", "endEventDateTime"), null);
    assertNull(ceEntity.getStartEventDateTime());
    assertNull(ceEntity.getEndEventDateTime());
  }

  @Test
  public void testEventDateMappingToDtoNull() {
    CollectingEvent ceEntity = CollectingEventFactory
        .newCollectingEvent()
        .startEventDateTime(null)
        .startEventDateTimePrecision(null)
        .build();

    Map<String, Object> asMap = JsonAPITestHelper.toAttributeMap(ceEntity);
    Set<String> attributesNames = new HashSet<>(asMap.keySet());

    CollectingEventDto ceDto = MAPPER.toDto(ceEntity, attributesNames, null);
    assertNull(ceDto.getStartEventDateTime());
  }

  @Test
  public void testEventDateMappingToDto() {
    CollectingEvent ceEntity = CollectingEvent.builder()
        .startEventDateTime(LocalDateTime.of(2019,8,13,0, 0))
        .startEventDateTimePrecision(ISODateTime.Format.YYYY_MM_DD.getPrecision())
        .endEventDateTime(LocalDateTime.of(2019,8,14,0, 0))
        .endEventDateTimePrecision(ISODateTime.Format.YYYY_MM_DD.getPrecision())
        .build();

    Map<String, Object> asMap = JsonAPITestHelper.toAttributeMap(ceEntity);
    Set<String> attributesNames = new HashSet<>(asMap.keySet());

    CollectingEventDto ceDto = MAPPER.toDto(ceEntity, attributesNames, null);
    assertEquals("2019-08-13", ceDto.getStartEventDateTime());
    assertEquals("2019-08-14", ceDto.getEndEventDateTime());
  }
}
