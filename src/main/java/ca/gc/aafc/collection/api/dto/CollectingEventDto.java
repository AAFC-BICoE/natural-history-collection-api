package ca.gc.aafc.collection.api.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.apache.commons.lang3.StringUtils;

import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.datetime.IsoDateTimeRsqlResolver;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.CustomFieldAdapter;
import ca.gc.aafc.dina.mapper.DinaFieldAdapter;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  private String group;

  private String createdBy;
  private OffsetDateTime createdOn;

  @JsonApiRelation
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<GeoreferenceAssertionDto> geoReferenceAssertions = new ArrayList<>();

  private String dwcVerbatimCoordinates;
  private String dwcRecordedBy;

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

  private String dwcVerbatimLocality;


  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @JsonApiRelation
  private List<CollectingEventManagedAttributeDto> managedAttributes = new ArrayList<>();

  private String dwcVerbatimLatitude;
  private String dwcVerbatimLongitude;
  private String dwcVerbatimCoordinateSystem;
  private String dwcVerbatimSRS;
  private String dwcVerbatimElevation;
  private String dwcVerbatimDepth; 
  private String[] dwcOtherRecordNumbers;
  private String dwcRecordNumber;

  @NoArgsConstructor
  public static final class StartEventDateTimeAdapter
    implements DinaFieldAdapter<CollectingEventDto, CollectingEvent, String, ISODateTime> {

    private static final IsoDateTimeRsqlResolver ISO_RSQL_VISITOR = new IsoDateTimeRsqlResolver(
      "startEventDateTime", "startEventDateTimePrecision");

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

    // Commented out for hotfix due to bug in date filtering See Redmine:21603
//    @Override
//    public Map<String, Function<FilterSpec, FilterSpec[]>> toFilterSpec() {
//      return Map.of("rsql", filterSpec -> new FilterSpec[]{PathSpec.of("rsql").filter(
//        FilterOperator.EQ, ISO_RSQL_VISITOR.resolveDates(filterSpec.getValue()))});
//    }
  }

  @NoArgsConstructor
  public static final class EndEventDateTimeAdapter
    implements DinaFieldAdapter<CollectingEventDto, CollectingEvent, String, ISODateTime> {
    private static final IsoDateTimeRsqlResolver ISO_RSQL_VISITOR = new IsoDateTimeRsqlResolver(
      "endEventDateTime", "endEventDateTimePrecision");

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

    //C.G. commented since it will run even if the field is not included
//    @Override
//    public Map<String, Function<FilterSpec, FilterSpec[]>> toFilterSpec() {
//      return Map.of("rsql", filterSpec -> new FilterSpec[]{PathSpec.of("rsql").filter(
//        FilterOperator.EQ, ISO_RSQL_VISITOR.resolveDates(filterSpec.getValue()))});
//    }
  }

}
