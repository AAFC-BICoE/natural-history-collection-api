package ca.gc.aafc.collection.api.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.PreparationMethodDto;
import ca.gc.aafc.collection.api.entities.PreparationMethod;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

@Mapper
public interface PreparationMethodMapper extends DinaMapperV2<PreparationMethodDto, PreparationMethod> {

  PreparationMethodMapper INSTANCE = Mappers.getMapper(PreparationMethodMapper.class);

  PreparationMethodDto toDto(PreparationMethod entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  PreparationMethod toEntity(PreparationMethodDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget PreparationMethod entity, PreparationMethodDto dto,
                   @Context Set<String> provided, @Context String scope);
}
