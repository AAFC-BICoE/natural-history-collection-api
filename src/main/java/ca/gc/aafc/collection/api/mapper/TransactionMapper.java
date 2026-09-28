package ca.gc.aafc.collection.api.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.collection.api.dto.TransactionDto;
import ca.gc.aafc.collection.api.entities.Transaction;
import ca.gc.aafc.dina.mapper.DinaMapperV2;
import ca.gc.aafc.dina.mapper.MapperStaticConverter;

@Mapper(imports = MapperStaticConverter.class)
public interface TransactionMapper extends DinaMapperV2<TransactionDto, Transaction> {
    
  TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

  @Mapping(target = "attachment", expression = "java(MapperStaticConverter.uuidListToExternalRelationsList(entity.getAttachment(), \"metadata\"))")
  @Mapping(target = "materialSamples", expression = "java(MapperStaticConverter.uuidListToExternalRelationsList(entity.getMaterialSamples(), \"external-material-sample\"))")
  TransactionDto toDto(Transaction entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "attachment", ignore = true)
  @Mapping(target = "materialSamples", ignore = true)
  Transaction toEntity(TransactionDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "attachment", ignore = true)
  @Mapping(target = "materialSamples", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget Transaction entity, TransactionDto dto, @Context Set<String> provided, @Context String scope);
}
