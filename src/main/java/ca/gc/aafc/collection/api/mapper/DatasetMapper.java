package ca.gc.aafc.collection.api.mapper;

import java.util.Set;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ca.gc.aafc.collection.api.dto.DatasetDto;
import ca.gc.aafc.dina.mapper.DinaMapperV2;
import ca.gc.aafc.collection.api.entities.Dataset;
import ca.gc.aafc.dina.mapper.MapperStaticConverter;

@Mapper(imports = MapperStaticConverter.class)
public interface DatasetMapper extends DinaMapperV2<DatasetDto, Dataset> {
  DatasetMapper INSTANCE = Mappers.getMapper(DatasetMapper.class);

  DatasetDto toDto(Dataset entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  Dataset toEntity(DatasetDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget Dataset entity, DatasetDto dto,
      @Context Set<String> provided, @Context String scope);
}
