package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.CustomFieldResolver;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

import java.util.UUID;

@RelatedEntity(CollectingEvent.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = "collecting-event")
public class CollectingEventDto {

  @JsonApiId
  private UUID uuid;

  private String startEventDateTime;

  /**
   * TODO: validate that IllegalArgumentException is the right exception to throw here
   * @param entity
   * @return
   */
  @CustomFieldResolver(fieldName = "startEventDateTime")
  public static String startEventDateTimeToDTO(CollectingEvent entity) {
    return ISODateTime.builder().localDateTime(entity.getStartEventDateTime()).format(ISODateTime.Format.fromPrecision(
        entity.getStartEventDateTimePrecision()).orElseThrow( () -> new IllegalArgumentException("Invalid EventDateTimePrecision")))
        .build()
        .toString();
  }

  @CustomFieldResolver(fieldName = "startISOEventDateTime")
  public static ISODateTime startEventDateTimeToEntity(CollectingEventDto dto) {
    return ISODateTime.parse(dto.getStartEventDateTime());
  }

}
