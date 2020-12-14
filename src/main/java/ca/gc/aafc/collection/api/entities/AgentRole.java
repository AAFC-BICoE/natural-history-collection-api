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
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

@Entity
@TypeDefs({ @TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class),
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class) })
@AllArgsConstructor
@Builder
@Setter
@Getter
@RequiredArgsConstructor
@SuppressFBWarnings(justification = "ok for Hibernate Entity", value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
@NaturalIdCache
public class AgentRole implements DinaEntity {

  public enum AgentRoleType {COLLECTING_EVENT};  

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
  @Type(type = "pgsql_enum")
  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private AgentRoleType agentRoleType;
  
  @Type(type = "jsonb")
  @Column(name = "name", columnDefinition = "jsonb")
  @NotEmpty  
  private Map<String, String> name;

}
