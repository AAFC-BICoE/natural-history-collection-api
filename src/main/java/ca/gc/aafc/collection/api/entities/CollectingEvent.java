package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import ca.gc.aafc.dina.entity.DinaEntity;
import ca.gc.aafc.dina.datetime.ISODateTime;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Entity
@AllArgsConstructor
@Builder
@Setter
@Getter
@RequiredArgsConstructor
@NaturalIdCache
public class CollectingEvent implements DinaEntity {

  public enum GeographicPlaceNameSource {
    OSM
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(unique = true)
  private UUID uuid;

  @Version
  private int version;

  @Size(max = 50)
  private String dwcFieldNumber;

  @Size(max = 50)
  private String dwcRecordNumber;

  @Type(type = "string-array")
  private String[] otherRecordNumbers;

  @NotBlank
  @Column(name = "_group")
  private String group;

  @Type(type = "jsonb")
  @Builder.Default
  private List<GeoreferenceAssertionDto> geoReferenceAssertions =  new ArrayList<>();

  private String dwcVerbatimCoordinates;

  @Size(max = 250)
  private String dwcRecordedBy;

  // Set by applyStartISOEventDateTime
  @Setter(AccessLevel.NONE)
  @Past
  private LocalDateTime startEventDateTime;
  @Setter(AccessLevel.NONE)
  private Byte startEventDateTimePrecision;

  // Set by applyEndISOEventDateTime
  @Setter(AccessLevel.NONE)
  @Past
  private LocalDateTime endEventDateTime;
  @Setter(AccessLevel.NONE)
  private Byte endEventDateTimePrecision;

  private String verbatimEventDateTime;

  @Column(insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(updatable = false)
  private String createdBy;

  @Type(type = "list-array")
  @Column(name = "collectors", columnDefinition = "uuid[]")
  @UniqueElements
  private List<UUID> collectors = new ArrayList<>();

  @Type(type = "list-array")
  @Column(name = "attachment", columnDefinition = "uuid[]")
  @UniqueElements
  private List<UUID> attachment = new ArrayList<>();

  @Size(max = 250)
  private String dwcVerbatimLocality;

  @Size(max = 25)
  private String dwcVerbatimLatitude;

  @Size(max = 25)
  private String dwcVerbatimLongitude;

  @Size(max = 50)
  private String dwcVerbatimCoordinateSystem;

  @Size(max = 50)
  private String dwcVerbatimSRS;

  @Size(max = 25)
  private String dwcVerbatimElevation;

  @Size(max = 25)
  private String dwcVerbatimDepth;

  @Size(max = 100)
  private String dwcCountry;
  @Size(max = 2)
  private String dwcCountryCode;
  @Size(max = 100)
  private String dwcStateProvince;

  @Size(max = 500)
  private String habitat;

  @Min(value = 0)
  @Max(value = 15000)
  @Column(precision = 7, scale = 2)
  @Digits(integer = 5, fraction = 2)
  private BigDecimal dwcMinimumElevationInMeters;

  @Min(value = 0)
  @Max(value = 15000)
  @Column(precision = 7, scale = 2)
  @Digits(integer = 5, fraction = 2)
  private BigDecimal dwcMinimumDepthInMeters;

  @Min(value = 0)
  @Max(value = 15000)
  @Column(precision = 7, scale = 2)
  @Digits(integer = 5, fraction = 2)
  private BigDecimal dwcMaximumElevationInMeters;

  @Min(value = 0)
  @Max(value = 15000)
  @Column(precision = 7, scale = 2)
  @Digits(integer = 5, fraction = 2)
  private BigDecimal dwcMaximumDepthInMeters;

  @Size(max = 255)
  private String substrate;

  @Size(max = 1000)
  private String remarks;

  /** Map of Managed attribute key to value object. */
  @Type(type = "jsonb")
  @NotNull
  @Builder.Default
  private Map<String, String> managedAttributes = Map.of();

  @Type(type = "pgsql_enum")
  @Enumerated(EnumType.STRING)
  private GeographicPlaceNameSource geographicPlaceNameSource;

  @Type(type = "jsonb")
  @Column(name = "geographic_place_name_source_details", columnDefinition = "jsonb")
  @Valid
  private GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail;

  @Type(type = "jsonb")
  @Column(name = "geographic_thesaurus", columnDefinition = "jsonb")
  @Valid
  private GeographicThesaurus geographicThesaurus;

  @Size(max = 250)
  private String host;

  @ManyToOne
  private CollectionMethod collectionMethod;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "protocol_id")
  private Protocol protocol;

  private Boolean publiclyReleasable;

  @Size(max = 500)
  private String notPubliclyReleasableReason;

  @Type(type = "string-array")
  private String[] tags;

  // Field calculated by CollectingEventService
  private Point<G2D> eventGeom;

  @Type(type = "jsonb")
  @Column(name = "extension_values", columnDefinition = "jsonb")
  private Map<String, Map<String, String>> extensionValues = Map.of();

  /**
   * Method used to set startEventDateTime and startEventDateTimePrecision to ensure the 2 fields are always
   * in sync.
   *
   * @param startISOEventDateTime the startEventDate time as ISODateTime or null.
   */
  public void applyStartISOEventDateTime(ISODateTime startISOEventDateTime) {
    if (startISOEventDateTime == null) {
      startEventDateTime = null;
      startEventDateTimePrecision = null;
    } else {
      startEventDateTime = startISOEventDateTime.getLocalDateTime();
      startEventDateTimePrecision = startISOEventDateTime.getFormat().getPrecision();
    }
  }

  public ISODateTime supplyStartISOEventDateTime() {
    if (startEventDateTime == null || startEventDateTimePrecision == null) {
      return null;
    }

    return ISODateTime.builder().localDateTime(startEventDateTime)
      .format(ISODateTime.Format.fromPrecision(startEventDateTimePrecision).orElse(null))
      .build();
  }

  /**
   * Method used to set startEventDateTime and startEventDateTimePrecision to ensure the 2 fields are always
   * in sync.
   *
   * @param endISOEventDateTime the ISODateTime
   */
  public void applyEndISOEventDateTime(ISODateTime endISOEventDateTime) {
    if (endISOEventDateTime == null) {
      endEventDateTime = null;
      endEventDateTimePrecision = null;
    } else {
      endEventDateTime = endISOEventDateTime.getLocalDateTime();
      endEventDateTimePrecision = endISOEventDateTime.getFormat().getPrecision();
    }
  }

  public ISODateTime supplyEndISOEventDateTime() {

    if (endEventDateTime == null || endEventDateTimePrecision == null) {
      return null;
    }

    return ISODateTime.builder().localDateTime(endEventDateTime)
      .format(ISODateTime.Format.fromPrecision(endEventDateTimePrecision).orElse(null))
      .build();
  }

  public Optional<GeoreferenceAssertionDto> getPrimaryAssertion() {
    if (CollectionUtils.isEmpty(this.getGeoReferenceAssertions())) {
      return Optional.empty();
    }
    return this.getGeoReferenceAssertions()
      .stream()
      .filter(geo -> BooleanUtils.isTrue(geo.getIsPrimary()))
      .findFirst();
  }

}
