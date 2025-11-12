package ca.gc.aafc.collection.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.CollectionMethodDto;
import ca.gc.aafc.collection.api.entities.CollectionMethod;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

import java.util.Set;

@Mapper
public interface CollectionMethodMapper extends DinaMapperV2<CollectionMethodDto, CollectionMethod> {

  CollectionMethodMapper INSTANCE = Mappers.getMapper(CollectionMethodMapper.class);

  CollectionMethodDto toDto(CollectionMethod entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  CollectionMethod toEntity(CollectionMethodDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget CollectionMethod entity, CollectionMethodDto dto,
                   @Context Set<String> provided, @Context String scope);
}

