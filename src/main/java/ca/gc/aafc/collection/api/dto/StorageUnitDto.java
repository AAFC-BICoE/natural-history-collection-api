package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.ImmutableStorageUnit;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.service.StorageHierarchicalObject;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.CustomFieldAdapter;
import ca.gc.aafc.dina.mapper.DinaFieldAdapter;
import ca.gc.aafc.dina.mapper.DinaMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Data
@RelatedEntity(StorageUnit.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = StorageUnitDto.TYPENAME)
@CustomFieldAdapter(adapters = StorageUnitDto.ImmutableStorageUnitAdapter.class)
public class StorageUnitDto {

  public static final String TYPENAME = "storage-unit";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String group;

  private String name;

  @JsonApiRelation
  private StorageUnitDto parentStorageUnit;


  private List<ImmutableStorageUnitDto> storageUnitChildren = new ArrayList<>();

  private List<StorageHierarchicalObject> hierarchy;

  @JsonApiRelation
  private StorageUnitTypeDto storageUnitType;

  public static class ImmutableStorageUnitAdapter implements
      DinaFieldAdapter<StorageUnitDto, StorageUnit, List<ImmutableStorageUnitDto>, List<ImmutableStorageUnit> > {
    private final DinaMapper<ImmutableStorageUnitDto, ImmutableStorageUnit> mapper;

    public ImmutableStorageUnitAdapter() {
      mapper = new DinaMapper<>(ImmutableStorageUnitDto.class);
    }

    @Override
    public List<ImmutableStorageUnitDto> toDTO(
        List<ImmutableStorageUnit> immutableStorageUnits) {
      if(immutableStorageUnits == null) {
        return null;
      }
      return immutableStorageUnits.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ImmutableStorageUnit> toEntity(
        List<ImmutableStorageUnitDto> immutableStorageUnitDtos) {
      // should not be called so no-op
      return null;
    }

    @Override
    public Consumer<List<ImmutableStorageUnit>> entityApplyMethod(StorageUnit entityRef) {
      // should not be called so no-op
      return l -> { };
    }

    @Override
    public Consumer<List<ImmutableStorageUnitDto>> dtoApplyMethod(StorageUnitDto dtoRef) {
      return dtoRef::setStorageUnitChildren;
    }

    @Override
    public Supplier<List<ImmutableStorageUnit>> entitySupplyMethod(StorageUnit entityRef) {
      return entityRef::getStorageUnitChildren;
    }

    @Override
    public Supplier<List<ImmutableStorageUnitDto>> dtoSupplyMethod(StorageUnitDto dtoRef) {
      return dtoRef::getStorageUnitChildren;
    }
  }
}
