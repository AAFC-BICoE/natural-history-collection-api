package ca.gc.aafc.collection.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.dina.mapper.DinaMapperV2;
import ca.gc.aafc.dina.mapper.MapperStaticConverter;

import java.util.Set;

@Mapper(imports = MapperStaticConverter.class)
public interface ProjectMapper extends DinaMapperV2<ProjectDto, Project> {

  ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

  @Mapping(target = "attachment", expression = "java(MapperStaticConverter.uuidListToExternalRelationsList(entity.getAttachment(), \"metadata\"))")
  ProjectDto toDto(Project entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  Project toEntity(ProjectDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget Project entity, ProjectDto dto,
                   @Context Set<String> provided, @Context String scope);
}
