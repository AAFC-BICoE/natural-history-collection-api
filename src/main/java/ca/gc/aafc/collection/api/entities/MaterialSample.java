package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.dina.dto.HierarchicalObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Type;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    ORGANISM_PART, 
    MIXED_ORGANISMS,
    MOLECULAR_SAMPLE
  }

  public static final String TABLE_NAME = "material_sample";
  public static final String ID_COLUMN_NAME = "id";
  public static final String UUID_COLUMN_NAME = "uuid";
  public static final String PARENT_ID_COLUMN_NAME = "parent_material_sample_id";
  public static final String NAME_COLUMN_NAME = "material_sample_name";

  @Version
  private int version;

  @ManyToOne
  private Collection collection;

  @ManyToOne(fetch = FetchType.LAZY)
  @ToString.Exclude
  private CollectingEvent collectingEvent;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_material_sample_id")
  @ToString.Exclude
  private MaterialSample parentMaterialSample;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_material_sample_id", referencedColumnName = "id", insertable = false, updatable = false)
  private List<ImmutableMaterialSample> materialSampleChildren = new ArrayList<>();
 
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "sample_project", 
    joinColumns = { @JoinColumn(name = "material_sample_id") }, 
    inverseJoinColumns = { @JoinColumn(name = "project_id") }
  )
  @OrderColumn(name = "pos")
  private List<Project> projects = new ArrayList<>();

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "material_sample_id")
  @OrderColumn(name = "pos")
  private List<Organism> organism = new ArrayList<>();

  @OneToMany(mappedBy = "sample", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
  @Valid
  private List<Association> associations = new ArrayList<>();

  public void setAssociations(List<Association> associations) {
    this.associations.clear();
    if (CollectionUtils.isNotEmpty(associations)) {
      this.associations.addAll(associations);
    }
  }

  @ManyToOne
  @ToString.Exclude
  private PreparationType preparationType;

  @NotNull
  @Type(type = "pgsql_enum")
  @Enumerated(EnumType.STRING)
  private MaterialSampleType materialSampleType;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "storage_unit_id")
  private StorageUnit storageUnit;

  @Transient
  @DiffIgnore
  private List<HierarchicalObject> hierarchy;

  @Size(max = 50)
  private String materialSampleState;

  private LocalDate stateChangedOn;

  @Size(max = 1000)
  private String stateChangeRemarks;

  @Size(max = 1000)
  private String materialSampleRemarks;

  @Type(type = "list-array")
  @Column(name = "attachment", columnDefinition = "uuid[]")
  private List<UUID> attachment = new ArrayList<>();

  @Type(type = "list-array")
  @Column(name = "preparation_attachment", columnDefinition = "uuid[]")
  private List<UUID> preparationAttachment = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "acquisition_event_id")
  private AcquisitionEvent acquisitionEvent;
}
