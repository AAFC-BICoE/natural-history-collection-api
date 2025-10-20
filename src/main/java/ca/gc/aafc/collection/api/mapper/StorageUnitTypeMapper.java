package ca.gc.aafc.collection.api.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

@Mapper
public interface StorageUnitTypeMapper extends DinaMapperV2<StorageUnitTypeDto, StorageUnitType> {

  StorageUnitTypeMapper INSTANCE = Mappers.getMapper(StorageUnitTypeMapper.class);

  StorageUnitTypeDto toDto(StorageUnitType entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  StorageUnitType toEntity(StorageUnitTypeDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget StorageUnitType entity, StorageUnitTypeDto dto,
                   @Context Set<String> provided, @Context String scope);
}
