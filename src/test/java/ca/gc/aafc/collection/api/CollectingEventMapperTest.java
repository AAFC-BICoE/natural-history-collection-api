package ca.gc.aafc.collection.api;

import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.dina.mapper.DinaMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectingEventMapperTest {

  private static final DinaMapper<CollectingEventDto, CollectingEvent> CE_MAPPER =
      new DinaMapper<>(CollectingEventDto.class);

  @Test
  public void testEventDateMappingToEntity() {
    CollectingEventDto ceDto = new CollectingEventDto();
    ceDto.setStartEventDateTime("2019-08-13");

    CollectingEvent ceEntity = new CollectingEvent();
    CE_MAPPER.applyDtoToEntity(ceDto, ceEntity, Map.of(CollectingEventDto.class,
        Collections.emptySet()), Collections.emptySet());

    assertEquals(LocalDateTime.of(2019,8,13,0, 0),
        ceEntity.getStartEventDateTime());
    assertEquals((byte)8, ceEntity.getStartEventDateTimePrecision());
  }

  @Test
  public void testEventDateMappingToDto() {
    CollectingEvent ceEntity = CollectingEvent.builder()
        .startEventDateTime(LocalDateTime.of(2019,8,13,0, 0))
        .startEventDateTimePrecision(ISODateTime.Format.YYYY_MM_DD.getPrecision())
        .build();

    CollectingEventDto ceDto = CE_MAPPER.toDto(ceEntity, Map.of(CollectingEvent.class,
        Collections.emptySet()), Collections.emptySet());
    assertEquals("2019-08-13", ceDto.getStartEventDateTime());
  }
}
