package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.datetime.IsoRsqlVisitor;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.CustomFieldAdapter;
import ca.gc.aafc.dina.mapper.DinaFieldAdapter;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
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
  private UUID collectorGroupUuid;

  private String group;

  private String createdBy;
  private OffsetDateTime createdOn;

  private Double decimalLatitude;
  private Double decimalLongitude;

  private Integer coordinateUncertaintyInMeters;
  private String verbatimCoordinates;
  private String verbatimCollectors;

  @IgnoreDinaMapping
  private String startEventDateTime;

  @IgnoreDinaMapping
  private String endEventDateTime;

  private String verbatimEventDateTime;

  @JsonApiExternalRelation(type = "agent")
  @JsonApiRelation
  private List<ExternalRelationDto> collectors = new ArrayList<>();

  @JsonApiExternalRelation(type = "metadata")
  @JsonApiRelation
  private List<ExternalRelationDto> attachment = new ArrayList<>();

  @NoArgsConstructor
  public static final class StartEventDateTimeAdapter
    implements DinaFieldAdapter<CollectingEventDto, CollectingEvent, String, ISODateTime> {

    @Override
    public String toDTO(@Nullable ISODateTime isoDateTime) {
      return isoDateTime == null ? null : isoDateTime.toString();
    }

    @Override
    public ISODateTime toEntity(@Nullable String startEventDateTime) {
      if (StringUtils.isBlank(startEventDateTime)) {
        return null;
      }
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

    @Override
    public Map<String, Function<FilterSpec, FilterSpec[]>> toFilterSpec() {
      return Map.of("rsql", filterSpec -> {
        Node node = new RSQLParser().parse(filterSpec.getValue());
        String translatedQuery = node.accept(new IsoRsqlVisitor(), "startEventDateTime");
        return new FilterSpec[]{PathSpec.of("rsql").filter(FilterOperator.EQ, translatedQuery)};
      });
    }
  }

  @NoArgsConstructor
  public static final class EndEventDateTimeAdapter
    implements DinaFieldAdapter<CollectingEventDto, CollectingEvent, String, ISODateTime> {

    @Override
    public String toDTO(@Nullable ISODateTime isoDateTime) {
      return isoDateTime == null ? null : isoDateTime.toString();
    }

    @Override
    public ISODateTime toEntity(@Nullable String endEventDateTime) {
      if (StringUtils.isBlank(endEventDateTime)) {
        return null;
      }
      return ISODateTime.parse(endEventDateTime);
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
