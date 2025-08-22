package ca.gc.aafc.collection.api.mapper;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.AssemblageDto;
import ca.gc.aafc.collection.api.dto.AssociationDto;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.ImmutableMaterialSampleDto;
import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.collection.api.dto.ProtocolDto;
import ca.gc.aafc.collection.api.dto.StorageUnitUsageDto;
import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.Assemblage;
import ca.gc.aafc.collection.api.entities.Association;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.ImmutableMaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.entities.Protocol;
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
   * Ignore special fields like associations
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
  @Mapping(target = "associations", ignore = true)
  MaterialSample toEntity(MaterialSampleDto dto, @Context Set<String> provided,
                          @Context String scope);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
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
  @Mapping(target = "associations", ignore = true)
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
  default StorageUnitUsage toEntity(StorageUnitUsageDto dto, @Context Set<String> provided, @Context String scope) {
    return toStorageUnitUsage(dto, provided, "storageUnitUsage");
  }

  default ProjectDto toDto(Project entity, @Context Set<String> provided, @Context String scope) {
    return toProjectDto(entity, provided, "storageUnitUsage");
  }

  default ProtocolDto toDto(Protocol entity, @Context Set<String> provided, @Context String scope) {
    return toProtocolDto(entity, provided, "storageUnitUsage");
  }

  default AssemblageDto toDto(Assemblage entity, @Context Set<String> provided, @Context String scope) {
    return toAssemblageDto(entity, provided, "assemblage");
  }

  default ImmutableMaterialSampleDto toDto(ImmutableMaterialSample entity, @Context Set<String> provided, @Context String scope) {
    return toImmutableMaterialSampleDto(entity, provided, "materialSampleChildren");
  }

  // Relationships handling
  @Mapping(target = "collectors", qualifiedByName = "uuidToPersonExternalRelations")
  @Mapping(target = "attachment", qualifiedByName = "uuidToMetadataExternalRelations")
  @Mapping(target = "protocol.attachments", ignore = true)
  CollectingEventDto toCollectingEventDto(CollectingEvent entity, Set<String> provided, String scope);

  CollectionDto toCollectionDto(Collection entity, Set<String> provided, String scope);

  @Mapping(target = "storageUnit.storageUnitChildren", ignore = true)
  StorageUnitUsageDto toStorageUnitUsageDto(StorageUnitUsage entity, Set<String> provided, String scope);

  @Mapping(target = "storageUnit.storageUnitChildren", ignore = true)
  StorageUnitUsage toStorageUnitUsage(StorageUnitUsageDto dto, Set<String> provided, String scope);

  @Mapping(target = "attachment", qualifiedByName = "uuidToMetadataExternalRelations")
  ProjectDto toProjectDto(Project entity, Set<String> provided, String scope);

  @Mapping(target = "attachments", qualifiedByName = "uuidToMetadataExternalRelations")
  ProtocolDto toProtocolDto(Protocol entity, Set<String> provided, String scope);

  @Mapping(target = "attachment", qualifiedByName = "uuidToMetadataExternalRelations")
  AssemblageDto toAssemblageDto(Assemblage entity,  Set<String> provided, String scope);

  @Mapping(target = "id", ignore = true)
  ImmutableMaterialSampleDto toImmutableMaterialSampleDto(ImmutableMaterialSample entity, Set<String> provided, String scope);

  // Specific type mapping ---
  default List<AssociationDto> associationToAssociationDto(List<Association> associations) {
    if (CollectionUtils.isNotEmpty(associations)) {
      return associations.stream()
        .map(association -> AssociationDto.builder()
          .associatedSample(association.getAssociatedSample().getUuid())
          .associationType(association.getAssociationType())
          .remarks(association.getRemarks())
          .build())
        .collect(Collectors.toList());
    }
    return List.of();
  }

  @AfterMapping
  default void afterObjectStoreMetadataMapping(@MappingTarget MaterialSample entity,
                                               MaterialSampleDto dto) {
    if (CollectionUtils.isNotEmpty(dto.getAssociations())) {
      entity.setAssociations(dto.getAssociations().stream()
        .map(association -> Association.builder()
          .associatedSample(MaterialSample.builder().uuid(association.getAssociatedSample()).build())
          .associationType(association.getAssociationType())
          .remarks(association.getRemarks())
          .build())
        .collect(Collectors.toList()));
    }
  }

  // move to dina-base
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
