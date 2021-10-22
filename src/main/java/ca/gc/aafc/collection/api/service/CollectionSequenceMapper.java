package ca.gc.aafc.collection.api.service;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import ca.gc.aafc.collection.api.entities.CollectionSequenceReserved;

@Mapper
public interface CollectionSequenceMapper {

  /**
   * Utilizes the {@code collection_get_next_id(integer, integer)} SQL function. Based
   * on the collection id provided, it will return the next available number(s) in the
   * sequence. This method also supports retrieving multiple numbers at once with the
   * amount param.
   * 
   * For example:
   * CNC001, CNC002, CNC003
   * (CNC is the collection prefix, the number is the sequence this will generate)
   * 
   * @param id     The collection id to find the sequence to iterate.
   * @param amount The amount you want to increment the counter. For example, if
   *               this is set to 10, it will reserve 10 ids.
   * @return CollectionSequenceReserved object. Which contains the lowest reserved number and the
   *         highest reserved number.
   */
  @Results({
    @Result(property = "lowReservedID", column = "low_reserved_id"),
    @Result(property = "highReservedID", column = "high_reserved_id")
  })
  @Select("SELECT * FROM collection_get_next_id(#{id}, #{amount}) LIMIT 1;")
  CollectionSequenceReserved getNextId(
    @Param("id") int id, 
    @Param("amount") int amount
  );
}
