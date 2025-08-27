package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.GeographicThesaurus;
import ca.gc.aafc.dina.datetime.ISODateTime;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.jsonapi.JsonApiImmutable;
import ca.gc.aafc.dina.mapper.CustomFieldAdapter;
import ca.gc.aafc.dina.mapper.DinaFieldAdapter;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;

import edu.umd.cs.findbugs.annotations.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.ShallowReference;
import org.javers.core.metamodel.annotation.TypeName;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@RelatedEntity(CollectingEvent.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonApiTypeForClass(CollectingEventDto.TYPENAME)
@TypeName(CollectingEventDto.TYPENAME)
public class CollectingEventDto implements JsonApiResource {

  public static final String TYPENAME = "collecting-event";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  @JsonApiImmutable(JsonApiImmutable.ImmutableOn.UPDATE)
  private int version;

  private String dwcFieldNumber;
  private String dwcRecordNumber;
  private String[] otherRecordNumbers;

  private String group;

  private String createdBy;
  private OffsetDateTime createdOn;

  private List<GeoreferenceAssertionDto> geoReferenceAssertions = List.of();

  // Read-only field
  @JsonApiImmutable(JsonApiImmutable.ImmutableOn.UPDATE)
  private Point<G2D> eventGeom;

  private String dwcVerbatimCoordinates;
  private String dwcRecordedBy;

  @IgnoreDinaMapping
  private String startEventDateTime;

  @IgnoreDinaMapping
  private String endEventDateTime;

  private String verbatimEventDateTime;

  @JsonApiExternalRelation(type = "person")
  private List<ExternalRelationDto> collectors = List.of();

  @JsonApiExternalRelation(type = "metadata")
  private List<ExternalRelationDto> attachment = List.of();

  @ShallowReference
  private CollectionMethodDto collectionMethod;

  @ShallowReference
  private ProtocolDto protocol;

  @ShallowReference
  @JsonApiRelation
  private ExpeditionDto expedition;

  private String dwcVerbatimLocality;

  private String host;

  /**
   * Map of Managed attribute key to value object.
   */
  private Map<String, String> managedAttributes = Map.of();

  private String dwcVerbatimLatitude;
  private String dwcVerbatimLongitude;
  private String dwcVerbatimCoordinateSystem;
  private String dwcVerbatimSRS;
  private String dwcVerbatimElevation;
  private String dwcVerbatimDepth;
  private String dwcCountry;
  private String dwcCountryCode;
  private String dwcStateProvince;
  private String habitat;
  private BigDecimal dwcMinimumElevationInMeters;
  private BigDecimal dwcMinimumDepthInMeters;
  private BigDecimal dwcMaximumElevationInMeters;
  private BigDecimal dwcMaximumDepthInMeters;
  private String substrate;
  private String remarks;
  private Boolean publiclyReleasable;
  private String notPubliclyReleasableReason;
  private String[] tags;

  private CollectingEvent.GeographicPlaceNameSource geographicPlaceNameSource;
  private GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail;

  private GeographicThesaurus geographicThesaurus;

  /**
   * Map of Managed attribute key to value object.
   */
  private Map<String, Map<String, String>> extensionValues = Map.of();

  @Override
  @JsonIgnore
  public String getJsonApiType() {
    return TYPENAME;
  }

  @Override
  @JsonIgnore
  public UUID getJsonApiId() {
    return uuid;
  }

//  @NoArgsConstructor
//  public static final class StartEventDateTimeAdapter
//    implements DinaFieldAdapter<CollectingEventDto, CollectingEvent, String, ISODateTime> {
//
//    @Override
//    public String toDTO(@Nullable ISODateTime isoDateTime) {
//      return isoDateTime == null ? null : isoDateTime.toString();
//    }
//
//    @Override
//    public ISODateTime toEntity(@Nullable String startEventDateTime) {
//      if (StringUtils.isBlank(startEventDateTime)) {
//        return null;
//      }
//      return ISODateTime.parse(startEventDateTime);
//    }
//
//    @Override
//    public Consumer<ISODateTime> entityApplyMethod(CollectingEvent entityRef) {
//      return entityRef::applyStartISOEventDateTime;
//    }
//
//    @Override
//    public Consumer<String> dtoApplyMethod(CollectingEventDto dtoRef) {
//      return dtoRef::setStartEventDateTime;
//    }
//
//    @Override
//    public Supplier<ISODateTime> entitySupplyMethod(CollectingEvent entityRef) {
//      return entityRef::supplyStartISOEventDateTime;
//    }
//
//    @Override
//    public Supplier<String> dtoSupplyMethod(CollectingEventDto dtoRef) {
//      return dtoRef::getStartEventDateTime;
//    }
//
//  }
//
//  @NoArgsConstructor
//  public static final class EndEventDateTimeAdapter
//    implements DinaFieldAdapter<CollectingEventDto, CollectingEvent, String, ISODateTime> {
//
//    @Override
//    public String toDTO(@Nullable ISODateTime isoDateTime) {
//      return isoDateTime == null ? null : isoDateTime.toString();
//    }
//
//    @Override
//    public ISODateTime toEntity(@Nullable String endEventDateTime) {
//      if (StringUtils.isBlank(endEventDateTime)) {
//        return null;
//      }
//      return ISODateTime.parse(endEventDateTime);
//    }
//
//    @Override
//    public Consumer<ISODateTime> entityApplyMethod(CollectingEvent entityRef) {
//      return entityRef::applyEndISOEventDateTime;
//    }
//
//    @Override
//    public Consumer<String> dtoApplyMethod(CollectingEventDto dtoRef) {
//      return dtoRef::setEndEventDateTime;
//    }
//
//    @Override
//    public Supplier<ISODateTime> entitySupplyMethod(CollectingEvent entityRef) {
//      return entityRef::supplyEndISOEventDateTime;
//    }
//
//    @Override
//    public Supplier<String> dtoSupplyMethod(CollectingEventDto dtoRef) {
//      return dtoRef::getEndEventDateTime;
//    }
//
//  }

}
