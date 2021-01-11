package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.dina.entity.DinaEntity;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@Builder
@Setter
@Getter
@RequiredArgsConstructor
@SuppressFBWarnings(justification = "ok for Hibernate Entity", value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
@NaturalIdCache
@TypeDef(
  name = "list-array",
  typeClass = ListArrayType.class
)
public class CollectingEvent implements DinaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(unique = true)
  private UUID uuid;

  // Not a Foreign Key, we would not use the UUID if it was.
  private UUID collectorGroupUuid;

  // Might not be the final choice to store lat/long
  private Double decimalLatitude;
  private Double decimalLongitude;

  private Integer coordinateUncertaintyInMeters;
  private String verbatimCoordinates;

  // Set by applyStartISOEventDateTime
  @Setter(AccessLevel.NONE)
  private LocalDateTime startEventDateTime;
  @Setter(AccessLevel.NONE)
  private Byte startEventDateTimePrecision;

  // Set by applyEndISOEventDateTime
  @Setter(AccessLevel.NONE)
  private LocalDateTime endEventDateTime;
  @Setter(AccessLevel.NONE)
  private Byte endEventDateTimePrecision;

  private String verbatimEventDateTime;
  private String verbatimCollectors;

  @Column(insertable = false, updatable = false)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(updatable = false)
  private String createdBy;

  @Type(type = "list-array")
  @Column(name = "collectors", columnDefinition = "uuid[]")
  private List<UUID> collectors;

  @Type(type = "list-array")
  @Column(name = "attachment", columnDefinition = "uuid[]")
  private List<UUID> attachment;

  /**
   * Method used to set startEventDateTime and startEventDateTimePrecision to ensure the 2 fields
   * are always in sync.
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
   *  Method used to set startEventDateTime and startEventDateTimePrecision to ensure the 2 fields
   * are always in sync.
   * @param endISOEventDateTime
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

}
