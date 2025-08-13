package ca.gc.aafc.collection.api.mapper;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.collection.api.dto.StorageUnitUsageDto;
import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.entities.StorageUnitUsage;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

@Mapper
public interface MaterialSampleMapper extends DinaMapperV2<MaterialSampleDto, MaterialSample> {

  MaterialSampleMapper INSTANCE = Mappers.getMapper(MaterialSampleMapper.class);

  @Mapping(source = "preparedBy", target = "preparedBy", qualifiedByName = "uuidToPersonExternalRelations")
  @Mapping(source = "attachment", target = "attachment", qualifiedByName = "uuidToMetadataExternalRelations")
  MaterialSampleDto toDto(MaterialSample entity, @Context Set<String> provided,
                          @Context String scope);

  /**
   * Ignore internal id
   * Ignore special fields like acSubtype and acSubtypeId.
   * Ignore relationships for MapStruct mapping dto -> entity
   * @param dto
   * @param provided provided properties so only those will be set
   * @param scope used to check provided properties within nested properties
   * @return
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "materialSampleChildren", ignore = true)
  @Mapping(target = "hierarchy", ignore = true)
  @Mapping(target = "parentMaterialSample", ignore = true)
  @Mapping(target = "collectingEvent", ignore = true)
  @Mapping(target = "collection", ignore = true)
  @Mapping(target = "preparationType", ignore = true)
  @Mapping(target = "preparationMethod", ignore = true)
  @Mapping(target = "storageUnitUsage", ignore = true)
  @Mapping(target = "organism", ignore = true)
  @Mapping(target = "preparationProtocol", ignore = true)
  @Mapping(target = "projects", ignore = true)
  @Mapping(target = "assemblages", ignore = true)
  @Mapping(target = "preparedBy", ignore = true)
  @Mapping(target = "attachment", ignore = true)
  MaterialSample toEntity(MaterialSampleDto dto, @Context Set<String> provided,
                          @Context String scope);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget MaterialSample entity, MaterialSampleDto dto,
                   @Context Set<String> provided, @Context String scope);


  /**
   * Default method to intercept the mapping and set the context to the relationship
   * @param entity
   * @param provided
   * @param scope will be ignored but required so MapStruct uses it
   * @return
   */
  default CollectingEventDto toDto(CollectingEvent entity, @Context Set<String> provided, @Context String scope) {
    return toCollectingEventDto(entity, provided, "collectingEvent");
  }

  default CollectionDto toDto(Collection entity, @Context Set<String> provided, @Context String scope) {
    return toCollectionDto(entity, provided, "collection");
  }

  default StorageUnitUsageDto toDto(StorageUnitUsage entity, @Context Set<String> provided, @Context String scope) {
    return toStorageUnitUsageDto(entity, provided, "storageUnitUsage");
  }

  default ProjectDto toDto(Project entity, @Context Set<String> provided, @Context String scope) {
    return toProjectDto(entity, provided, "storageUnitUsage");
  }

  // Relationships handling
  @Mapping(target = "collectors", qualifiedByName = "uuidToPersonExternalRelations")
  @Mapping(target = "attachment", qualifiedByName = "uuidToMetadataExternalRelations")
  CollectingEventDto toCollectingEventDto(CollectingEvent entity, Set<String> provided, String scope);

  CollectionDto toCollectionDto(Collection entity, Set<String> provided, String scope);

  StorageUnitUsageDto toStorageUnitUsageDto(StorageUnitUsage entity, Set<String> provided, String scope);

  @Mapping(target = "attachment", qualifiedByName = "uuidToMetadataExternalRelations")
  ProjectDto toProjectDto(Project entity, Set<String> provided, String scope);

  // mode to dina-base
  @Named("uuidToPersonExternalRelations")
  static List<ExternalRelationDto> uuidToPersonExternalRelations(List<UUID> personUUID) {
    return personUUID == null ? null :
      personUUID.stream().map(uuid ->
        ExternalRelationDto.builder().id(uuid.toString()).type("person").build()).toList();
  }

  @Named("uuidToMetadataExternalRelations")
  static List<ExternalRelationDto> uuidToMetadataExternalRelations(List<UUID> metadataUUIDs) {
    return metadataUUIDs == null ? null :
      metadataUUIDs.stream().map(uuid ->
        ExternalRelationDto.builder().id(uuid.toString()).type("metadata").build()).toList();
  }

}
