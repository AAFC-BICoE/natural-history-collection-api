package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.dina.entity.DinaEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AccessLevel;
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
public class CollectorGroup implements DinaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(unique = true)
  private UUID uuid;

  @Column(insertable = false, updatable = false)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(updatable = false)
  private String createdBy;

  @NotNull
  @Column(unique = true)
  private String name;  

  private UUID[] agentIdentifiers;

}
