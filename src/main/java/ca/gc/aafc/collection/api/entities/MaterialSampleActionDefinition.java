package ca.gc.aafc.collection.api.entities;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Map;
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

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import ca.gc.aafc.dina.entity.DinaEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@RequiredArgsConstructor
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
@SuppressFBWarnings(justification = "ok for Hibernate Entity", value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
@NaturalIdCache
public class MaterialSampleActionDefinition implements DinaEntity {

  public enum ActionType {
    SPLIT, MERGE, ADD
  }

  public enum MaterialSampleFormComponent {
    COLLECTING_EVENT,
    MATERIAL_SAMPLE,
    ACQUISITION_EVENT
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(name = "uuid", unique = true)
  private UUID uuid;

  @NotBlank
  private String name;

  @NotBlank
  @Column(name = "prep_group")
  private String group;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @NotNull
  @Type(type = "pgsql_enum")
  @Enumerated(EnumType.STRING)
  @Column(name = "action_type")
  private ActionType actionType;
  
  /** Map of form names to form templates */
  @NotNull
  @Type(type = "jsonb")
  private Map<MaterialSampleFormComponent, FormTemplate> formTemplates;

  /** Form template config and default values. */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FormTemplate {
    @NotNull
    private Boolean allowNew;

    @NotNull
    private Boolean allowExisting;

    /** Map of field names to template field config. */
    @NotNull
    private Map<String, TemplateField> templateFields;
  }

  /** Configures one field in a form template. */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TemplateField {
    @NotNull
    private Boolean enabled;

    @NotNull
    private Serializable defaultValue;
  }
}
