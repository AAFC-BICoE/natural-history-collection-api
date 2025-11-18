package ca.gc.aafc.collection.api.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.FormTemplateDto;
import ca.gc.aafc.collection.api.entities.FormTemplate;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

@Mapper
public interface FormTemplateMapper extends DinaMapperV2<FormTemplateDto, FormTemplate> {

  FormTemplateMapper INSTANCE = Mappers.getMapper(FormTemplateMapper.class);

  FormTemplateDto toDto(FormTemplate entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  FormTemplate toEntity(FormTemplateDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget FormTemplate entity, FormTemplateDto dto,
                   @Context Set<String> provided, @Context String scope);
}
