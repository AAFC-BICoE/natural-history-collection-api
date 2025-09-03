package ca.gc.aafc.collection.api.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.AssemblageDto;
import ca.gc.aafc.collection.api.entities.Assemblage;
import ca.gc.aafc.dina.mapper.DinaMapperV2;
import ca.gc.aafc.dina.mapper.MapperStaticConverter;

@Mapper( imports = MapperStaticConverter.class)
public interface AssemblageMapper extends DinaMapperV2<AssemblageDto, Assemblage> {

  AssemblageMapper INSTANCE = Mappers.getMapper(AssemblageMapper.class);

  @Mapping(target = "attachment", expression = "java(MapperStaticConverter.uuidListToExternalRelationsList(entity.getAttachment(), \"metadata\"))")
  AssemblageDto toDto(Assemblage entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  Assemblage toEntity(AssemblageDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget Assemblage entity, AssemblageDto dto,
                   @Context Set<String> provided, @Context String scope);
}
