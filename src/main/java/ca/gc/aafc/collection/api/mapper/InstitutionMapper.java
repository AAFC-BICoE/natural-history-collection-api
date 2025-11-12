package ca.gc.aafc.collection.api.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.InstitutionDto;
import ca.gc.aafc.collection.api.entities.Institution;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

@Mapper
public interface InstitutionMapper extends DinaMapperV2<InstitutionDto, Institution> {

  InstitutionMapper INSTANCE = Mappers.getMapper(InstitutionMapper.class);

  InstitutionDto toDto(Institution entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  Institution toEntity(InstitutionDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget Institution entity, InstitutionDto dto,
                   @Context Set<String> provided, @Context String scope);
}
