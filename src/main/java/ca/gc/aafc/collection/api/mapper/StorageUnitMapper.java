package ca.gc.aafc.collection.api.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.ImmutableStorageUnitDto;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.entities.ImmutableStorageUnit;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface StorageUnitMapper extends DinaMapperV2<StorageUnitDto, StorageUnit> {

  StorageUnitMapper INSTANCE = Mappers.getMapper(StorageUnitMapper.class);

  StorageUnitDto toDto(StorageUnit entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "storageUnitChildren", ignore = true)
  @Mapping(target = "hierarchy", ignore = true)
  @Mapping(target = "parentStorageUnit", ignore = true)
  @Mapping(target = "storageUnitType", ignore = true)
  StorageUnit toEntity(StorageUnitDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "storageUnitChildren", ignore = true)
  @Mapping(target = "hierarchy", ignore = true)
  @Mapping(target = "parentStorageUnit", ignore = true)
  @Mapping(target = "storageUnitType", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget StorageUnit entity, StorageUnitDto dto,
                   @Context Set<String> provided, @Context String scope);

  default List<ImmutableStorageUnitDto> toDto(List<ImmutableStorageUnit> entities, @Context Set<String> provided, @Context String scope) {

    if (CollectionUtils.isEmpty(entities)) {
      return null;
    }
    return entities.stream().map(this::toImmutableStorageUnitDto)
      .collect(Collectors.toList());
  }

  default StorageUnitTypeDto toDto(StorageUnitType entity, @Context Set<String> provided, @Context String scope) {
    return entity == null ? null : toStorageUnitUsageDto(entity, provided, "storageUnitType");
  }

  StorageUnitTypeDto toStorageUnitUsageDto(StorageUnitType entity, Set<String> provided, String scope);

  // Optional fields
  /**
   * Considered an optional field so no scope is used.
   * @param entity
   * @return
   */
  @Mapping(target = "id", source = "uuid")
  ImmutableStorageUnitDto toImmutableStorageUnitDto(ImmutableStorageUnit entity);
}
