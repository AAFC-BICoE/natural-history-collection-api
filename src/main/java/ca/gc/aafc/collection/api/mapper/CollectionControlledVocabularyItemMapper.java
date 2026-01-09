package ca.gc.aafc.collection.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyItemDto;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

import java.util.Set;

@Mapper
public interface CollectionControlledVocabularyItemMapper extends DinaMapperV2<CollectionControlledVocabularyItemDto, CollectionControlledVocabularyItem> {

  CollectionControlledVocabularyItemMapper INSTANCE = Mappers.getMapper(CollectionControlledVocabularyItemMapper.class);

  CollectionControlledVocabularyItemDto toDto(CollectionControlledVocabularyItem entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "controlledVocabulary", ignore = true)
  CollectionControlledVocabularyItem toEntity(CollectionControlledVocabularyItemDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "controlledVocabulary", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget CollectionControlledVocabularyItem entity, CollectionControlledVocabularyItemDto dto, @Context Set<String> provided, @Context String scope);
}
