package ca.gc.aafc.collection.api.mapper;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.collection.api.dto.DatasetDto;
import ca.gc.aafc.dina.mapper.DinaMapperV2;
import ca.gc.aafc.collection.api.entities.Dataset;
import ca.gc.aafc.dina.mapper.MapperStaticConverter;

@Mapper(imports = MapperStaticConverter.class)
public interface DatasetMapper extends DinaMapperV2<DatasetDto, Dataset> {

  DatasetMapper INSTANCE = Mappers.getMapper(DatasetMapper.class);
  ObjectMapper OBJ_MAPPER = new ObjectMapper();
  TypeReference<Map<String, Object>> MAP_TYPEREF =
    new TypeReference<>() {
    };

  @Mapping(source = "query", target = "query", qualifiedByName = "mapToJson")
  DatasetDto toDto(Dataset entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "query", target = "query", qualifiedByName = "jsonToMap")
  Dataset toEntity(DatasetDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "query", target = "query", qualifiedByName = "jsonToMap")
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget Dataset entity, DatasetDto dto,
      @Context Set<String> provided, @Context String scope);

    /**
   * Converts a map to JSON string.
   * @param query the map to convert
   * @return the JSON string representation
   */
  @Named("mapToJson")
  static String mapToJsonString(Map<String, Object> query) {
    try {
      return query == null ? null : OBJ_MAPPER.writeValueAsString(query);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Named("jsonToMap")
  static Map<String, Object> jsonStringToMap(String query) {
    if (StringUtils.isBlank(query)) {
      return null;
    }
    try {
      return OBJ_MAPPER.readValue(query, MAP_TYPEREF);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
