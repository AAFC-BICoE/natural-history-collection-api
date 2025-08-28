package ca.gc.aafc.collection.api.dto;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.ShallowReference;
import org.javers.core.metamodel.annotation.TypeName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.collection.api.entities.GeographicThesaurus;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.jsonapi.JsonApiImmutable;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  private String startEventDateTime;
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
}
