package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.dto.MaterialSampleHierarchyObject;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.UniqueElements;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
@Table(name = "material_sample")
public class MaterialSample extends AbstractMaterialSample {

  public enum MaterialSampleType {
    WHOLE_ORGANISM,
    CULTURE_STRAIN,
    ORGANISM_PART,
    MIXED_ORGANISMS,
    MOLECULAR_SAMPLE;

    /**
     * More lenient version of {@link #valueOf(String)}.
     * Case insensitive and returning Optional instead of throwing exceptions.
     * @param text
     * @return
     */
    public static Optional<MaterialSampleType> fromString(String text) {
      for (MaterialSampleType curr : values()) {
        if (text.equalsIgnoreCase(curr.toString())) {
          return Optional.of(curr);
        }
      }
      return Optional.empty();
    }
  }

  public static final String TABLE_NAME = "material_sample";
  public static final String ID_COLUMN_NAME = "id";
  public static final String UUID_COLUMN_NAME = "uuid";
  public static final String PARENT_ID_COLUMN_NAME = "parent_material_sample_id";
  public static final String NAME_COLUMN_NAME = "material_sample_name";

  public static final String CHILDREN_COL_NAME = "materialSampleChildren";
  public static final String HIERARCHY_PROP_NAME = "hierarchy";

  // Relationships property name
  public static final String ORGANISM_PROP_NAME = "organism";
  public static final String PROJECTS_PROP_NAME = "projects";
  public static final String ASSEMBLAGES_PROP_NAME = "assemblages";
  public static final String STORAGE_UNIT_USAGE_PROP_NAME = "storageUnitUsage";
  public static final String COLLECTION_PROP_NAME = "collection";
  public static final String COLLECTING_EVENT_PROP_NAME = "collectingEvent";
  public static final String PREPARATION_PROTOCOL_PROP_NAME = "preparationProtocol";
  public static final String PREPARATION_TYPE_PROP_NAME = "preparationType";
  public static final String PREPARATION_METHOD_PROP_NAME = "preparationMethod";

  @Version
  private int version;

  @ManyToOne(fetch = FetchType.LAZY)
  private Collection collection;

  @ManyToOne(fetch = FetchType.LAZY)
  @ToString.Exclude
  private CollectingEvent collectingEvent;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_material_sample_id")
  @ToString.Exclude
  private MaterialSample parentMaterialSample;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_material_sample_id", referencedColumnName = "id", insertable = false, updatable = false)
  private List<ImmutableMaterialSample> materialSampleChildren = List.of();
 
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "sample_project", 
    joinColumns = { @JoinColumn(name = "material_sample_id") }, 
    inverseJoinColumns = { @JoinColumn(name = "project_id") }
  )
  @OrderColumn(name = "pos")
  private List<Project> projects = List.of();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          name = "material_sample_assemblage",
          joinColumns = { @JoinColumn(name = "material_sample_id") },
          inverseJoinColumns = { @JoinColumn(name = "assemblage_id") }
  )
  @OrderColumn(name = "pos")
  private List<Assemblage> assemblages = List.of();

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "material_sample_id")
  @OrderColumn(name = "pos")
  private List<Organism> organism = List.of();

  @OneToMany(mappedBy = "sample", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
  @Valid
  private List<Association> associations = new ArrayList<>();

  public void setAssociations(List<Association> associations) {
    if (associations == null) {
      this.associations = null;
      return;
    }

    if (this.associations == null) {
      this.associations = new ArrayList<>();
    }
    this.associations.clear();
    if (CollectionUtils.isNotEmpty(associations)) {
      this.associations.addAll(associations);
    }
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "storage_unit_usage_id")
  private StorageUnitUsage storageUnitUsage;

  @Transient
  @DiffIgnore
  private List<MaterialSampleHierarchyObject> hierarchy;

  @Transient
  private String targetOrganismPrimaryScientificName;

  @Transient
  private Map<String, String> targetOrganismPrimaryClassification;

  @Transient
  private String effectiveScientificName;

  @Size(max = 50)
  private String materialSampleState;

  private LocalDate stateChangedOn;

  @Size(max = 1000)
  private String stateChangeRemarks;

  @Size(max = 1000)
  private String materialSampleRemarks;

  @Size(max = 50)
  private String sourceSet;

  @Type(type = "list-array")
  @Column(name = "prepared_by", columnDefinition = "uuid[]")
  @UniqueElements
  private List<UUID> preparedBy = List.of();

  @Type(type = "list-array")
  @Column(name = "attachment", columnDefinition = "uuid[]")
  @UniqueElements
  private List<UUID> attachment = List.of();

  @ManyToOne(fetch = FetchType.LAZY)
  private PreparationType preparationType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "preparation_method_id")
  private PreparationMethod preparationMethod;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "protocol_id")
  private Protocol preparationProtocol;

}
