package ca.gc.aafc.collection.api.entities;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

import ca.gc.aafc.dina.entity.DinaEntity;
import ca.gc.aafc.dina.service.OnUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder
@NaturalIdCache
public class Transaction implements DinaEntity {

  public enum Direction { IN, OUT }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull(groups = OnUpdate.class)
  @Column(name = "uuid", unique = true)
  private UUID uuid;

  @NotBlank
  @Size(max = 50)
  @Column(name = "_group")
  private String group;

  @NotNull
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Enumerated(EnumType.STRING)
  private Direction materialDirection;

  @Size(max = 50)
  private String transactionNumber;

  private List<String> otherIdentifiers;

  @Generated(event = EventType.INSERT)
  private Boolean materialToBeReturned;

  @Size(max = 50)
  private String transactionType;

  @Size(max = 50)
  private String status;

  @Size(max = 1000)
  private String purpose;

  @PastOrPresent
  private LocalDate openedDate;
  @PastOrPresent
  private LocalDate closedDate;

  private LocalDate dueDate;

  @Size(max = 20_000)
  private String remarks;

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  @Valid
  private Shipment shipment;

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  @Valid
  @Builder.Default
  private List<AgentRolesWithDate> agentRoles = List.of();

  @Column(columnDefinition = "uuid[]")
  private List<UUID> materialSamples = List.of();

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  @NotNull
  @Builder.Default
  private Map<String, String> managedAttributes = new HashMap<>();

  @Column(name = "attachment", columnDefinition = "uuid[]")
  @Builder.Default
  private List<UUID> attachment = List.of();

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(event = EventType.INSERT)
  private OffsetDateTime createdOn;

}
