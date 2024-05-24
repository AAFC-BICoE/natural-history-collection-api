package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.dto.MaterialSampleHierarchyObject;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
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
  public static final String ORGANISM_PROP_NAME = "organism";

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
    this.associations.clear();
    if (CollectionUtils.isNotEmpty(associations)) {
      this.associations.addAll(associations);
    }
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "storage_unit_id")
  private StorageUnit storageUnit;

  @Min(value = 1)
  @Max(value = 255)
  @Column(name = "well_column")
  private Integer wellColumn;

  @Size(max = 2)
  @Pattern(regexp = "[a-zA-Z]")
  @Column(name = "well_row")
  private String wellRow;

  // calculated field
  @Transient
  private int cellNumber;

  @Transient
  @DiffIgnore
  private List<MaterialSampleHierarchyObject> hierarchy;

  @Transient
  private String targetOrganismPrimaryScientificName;

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
