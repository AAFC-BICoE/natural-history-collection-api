package ca.gc.aafc.collection.api.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.AssemblageDto;
import ca.gc.aafc.collection.api.dto.AssociationDto;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.dto.ImmutableMaterialSampleDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.OrganismDto;
import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.collection.api.dto.ProtocolDto;
import ca.gc.aafc.collection.api.dto.StorageUnitUsageDto;
import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.collection.api.dto.PreparationMethodDto;
import ca.gc.aafc.collection.api.entities.Assemblage;
import ca.gc.aafc.collection.api.entities.Association;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.ImmutableMaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.entities.Protocol;
import ca.gc.aafc.collection.api.entities.StorageUnitUsage;
import ca.gc.aafc.collection.api.entities.PreparationType;
import ca.gc.aafc.collection.api.entities.PreparationMethod;
import ca.gc.aafc.dina.mapper.DinaMapperV2;
import ca.gc.aafc.dina.mapper.MapperStaticConverter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(imports = MapperStaticConverter.class)
public interface MaterialSampleMapper extends DinaMapperV2<MaterialSampleDto, MaterialSample> {

  MaterialSampleMapper INSTANCE = Mappers.getMapper(MaterialSampleMapper.class);

  @Mapping(target = "preparedBy", expression = "java(MapperStaticConverter.uuidListToExternalRelationsList(entity.getPreparedBy(), \"person\"))")
  @Mapping(target = "attachment", expression = "java(MapperStaticConverter.uuidListToExternalRelationsList(entity.getAttachment(), \"metadata\"))")
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
    return entity == null ? null : toCollectingEventDto(entity, provided, MaterialSample.COLLECTING_EVENT_PROP_NAME);
  }

  default CollectionDto toDto(Collection entity, @Context Set<String> provided, @Context String scope) {
    return entity == null ? null : toCollectionDto(entity, provided, MaterialSample.COLLECTION_PROP_NAME);
  }

  default StorageUnitUsageDto toDto(StorageUnitUsage entity, @Context Set<String> provided, @Context String scope) {
    return entity == null ? null : toStorageUnitUsageDto(entity, provided, MaterialSample.STORAGE_UNIT_USAGE_PROP_NAME);
  }

  default ProjectDto toDto(Project entity, @Context Set<String> provided, @Context String scope) {
    return entity == null ? null : toProjectDto(entity, provided, MaterialSample.PROJECTS_PROP_NAME);
  }

  default ProtocolDto toDto(Protocol entity, @Context Set<String> provided, @Context String scope) {
    return entity == null ? null : toProtocolDto(entity, provided, MaterialSample.PREPARATION_PROTOCOL_PROP_NAME);
  }

  default AssemblageDto toDto(Assemblage entity, @Context Set<String> provided, @Context String scope) {
    return entity == null ? null : toAssemblageDto(entity, provided, MaterialSample.ASSEMBLAGES_PROP_NAME);
  }

  default OrganismDto toDto(Organism entity, @Context Set<String> provided, @Context String scope) {
    return entity == null ? null : toOrganismDto(entity, provided, MaterialSample.ORGANISM_PROP_NAME);
  }

  default PreparationTypeDto toDto(PreparationType entity, @Context Set<String> provided, @Context String scope) {
    return entity == null ? null : toPreparationTypeDto(entity, provided, MaterialSample.PREPARATION_TYPE_PROP_NAME);
  }

  default PreparationMethodDto toDto(PreparationMethod entity, @Context Set<String> provided, @Context String scope) {
    return entity == null ? null : toPreparationMethodDto(entity, provided, MaterialSample.PREPARATION_METHOD_PROP_NAME);
  }

  default  List<ImmutableMaterialSampleDto> toDto(List<ImmutableMaterialSample> entities, @Context Set<String> provided, @Context String scope) {

    if (CollectionUtils.isEmpty(entities)) {
      return null;
    }
    return entities.stream().map(this::toImmutableMaterialSampleDto)
      .collect(Collectors.toList());
  }

  // Relationships handling
  @Mapping(target = "collectors", expression = "java(MapperStaticConverter.uuidListToExternalRelationsList(entity.getCollectors(), \"person\"))")
  @Mapping(target = "attachment", expression = "java(MapperStaticConverter.uuidListToExternalRelationsList(entity.getAttachment(), \"metadata\"))")
  @Mapping(target = "startEventDateTime", expression = "java(entity.supplyStartISOEventDateTime() != null ? entity.supplyStartISOEventDateTime().toString() : null)")
  @Mapping(target = "endEventDateTime", expression = "java(entity.supplyEndISOEventDateTime() != null ? entity.supplyEndISOEventDateTime().toString() : null)")
  @Mapping(target = "protocol.attachments", ignore = true)
  @Mapping(target = "expedition.participants", ignore = true)
  CollectingEventDto toCollectingEventDto(CollectingEvent entity, Set<String> provided, String scope);

  CollectionDto toCollectionDto(Collection entity, Set<String> provided, String scope);

  PreparationTypeDto toPreparationTypeDto(PreparationType entity, Set<String> provided, String scope);

  PreparationMethodDto toPreparationMethodDto(PreparationMethod entity, Set<String> provided, String scope);

  @Mapping(target = "storageUnit", ignore = true)
  @Mapping(target = "storageUnitType", ignore = true)
  StorageUnitUsageDto toStorageUnitUsageDto(StorageUnitUsage entity, Set<String> provided, String scope);

  @Mapping(target = "attachment", expression = "java(MapperStaticConverter.uuidListToExternalRelationsList(entity.getAttachment(), \"metadata\"))")
  ProjectDto toProjectDto(Project entity, Set<String> provided, String scope);

  @Mapping(target = "attachments", expression = "java(MapperStaticConverter.uuidListToExternalRelationsList(entity.getAttachments(), \"metadata\"))")
  ProtocolDto toProtocolDto(Protocol entity, Set<String> provided, String scope);

  @Mapping(target = "attachment", expression = "java(MapperStaticConverter.uuidListToExternalRelationsList(entity.getAttachment(), \"metadata\"))")
  AssemblageDto toAssemblageDto(Assemblage entity,  Set<String> provided, String scope);

  OrganismDto toOrganismDto(Organism entity, Set<String> provided, String scope);

  // Optional fields

  /**
   * Considered an optional field so no scope is used.
   * @param entity
   * @return
   */
  @Mapping(target = "id", source = "uuid")
  ImmutableMaterialSampleDto toImmutableMaterialSampleDto(ImmutableMaterialSample entity);

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
  default void afterMaterialSampleMapping(@MappingTarget MaterialSample entity,
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
}
