package ca.gc.aafc.collection.api.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

@Mapper
public interface CollectionMapper extends DinaMapperV2<CollectionDto, Collection> {

  CollectionMapper INSTANCE = Mappers.getMapper(CollectionMapper.class);

  CollectionDto toDto(Collection entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "parentCollection", ignore = true)
  @Mapping(target = "institution", ignore = true)
  Collection toEntity(CollectionDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "parentCollection", ignore = true)
  @Mapping(target = "institution", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget Collection entity, CollectionDto dto,
                   @Context Set<String> provided, @Context String scope);
}
