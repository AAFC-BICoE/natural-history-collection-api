package ca.gc.aafc.collection.api.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.SplitConfigurationDto;
import ca.gc.aafc.collection.api.entities.SplitConfiguration;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

@Mapper
public interface SplitConfigurationMapper extends DinaMapperV2<SplitConfigurationDto, SplitConfiguration> {

  SplitConfigurationMapper INSTANCE = Mappers.getMapper(SplitConfigurationMapper.class);

  SplitConfigurationDto toDto(SplitConfiguration entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  SplitConfiguration toEntity(SplitConfigurationDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget SplitConfiguration entity, SplitConfigurationDto dto,
                   @Context Set<String> provided, @Context String scope);
}

