package ca.gc.aafc.collection.api.entities;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@Builder
@Setter
@Getter
@RequiredArgsConstructor
@SuppressFBWarnings(justification = "ok for Hibernate Entity", value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Table(name = "georeference_assertion")
public class GeoreferenceAssertion {

  public enum GeoreferenceVerificationStatus {
    GEOREFERENCING_NOT_POSSIBLE
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "index")
  private int index;

  @DecimalMin(value = "-90.0")
  @DecimalMax(value = "90.0")
  private Double dwcDecimalLatitude;
  @DecimalMin(value = "-180.0")
  @DecimalMax(value = "180.0")
  private Double dwcDecimalLongitude;
  private Integer dwcCoordinateUncertaintyInMeters;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  @EqualsAndHashCode.Exclude
  private CollectingEvent collectingEvent;

  private LocalDate dwcGeoreferencedDate;

  @Type(type = "list-array")
  @Column(name = "georeferenced_by", columnDefinition = "uuid[]")
  private List<UUID> georeferencedBy;

  @Size(max = 250)
  private String literalGeoreferencedBy;

  @Size(max = 100)
  private String dwcGeoreferenceProtocol;

  @Size(max = 150)
  private String dwcGeoreferenceSources;

  @Size(max = 250)
  private String dwcGeoreferenceRemarks;

  @Size(max = 25)
  private String dwcGeodeticDatum;

  @Type(type = "pgsql_enum")
  @Enumerated(EnumType.STRING)
  private GeoreferenceVerificationStatus dwcGeoreferenceVerificationStatus;

  @NotNull
  @Column(name = "is_primary")
  private Boolean isPrimary;

  public List<UUID> getGeoreferencedBy() {
    return CollectionUtils.isNotEmpty(georeferencedBy) ? georeferencedBy : Collections.emptyList();
  }
}
