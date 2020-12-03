package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.CustomFieldAdapter;
import ca.gc.aafc.dina.mapper.DinaFieldAdapter;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RelatedEntity(CollectingEvent.class)
@CustomFieldAdapter(adapters = {
    CollectingEventDto.StartEventDateTimeAdapter.class,
    CollectingEventDto.EndEventDateTimeAdapter.class})
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = "collecting-event")
public class CollectingEventDto {

  @JsonApiId
  private UUID uuid;

  private Double decimalLatitude;
  private Double decimalLongitude;

  private Integer coordinateUncertaintyInMeters;
  private String verbatimCoordinates;

  @IgnoreDinaMapping
  private String startEventDateTime;

  @IgnoreDinaMapping
  private String endEventDateTime;

  private String verbatimEventDateTime;


  @NoArgsConstructor
  public static final class StartEventDateTimeAdapter
      implements DinaFieldAdapter<CollectingEventDto, CollectingEvent, String, ISODateTime> {

    @Override
    public String toDTO(ISODateTime isoDateTime) {
      return isoDateTime.toString();
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

    @Override
    public Supplier<ISODateTime> entitySupplyMethod(CollectingEvent entityRef) {
      return entityRef::supplyStartISOEventDateTime;
    }

    @Override
    public Supplier<String> dtoSupplyMethod(CollectingEventDto dtoRef) {
      return dtoRef::getStartEventDateTime;
    }

  }

  @NoArgsConstructor
  public static final class EndEventDateTimeAdapter
      implements DinaFieldAdapter<CollectingEventDto, CollectingEvent, String, ISODateTime> {
    @Override
    public String toDTO(ISODateTime isoDateTime) {
      return isoDateTime.toString();
    }

    @Override
    public ISODateTime toEntity(String startEventDateTime) {
      return ISODateTime.parse(startEventDateTime);
    }

    @Override
    public Consumer<ISODateTime> entityApplyMethod(CollectingEvent entityRef) {
      return entityRef::applyEndISOEventDateTime;
    }

    @Override
    public Consumer<String> dtoApplyMethod(CollectingEventDto dtoRef) {
      return dtoRef::setEndEventDateTime;
    }

    @Override
    public Supplier<ISODateTime> entitySupplyMethod(CollectingEvent entityRef) {
      return entityRef::supplyEndISOEventDateTime;
    }

    @Override
    public Supplier<String> dtoSupplyMethod(CollectingEventDto dtoRef) {
      return dtoRef::getEndEventDateTime;
    }
  }

}
