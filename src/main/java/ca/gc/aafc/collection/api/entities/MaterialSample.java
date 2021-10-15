package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.dina.dto.HierarchicalObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
@Table(name = "material_sample")
public class MaterialSample extends AbstractMaterialSample {

  public static final String TABLE_NAME = "material_sample";
  public static final String ID_COLUMN_NAME = "id";
  public static final String UUID_COLUMN_NAME = "uuid";
  public static final String PARENT_ID_COLUMN_NAME = "parent_material_sample_id";
  public static final String NAME_COLUMN_NAME = "material_sample_name";

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

  @OneToMany(mappedBy = "sample", cascade = CascadeType.PERSIST)
  private List<Association> associations = new ArrayList<>();

  @ManyToOne
  @ToString.Exclude
  private PreparationType preparationType;

  @ManyToOne
  @ToString.Exclude
  private MaterialSampleType materialSampleType;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "storage_unit_id")
  private StorageUnit storageUnit;

  @Transient
  private List<HierarchicalObject> hierarchy;

  @Size(max = 50)
  private String materialSampleState;

  @Size(max = 250)
  private String materialSampleRemarks;

}
