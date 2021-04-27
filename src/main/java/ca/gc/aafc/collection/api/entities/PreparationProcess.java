package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.dina.entity.DinaEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
@SuppressFBWarnings(justification = "ok for Hibernate Entity", value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@NaturalIdCache
public class PreparationProcess implements DinaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @Column(name = "uuid", unique = true)
  private UUID uuid;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @NotNull
  @Column(name = "agent_id")
  private UUID agentId;

  @OneToOne
  @NotNull
  @JoinColumn(name = "preparation_process_definition_id", referencedColumnName = "id", unique = true)
  private PreparationProcessDefinition preparationProcessDefinition;

  @OneToOne
  @NotNull
  @JoinColumn(name = "source_physical_entity_id", referencedColumnName = "id", unique = true)
  private PhysicalEntity sourcePhysicalEntity;

  @Past
  private LocalDateTime startDateTime;

  @Past
  private LocalDateTime endDateTime;
}
