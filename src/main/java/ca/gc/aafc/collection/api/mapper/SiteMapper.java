package ca.gc.aafc.collection.api.mapper;

import java.util.Set;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ca.gc.aafc.collection.api.dto.SiteDto;
import ca.gc.aafc.dina.mapper.DinaMapperV2;
import ca.gc.aafc.collection.api.entities.Site;
import ca.gc.aafc.dina.mapper.MapperStaticConverter;

@Mapper(imports = MapperStaticConverter.class)
public interface SiteMapper extends DinaMapperV2<SiteDto, Site> {
  SiteMapper INSTANCE = Mappers.getMapper(SiteMapper.class);

  @Mapping(target = "id", ignore = true)
  Site toEntity(SiteDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget Site entity, SiteDto dto,
      @Context Set<String> provided, @Context String scope);
}
