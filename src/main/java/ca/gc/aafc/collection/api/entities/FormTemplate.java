package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.dina.entity.DinaEntity;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
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
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * FormTemplate is used to store client (e.g. ui) customizations.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@RequiredArgsConstructor
@NaturalIdCache
public class FormTemplate implements DinaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(name = "uuid", unique = true)
  private UUID uuid;

  @NotBlank
  @Size(max = 100)
  private String name;

  @NotBlank
  @Size(max = 50)
  @Column(name = "_group")
  private String group;

  @NotNull
  private Boolean restrictToCreatedBy = false;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @NotNull
  @Type(type = "jsonb")
  private Map<String, Object> viewConfiguration;

}
