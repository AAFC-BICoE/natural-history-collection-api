package ca.gc.aafc.collection.api.service;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.CollectionSequence;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;

@Mapper
public interface CollectionSequenceService {
  
  @Select(value = "SELECT collection_get_next_id(#{id})")
  @Options(statementType = StatementType.CALLABLE)
  Integer getNextId(@Param("id") Integer id);
}
