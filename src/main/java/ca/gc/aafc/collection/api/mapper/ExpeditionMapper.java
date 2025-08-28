package ca.gc.aafc.collection.api.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.ExpeditionDto;
import ca.gc.aafc.collection.api.entities.Expedition;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

@Mapper
public interface ExpeditionMapper extends DinaMapperV2<ExpeditionDto, Expedition> {

  ExpeditionMapper INSTANCE = Mappers.getMapper(ExpeditionMapper.class);

  @Mapping(target = "participants", expression = "java(ExternalUUIDConverter.uuidToPersonExternalRelations(entity.getParticipants()))")
  ExpeditionDto toDto(Expedition entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "participants", ignore = true)
  @Mapping(target = "startDate", ignore = true)
  @Mapping(target = "endDate", ignore = true)
  Expedition toEntity(ExpeditionDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "participants", ignore = true)
  @Mapping(target = "startDate", ignore = true)
  @Mapping(target = "endDate", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget Expedition entity, ExpeditionDto dto,
                   @Context Set<String> provided, @Context String scope);
}
