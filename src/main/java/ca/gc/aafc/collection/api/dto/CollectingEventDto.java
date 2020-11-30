package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.CustomFieldAdapter;
import ca.gc.aafc.dina.mapper.DinaFieldAdapter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.function.Consumer;

@RelatedEntity(CollectingEvent.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = "collecting-event")
public class CollectingEventDto {

  @JsonApiId
  private UUID uuid;

  @CustomFieldAdapter(adapter = StartEventDateTimeAdapter.class)
  private String startEventDateTime;

  /**
   * TODO: validate that IllegalArgumentException is the right exception to throw here
   * @param entity
   * @return
   */
//
//  public static String startEventDateTimeToDTO(CollectingEvent entity) {
//    return ISODateTime.builder().localDateTime(entity.getStartEventDateTime()).format(ISODateTime.Format.fromPrecision(
//        entity.getStartEventDateTimePrecision()).orElseThrow( () -> new IllegalArgumentException("Invalid EventDateTimePrecision")))
//        .build()
//        .toString();

  @NoArgsConstructor
  public static final class StartEventDateTimeAdapter
      implements DinaFieldAdapter<CollectingEventDto, CollectingEvent, String, ISODateTime> {

    @Override
    public String toDTO(ISODateTime isoDateTime) {
      // a piece is missing here since there is no getter on the entity to get an ISODateTime
      // we probably need to provide a producer from the entity
      //public Producer<ISODateTime> entityApplyMethod(CollectingEvent entityRef) {
      //  return entityRef::startISOEventDateTime;
      //}
      //it should be optional since it's only necessary if there is an intermediate type
      return null;
    }

    @Override
    public ISODateTime toEntity(String startEventDateTime) {
      return ISODateTime.parse(startEventDateTime);
    }

    @Override
    public Consumer<ISODateTime> entityApplyMethod(CollectingEvent entityRef) {
      return entityRef::applyStartISOEventDateTime;
    }

    @Override
    public Consumer<String> dtoApplyMethod(CollectingEventDto dtoRef) {
      return dtoRef::setStartEventDateTime;
    }
  }

}
