package ca.gc.aafc.collection.api.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.CollectionMappingStrategy;
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
import ca.gc.aafc.dina.datetime.ISODateTime;
import ca.gc.aafc.dina.mapper.DinaMapperV2;
import ca.gc.aafc.dina.mapper.MapperStaticConverter;

import java.util.Set;

@Mapper(imports = { MapperStaticConverter.class }, collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public interface CollectingEventMapper extends DinaMapperV2<CollectingEventDto, CollectingEvent> {

  CollectingEventMapper INSTANCE = Mappers.getMapper(CollectingEventMapper.class);

  @Mapping(target = "collectors", expression = "java(MapperStaticConverter.uuidListToExternalRelationsList(entity.getCollectors(), \"person\"))")
  @Mapping(target = "attachment", expression = "java(MapperStaticConverter.uuidListToExternalRelationsList(entity.getAttachment(), \"metadata\"))")
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "startEventDateTime", expression = "java(entity.supplyStartISOEventDateTime() != null ? entity.supplyStartISOEventDateTime().toString() : null)")
  @Mapping(target = "endEventDateTime", expression = "java(entity.supplyEndISOEventDateTime() != null ? entity.supplyEndISOEventDateTime().toString() : null)")
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

  @Mapping(target = "attachments", expression = "java(MapperStaticConverter.uuidListToExternalRelationsList(entity.getAttachments(), \"metadata\"))")
  ProtocolDto toProtocolDto(Protocol entity, Set<String> provided, String scope);

  CollectionMethodDto toCollectionMethodDto(CollectionMethod entity, Set<String> provided, String scope);


  @AfterMapping
  default void handleDateTimes(CollectingEventDto dto, @MappingTarget CollectingEvent entity,
                               @Context Set<String> provided) {

    if (provided.contains("startEventDateTime")) {
      if (StringUtils.isBlank(dto.getStartEventDateTime())) {
        entity.applyStartISOEventDateTime(null);
      } else {
        entity.applyStartISOEventDateTime(ISODateTime.parse(dto.getStartEventDateTime()));
      }
    }

    if (provided.contains("endEventDateTime")) {
      if (StringUtils.isBlank(dto.getEndEventDateTime())) {
        entity.applyEndISOEventDateTime(null);
      } else {
        entity.applyEndISOEventDateTime(ISODateTime.parse(dto.getEndEventDateTime()));
      }
    }
  }

}