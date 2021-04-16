package ca.gc.aafc.collection.api.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import ca.gc.aafc.dina.entity.DinaEntity;
import ca.gc.aafc.dina.service.OnCreate;
import ca.gc.aafc.dina.service.OnUpdate;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@TypeDefs({
  @TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class),
  @TypeDef(name = "string-array", typeClass = StringArrayType.class)})
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
@SuppressFBWarnings(justification = "ok for Hibernate Entity", value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@NaturalIdCache
public class ManagedAttribute implements DinaEntity {

  public enum ManagedAttributeType {
    INTEGER, STRING
  }

  public enum ManagedAttributeComponent {
    COLLECTING_EVENT
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull(groups = OnUpdate.class)
  @Column(name = "uuid", unique = true)
  private UUID uuid;

  @NotBlank
  @Size(max = 50)
  @Column(updatable = false)
  private String name;

  @Null(groups = OnCreate.class)
  @NotBlank(groups = OnUpdate.class)
  @Size(max = 50)
  @Column(updatable = false)
  private String key;

  @NotNull
  @Type(type = "pgsql_enum")
  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private ManagedAttributeType managedAttributeType;

  @NotNull
  @Type(type = "pgsql_enum")
  @Enumerated(EnumType.STRING)
  @Column(name = "component")
  private ManagedAttributeComponent managedAttributeComponent;

  @Type(type = "string-array")
  @Column(columnDefinition = "text[]")
  private String[] acceptedValues;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(name = "created_by", updatable = false)
  private String createdBy;

}
