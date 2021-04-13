package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.dina.entity.DinaEntity;
import ca.gc.aafc.dina.service.OnCreate;
import ca.gc.aafc.dina.service.OnUpdate;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity(name = "collecting_event_managed_attribute")
@AllArgsConstructor
@Builder
@Setter
@Getter
@RequiredArgsConstructor
@SuppressFBWarnings(justification = "ok for Hibernate Entity", value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@NaturalIdCache
public class CollectingEventManagedAttribute implements DinaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @Null(groups = OnCreate.class)
  @NotNull(groups = OnUpdate.class)
  @Column(name = "uuid", unique = true)
  private UUID uuid;

  @ManyToOne
  @JoinColumn(name = "event_id")
  private CollectingEvent event;

  @ManyToOne
  @JoinColumn(name = "managed_attribute_id")
  private ManagedAttribute managedAttribute;

  @NotBlank
  private String assignedValue;

  @Override
  public String getCreatedBy() {
    return null;
  }

  @Override
  public OffsetDateTime getCreatedOn() {
    return null;
  }
}
