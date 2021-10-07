package ca.gc.aafc.collection.api;

import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.dina.mapper.DinaMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Collecting Event contains some custom mappings using {@link ca.gc.aafc.dina.mapper.DinaFieldAdapter}.
 * This class ensure the mapping is configured to return expected results.
 */
public class CollectingEventMapperTest {

  private static final DinaMapper<CollectingEventDto, CollectingEvent> CE_MAPPER =
      new DinaMapper<>(CollectingEventDto.class);

  @Test
  public void testEventDateMappingToEntity() {
    CollectingEventDto ceDto = new CollectingEventDto();
    ceDto.setStartEventDateTime("2019-08-13");
    ceDto.setEndEventDateTime("2019-08-14");

    CollectingEvent ceEntity = new CollectingEvent();
    CE_MAPPER.applyDtoToEntity(ceDto, ceEntity, Map.of(CollectingEventDto.class,
        Collections.emptySet()), Collections.emptySet());

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
    CE_MAPPER.applyDtoToEntity(ceDto, ceEntity, Map.of(CollectingEventDto.class,
        Collections.emptySet()), Collections.emptySet());
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

    CollectingEventDto ceDto = CE_MAPPER.toDto(ceEntity);
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

    CollectingEventDto ceDto = CE_MAPPER.toDto(ceEntity, Map.of(CollectingEvent.class,
        Collections.emptySet()), Collections.emptySet());
    assertEquals("2019-08-13", ceDto.getStartEventDateTime());
    assertEquals("2019-08-14", ceDto.getEndEventDateTime());
  }
}
