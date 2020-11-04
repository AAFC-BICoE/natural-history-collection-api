package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.dina.entity.DinaEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@Builder
@Setter
@Getter
@RequiredArgsConstructor
@SuppressFBWarnings(justification = "ok for Hibernate Entity", value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
@NaturalIdCache
public class CollectingEvent implements DinaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(unique = true)
  private UUID uuid;

  // Might not be the final choice to store lat/long
  private Double decimalLatitude;
  private Double decimalLongitude;

  private Integer coordinateUncertaintyInMeters;
  private String verbatimCoordinates;

  // ideally, I would like to remove this field completely and rename setStartISOEventDateTime
  // to applyStartISOEventDateTime
  @Transient
  private ISODateTime startISOEventDateTime;

  // Setters for startEventDateTime and startEventDateTimePrecision should only be used
  // by Hibernate to set the data from database. Otherwise startISOEventDateTime should be used.
  private LocalDateTime startEventDateTime;
  private Byte startEventDateTimePrecision;

  private LocalDateTime endEventDateTime;
  private Byte endEventDateTimePrecision;

  private String verbatimEventDateTime;

  @Column(insertable = false, updatable = false)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(updatable = false)
  private String createdBy;

  public void setStartISOEventDateTime(ISODateTime startISOEventDateTime) {
    if (startISOEventDateTime == null) {
      startEventDateTime = null;
      startEventDateTimePrecision = null;
    } else {
      startEventDateTime = startISOEventDateTime.getLocalDateTime();
      startEventDateTimePrecision = startISOEventDateTime.getFormat().getPrecision();
    }
  }

}
