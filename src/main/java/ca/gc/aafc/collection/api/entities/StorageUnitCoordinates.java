package ca.gc.aafc.collection.api.entities;

import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;

import ca.gc.aafc.dina.entity.DinaEntity;
import ca.gc.aafc.dina.entity.StorageGridLayout;
import ca.gc.aafc.dina.translator.NumberLetterTranslator;

@Entity
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
@Table
public class StorageUnitCoordinates implements DinaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  private UUID uuid;

  @Transient
  private String group;

  @Min(value = 1)
  @Max(value = 255)
  @Column(name = "well_column")
  private Integer wellColumn;

  @Size(max = 2)
  @Pattern(regexp = "[a-zA-Z]")
  @Column(name = "well_row")
  private String wellRow;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  // declared since the mapper needs it for consistency
  @Transient
  private Integer cellNumber;

  /**
   * Only set if storageUnit is not.
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "storage_unit_type_id")
  private StorageUnitType storageUnitType;

  /**
   * Only set if storageUnitType is not.
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "storage_unit_id")
  private StorageUnit storageUnit;

  public String getGroup() {
    if (storageUnit != null) {
      return storageUnit.getGroup();
    } else if (storageUnitType != null) {
      return storageUnitType.getGroup();
    } else {
      return null;
    }
  }

  public void setCellNumber(Integer i) {
    // nop-op, read only
  }

  /**
   * Calculated cell number (if possible to compute)
   * @return cell number or null
   */
  public Integer getCellNumber() {
    StorageUnitType sut = getEffectiveStorageUnitType();
    if (sut == null || sut.getGridLayoutDefinition() == null) {
      return null;
    }

    if(wellRow == null || wellColumn == null) {
      return null;
    }

    StorageGridLayout restriction = sut.getGridLayoutDefinition();
    return restriction.calculateCellNumber(NumberLetterTranslator.toNumber(wellRow), wellColumn);
  }

  /**
   * Returns the storageUnitType or the one from storageUnit depending which one
   * is set.
   * @return effective storageUnitType or null if none
   */
  public StorageUnitType getEffectiveStorageUnitType() {
    if (storageUnitType != null) {
      return storageUnitType;
    }

    if (storageUnit != null) {
      return storageUnit.getStorageUnitType();
    }
    return null;
  }

  public void setGroup(String group) {
    // no op
  }

}
