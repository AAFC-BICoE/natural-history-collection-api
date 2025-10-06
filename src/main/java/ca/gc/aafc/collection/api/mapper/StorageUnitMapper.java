package ca.gc.aafc.collection.api.mapper;

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
import ca.gc.aafc.collection.api.dto.StorageUnitUsageDto;
import ca.gc.aafc.collection.api.entities.ImmutableStorageUnit;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.collection.api.entities.StorageUnitUsage;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

import java.util.Set;

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

  default ImmutableStorageUnitDto toDto(ImmutableStorageUnit entity, @Context Set<String> provided, @Context String scope) {
    return entity == null ? null : toImmutableStorageUnitDto(entity, provided, "storageUnitChildren");
  }

  default StorageUnitTypeDto toDto(StorageUnitType entity, @Context Set<String> provided, @Context String scope) {
    return entity == null ? null : toStorageUnitUsageDto(entity, provided, "storageUnitType");
  }

  StorageUnitTypeDto toStorageUnitUsageDto(StorageUnitType entity, Set<String> provided, String scope);

  @Mapping(target = "id", ignore = true)
  ImmutableStorageUnitDto toImmutableStorageUnitDto(ImmutableStorageUnit entity, Set<String> provided, String scope);

}
