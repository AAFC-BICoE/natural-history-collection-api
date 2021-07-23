package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import ca.gc.aafc.dina.entity.DinaEntity;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.locationtech.jts.geom.Geometry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
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
@SuppressFBWarnings(justification = "ok for Hibernate Entity", value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@NaturalIdCache
@TypeDef(
  name = "list-array",
  typeClass = ListArrayType.class
)
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
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
  private List<UUID> collectors = new ArrayList<>();

  @Type(type = "list-array")
  @Column(name = "attachment", columnDefinition = "uuid[]")
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

  @Type(type = "string-array")
  private String[] dwcOtherRecordNumbers;

  @Size(max = 50)
  private String dwcRecordNumber;

  @Size(max = 100)
  private String dwcCountry;
  @Size(max = 2)
  private String dwcCountryCode;
  @Size(max = 100)
  private String dwcStateProvince;

  @Size(max = 500)
  private String habitat;

  @Min(value = 0)
  private Integer dwcMinimumElevationInMeters;

  @Min(value = 0)
  private Integer dwcMinimumDepthInMeters;

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

  private Geometry eventGeom;

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
