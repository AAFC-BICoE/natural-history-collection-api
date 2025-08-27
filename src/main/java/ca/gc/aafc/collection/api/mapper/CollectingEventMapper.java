package ca.gc.aafc.collection.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.CollectionMethodDto;
import ca.gc.aafc.collection.api.dto.ProtocolDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.CollectionMethod;
import ca.gc.aafc.collection.api.entities.Protocol;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

import java.util.Set;

@Mapper(imports = { ExternalUUIDConverter.class })
public interface CollectingEventMapper extends DinaMapperV2<CollectingEventDto, CollectingEvent> {

  CollectingEventMapper INSTANCE = Mappers.getMapper(CollectingEventMapper.class);

  @Mapping(target = "collectors", expression = "java(ExternalUUIDConverter.uuidToPersonExternalRelations(entity.getCollectors()))")
  @Mapping(target = "attachment", expression = "java(ExternalUUIDConverter.uuidToMetadataExternalRelations(entity.getAttachment()))")
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "startEventDateTime", ignore = true)
  @Mapping(target = "endEventDateTime", ignore = true)
  CollectingEventDto toDto(CollectingEvent entity, @Context Set<String> provided,
                           @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "eventGeom", ignore = true)
  @Mapping(target = "startEventDateTime", ignore = true)
  @Mapping(target = "endEventDateTime", ignore = true)
  @Mapping(target = "startEventDateTimePrecision", ignore = true)
  @Mapping(target = "endEventDateTimePrecision", ignore = true)
  @Mapping(target = "collectors", ignore = true)
  @Mapping(target = "attachment", ignore = true)
  @Mapping(target = "collectionMethod", ignore = true)
  @Mapping(target = "protocol", ignore = true)
  CollectingEvent toEntity(CollectingEventDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "eventGeom", ignore = true)
  @Mapping(target = "startEventDateTime", ignore = true)
  @Mapping(target = "endEventDateTime", ignore = true)
  @Mapping(target = "startEventDateTimePrecision", ignore = true)
  @Mapping(target = "endEventDateTimePrecision", ignore = true)
  @Mapping(target = "collectors", ignore = true)
  @Mapping(target = "attachment", ignore = true)
  @Mapping(target = "collectionMethod", ignore = true)
  @Mapping(target = "protocol", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget CollectingEvent entity, CollectingEventDto dto,
                   @Context Set<String> provided, @Context String scope);

  default ProtocolDto toDto(Protocol entity, @Context Set<String> provided, @Context String scope) {
    return toProtocolDto(entity, provided, "protocol");
  }

  default CollectionMethodDto toDto(CollectionMethod entity, @Context Set<String> provided, @Context String scope) {
    return toCollectionMethodDto(entity, provided, "collectionMethod");
  }

  @Mapping(target = "attachments", qualifiedByName = "uuidToMetadataExternalRelations")
  ProtocolDto toProtocolDto(Protocol entity, Set<String> provided, String scope);

  CollectionMethodDto toCollectionMethodDto(CollectionMethod entity, Set<String> provided, String scope);

}