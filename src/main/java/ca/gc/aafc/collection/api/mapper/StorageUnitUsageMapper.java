package ca.gc.aafc.collection.api.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.dto.StorageUnitUsageDto;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.collection.api.entities.StorageUnitUsage;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

@Mapper
public interface StorageUnitUsageMapper extends DinaMapperV2<StorageUnitUsageDto, StorageUnitUsage> {

  StorageUnitUsageMapper INSTANCE = Mappers.getMapper(StorageUnitUsageMapper.class);

  StorageUnitUsageDto toDto(StorageUnitUsage entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "storageUnit", ignore = true)
  @Mapping(target = "storageUnitType", ignore = true)
  @Mapping(target = "cellNumber", ignore = true) //calculated field
  StorageUnitUsage toEntity(StorageUnitUsageDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "storageUnit", ignore = true)
  @Mapping(target = "storageUnitType", ignore = true)
  @Mapping(target = "cellNumber", ignore = true) //calculated field
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget StorageUnitUsage entity, StorageUnitUsageDto dto,
                   @Context Set<String> provided, @Context String scope);

  default StorageUnitTypeDto toDto(StorageUnitType entity, @Context Set<String> provided, @Context String scope) {
    return entity == null ? null : toStorageUnitTypeDto(entity, provided, "storageUnitType");
  }

  default StorageUnitDto toDto(StorageUnit entity, @Context Set<String> provided, @Context String scope) {
    return entity == null ? null : toStorageUnitDto(entity, provided, "storageUnit");
  }

  StorageUnitTypeDto toStorageUnitTypeDto(StorageUnitType entity, Set<String> provided, String scope);

  @Mapping(target = "storageUnitChildren", ignore = true)
  @Mapping(target = "parentStorageUnit", ignore = true)
  StorageUnitDto toStorageUnitDto(StorageUnit entity, Set<String> provided, String scope);

}
