package ca.gc.aafc.collection.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.AssociationDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.Association;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.mapper.DinaMapperV2;
import ca.gc.aafc.dina.mapper.MapperStaticConverter;

import java.util.Set;

@Mapper(imports = MapperStaticConverter.class)
public interface AssociationMapper extends DinaMapperV2<AssociationDto, Association> {

  AssociationMapper INSTANCE = Mappers.getMapper(AssociationMapper.class);

  AssociationDto toDto(Association entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "sample", ignore = true)
  @Mapping(target = "associatedSample", ignore = true)
  Association toEntity(AssociationDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "sample", ignore = true)
  @Mapping(target = "associatedSample", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget Association entity, AssociationDto dto,
                   @Context Set<String> provided, @Context String scope);

  // WARNING here since we can only have 1 method per type is also means sample needs to be provided
  // to get associatedSample
  default MaterialSampleDto toDto(MaterialSample entity, @Context Set<String> provided, @Context String scope) {
    return entity == null ? null : toMaterialSampleDto(entity, provided, "sample");
  }

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
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
  MaterialSampleDto toMaterialSampleDto(MaterialSample entity, Set<String> provided, String scope);
}
