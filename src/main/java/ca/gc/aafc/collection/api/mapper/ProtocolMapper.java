package ca.gc.aafc.collection.api.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.ProtocolDto;
import ca.gc.aafc.collection.api.entities.Protocol;
import ca.gc.aafc.dina.mapper.DinaMapperV2;
import ca.gc.aafc.dina.mapper.MapperStaticConverter;

@Mapper(imports = MapperStaticConverter.class)
public interface ProtocolMapper extends DinaMapperV2<ProtocolDto, Protocol> {

  ProtocolMapper INSTANCE = Mappers.getMapper(ProtocolMapper.class);

  @Mapping(target = "attachments", expression = "java(MapperStaticConverter.uuidListToExternalRelationsList(entity.getAttachments(), \"metadata\"))")
  ProtocolDto toDto(Protocol entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  Protocol toEntity(ProtocolDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget Protocol entity, ProtocolDto dto,
    @Context Set<String> provided, @Context String scope);
}
