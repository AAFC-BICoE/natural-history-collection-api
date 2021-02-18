package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.datetime.IsoDateTimeRsqlResolver;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.CollectingEventManagedAttribute;
import ca.gc.aafc.collection.api.entities.ManagedAttribute;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.CustomFieldAdapter;
import ca.gc.aafc.dina.mapper.DinaFieldAdapter;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RelatedEntity(CollectingEvent.class)
@CustomFieldAdapter(adapters = {
  CollectingEventDto.StartEventDateTimeAdapter.class,
  CollectingEventDto.EndEventDateTimeAdapter.class,
  CollectingEventDto.ManagedAttributesAdapter.class})
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = "collecting-event")
public class CollectingEventDto {

  @JsonApiId
  private UUID uuid;

  private String group;

  private String createdBy;
  private OffsetDateTime createdOn;

  private Double dwcDecimalLatitude;
  private Double dwcDecimalLongitude;

  private Integer dwcCoordinateUncertaintyInMeters;
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

  @JsonApiExternalRelation(type = "agent")
  @JsonApiRelation
  private List<ExternalRelationDto> dwcGeoreferencedBy = new ArrayList<>();

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @IgnoreDinaMapping(reason = "custom mapped")
  private List<CollectingEventManagedAttributeDto> managedAttributes = new ArrayList<>();

  private OffsetDateTime dwcGeoreferencedDate;
  private String dwcGeoreferenceSources;
  private String dwcVerbatimLatitude;
  private String dwcVerbatimLongitude;
  private String dwcVerbatimCoordinateSystem;
  private String dwcVerbatimSRS;
  private String dwcVerbatimElevation;
  private String dwcVerbatimDepth;
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

  public static class ManagedAttributesAdapter implements DinaFieldAdapter<
    CollectingEventDto,
    CollectingEvent,
    List<CollectingEventManagedAttributeDto>,
    List<CollectingEventManagedAttribute>> {

    @Override
    public List<CollectingEventManagedAttributeDto> toDTO(List<CollectingEventManagedAttribute> attributes) {
      if (CollectionUtils.isEmpty(attributes)) {
        return Collections.emptyList();
      }
      return attributes.stream()
        .map(ManagedAttributesAdapter::mapToDto)
        .collect(Collectors.toList());
    }

    private static CollectingEventManagedAttributeDto mapToDto(CollectingEventManagedAttribute e) {
      return CollectingEventManagedAttributeDto.builder()
        .assignedValue(e.getAssignedValue())
        .attributeId(e.getAttribute().getUuid())
        .name(e.getAttribute().getName())
        .build();
    }

    @Override
    public List<CollectingEventManagedAttribute> toEntity(List<CollectingEventManagedAttributeDto> attributes) {
      if (CollectionUtils.isEmpty(attributes)) {
        return Collections.emptyList();
      }
      return attributes.stream()
        .map(ManagedAttributesAdapter::mapToEntity)
        .collect(Collectors.toList());
    }

    private static CollectingEventManagedAttribute mapToEntity(CollectingEventManagedAttributeDto dto) {
      return CollectingEventManagedAttribute.builder()
        .attribute(ManagedAttribute.builder()
          .uuid(dto.getAttributeId())
          .name(dto.getName())
          .build())
        .assignedValue(dto.getAssignedValue())
        .build();
    }

    @Override
    public Consumer<List<CollectingEventManagedAttribute>> entityApplyMethod(CollectingEvent entityRef) {
      return entityRef::setManagedAttributes;
    }

    @Override
    public Consumer<List<CollectingEventManagedAttributeDto>> dtoApplyMethod(CollectingEventDto dtoRef) {
      return dtoRef::setManagedAttributes;
    }

    @Override
    public Supplier<List<CollectingEventManagedAttribute>> entitySupplyMethod(CollectingEvent entityRef) {
      return entityRef::getManagedAttributes;
    }

    @Override
    public Supplier<List<CollectingEventManagedAttributeDto>> dtoSupplyMethod(CollectingEventDto dtoRef) {
      return dtoRef::getManagedAttributes;
    }
  }

}
