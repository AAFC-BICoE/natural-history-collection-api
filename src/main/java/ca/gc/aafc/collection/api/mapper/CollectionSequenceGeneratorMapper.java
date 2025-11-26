package ca.gc.aafc.collection.api.mapper;

import java.util.Set;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.CollectionSequenceGeneratorDto;
import ca.gc.aafc.collection.api.entities.CollectionSequenceGenerationRequest;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

@Mapper
public interface CollectionSequenceGeneratorMapper extends DinaMapperV2<CollectionSequenceGeneratorDto, CollectionSequenceGenerationRequest> {
  CollectionSequenceGeneratorMapper INSTANCE = Mappers.getMapper(CollectionSequenceGeneratorMapper.class);

  CollectionSequenceGeneratorDto toDto(CollectionSequenceGenerationRequest entity, @Context Set<String> provided, @Context String scope);

  CollectionSequenceGenerationRequest toEntity(CollectionSequenceGeneratorDto dto, @Context Set<String> provided, @Context String scope);
}
