package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.ExtensionValue;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.CustomFieldAdapter;
import ca.gc.aafc.dina.mapper.DinaFieldAdapter;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.PatchStrategy;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import javax.annotation.Nullable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RelatedEntity(CollectingEvent.class)
@CustomFieldAdapter(adapters = {
  CollectingEventDto.StartEventDateTimeAdapter.class,
  CollectingEventDto.EndEventDateTimeAdapter.class})
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = CollectingEventDto.TYPENAME)
@TypeName(CollectingEventDto.TYPENAME)
public class CollectingEventDto {

  public static final String TYPENAME = "collecting-event";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private String group;

  private String createdBy;
  private OffsetDateTime createdOn;

  @JsonApiField(patchStrategy = PatchStrategy.SET)
  private List<GeoreferenceAssertionDto> geoReferenceAssertions =  new ArrayList<>();

  private String dwcVerbatimCoordinates;
  private String dwcRecordedBy;

  @IgnoreDinaMapping
  private String startEventDateTime;

  @IgnoreDinaMapping
  private String endEventDateTime;

  private String verbatimEventDateTime;

  @JsonApiExternalRelation(type = "person")
  @JsonApiRelation
  private List<ExternalRelationDto> collectors = new ArrayList<>();

  @JsonApiExternalRelation(type = "metadata")
  @JsonApiRelation
  private List<ExternalRelationDto> attachment = new ArrayList<>();

  @JsonApiRelation
  private CollectionMethodDto collectionMethod;

  private String dwcVerbatimLocality;

  private String host;

  /**
   * Map of Managed attribute key to value object.
   */
  @JsonApiField(patchStrategy = PatchStrategy.SET)
  private Map<String, String> managedAttributes = Map.of();

  private String dwcVerbatimLatitude;
  private String dwcVerbatimLongitude;
  private String dwcVerbatimCoordinateSystem;
  private String dwcVerbatimSRS;
  private String dwcVerbatimElevation;
  private String dwcVerbatimDepth;
  private String[] dwcOtherRecordNumbers;
  private String dwcRecordNumber;
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

  private List<ExtensionValue> extensionValues = new ArrayList<>();

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
