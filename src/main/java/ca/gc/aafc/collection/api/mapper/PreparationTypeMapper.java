package ca.gc.aafc.collection.api.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.collection.api.entities.PreparationType;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

@Mapper
public interface PreparationTypeMapper extends DinaMapperV2<PreparationTypeDto, PreparationType> {

  PreparationTypeMapper INSTANCE = Mappers.getMapper(PreparationTypeMapper.class);

  PreparationTypeDto toDto(PreparationType entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  PreparationType toEntity(PreparationTypeDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget PreparationType entity, PreparationTypeDto dto,
                   @Context Set<String> provided, @Context String scope);
}
