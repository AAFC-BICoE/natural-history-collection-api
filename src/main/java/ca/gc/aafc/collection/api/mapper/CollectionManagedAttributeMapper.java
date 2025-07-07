package ca.gc.aafc.collection.api.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

@Mapper
public interface CollectionManagedAttributeMapper extends DinaMapperV2<CollectionManagedAttributeDto, CollectionManagedAttribute> {
  CollectionManagedAttributeMapper INSTANCE = Mappers.getMapper(CollectionManagedAttributeMapper.class);

  CollectionManagedAttributeDto toDto(CollectionManagedAttribute entity, @Context Set<String> provided, @Context String scope);

  CollectionManagedAttribute toEntity(CollectionManagedAttributeDto dto, @Context Set<String> provided, @Context String scope);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget CollectionManagedAttribute entity, CollectionManagedAttributeDto dto, @Context Set<String> provided, @Context String scope);
}
