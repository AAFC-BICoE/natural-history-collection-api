package ca.gc.aafc.collection.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.OrganismDto;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

import java.util.Set;

@Mapper
public interface OrganismMapper extends DinaMapperV2<OrganismDto, Organism> {

  OrganismMapper INSTANCE = Mappers.getMapper(OrganismMapper.class);

  OrganismDto toDto(Organism entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  Organism toEntity(OrganismDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget Organism entity, OrganismDto dto,
                   @Context Set<String> provided, @Context String scope);
}
