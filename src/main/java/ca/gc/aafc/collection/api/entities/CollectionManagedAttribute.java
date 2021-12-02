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
import javax.validation.constraints.Size;

import ca.gc.aafc.dina.entity.ManagedAttribute;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity(name = "managed_attribute")
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
public class CollectionManagedAttribute implements ManagedAttribute {

  public enum ManagedAttributeComponent {
    COLLECTING_EVENT,
    MATERIAL_SAMPLE,
    DETERMINATION
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(name = "uuid", unique = true)
  private UUID uuid;

  @NotBlank
  @Size(max = 50)
  @Column(updatable = false)
  private String name;

  @NotBlank
  @Column(name = "_group")
  @Size(max = 50)
  private String group;

  @NotBlank
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
