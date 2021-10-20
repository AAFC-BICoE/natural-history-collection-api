package ca.gc.aafc.collection.api.service;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;

@Mapper
public interface CollectionSequenceMapper {

  /**
   * Utilizes the `collection_get_next_id(integer)` SQL function. Based on the collection id
   * provided, it will return the next number in the sequence.
   * 
   * For example:
   * 
   * CNC001
   * CNC002
   * CNC003
   * 
   * Calling this function will SET and return the value 4.
   */
  @Select(value = "SELECT collection_get_next_id(#{id})")
  @Options(statementType = StatementType.CALLABLE)
  Integer getNextId(@Param("id") Integer id);

}
