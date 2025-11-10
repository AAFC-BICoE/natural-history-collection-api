package ca.gc.aafc.collection.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyDto;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabulary;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

import java.util.Set;

@Mapper
public interface CollectionControlledVocabularyMapper extends DinaMapperV2<CollectionControlledVocabularyDto, CollectionControlledVocabulary> {

  CollectionControlledVocabularyMapper INSTANCE = Mappers.getMapper(CollectionControlledVocabularyMapper.class);

  CollectionControlledVocabularyDto toDto(CollectionControlledVocabulary entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  CollectionControlledVocabulary toEntity(CollectionControlledVocabularyDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget CollectionControlledVocabulary entity, CollectionControlledVocabularyDto dto, @Context Set<String> provided, @Context String scope);
}
