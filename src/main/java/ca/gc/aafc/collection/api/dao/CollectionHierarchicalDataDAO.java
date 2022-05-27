package ca.gc.aafc.collection.api.dao;

import ca.gc.aafc.collection.api.dto.MaterialSampleHierarchyObject;
import ca.gc.aafc.collection.api.mybatis.DeterminationSummaryTypeHandler;
import ca.gc.aafc.dina.mybatis.UUIDTypeHandler;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;

import java.util.List;

/**
 * Similar to {@link ca.gc.aafc.dina.service.PostgresHierarchicalDataService} but specific to collection-api data.
 */
@Mapper
public interface CollectionHierarchicalDataDAO {

  @Select(
    "WITH RECURSIVE get_hierarchy (id, parent_id, uuid, name, rank, target_organism_primary_determination) AS (" +
            "SELECT initial_t.id, initial_t.parent_material_sample_id, initial_t.uuid, initial_t.material_sample_name, 1, jsonb_path_query_first(organism.determination, '$[*] ? (@.isPrimary == true)')" +
            "FROM material_sample AS initial_t LEFT OUTER JOIN organism ON initial_t.id = organism.material_sample_id  and organism.is_target WHERE initial_t.id = ${id}" +
            " UNION ALL " +
            "SELECT node.id, node.parent_material_sample_id, node.uuid, node.material_sample_name, gh.rank + 1, jsonb_path_query_first(organism.determination, '$[*] ? (@.isPrimary == true)') FROM " +
            " get_hierarchy gh, material_sample AS node  LEFT OUTER JOIN organism ON node.id = organism.material_sample_id and organism.is_target WHERE node.id = gh.parent_id)" +
            "SELECT id, uuid, name, rank, target_organism_primary_determination FROM get_hierarchy;")
  @Options(statementType = StatementType.CALLABLE)
  @Result(property = "uuid", column = "uuid", typeHandler = UUIDTypeHandler.class)
  @Result(property = "targetOrganismPrimaryDetermination", column = "target_organism_primary_determination", typeHandler = DeterminationSummaryTypeHandler.class)
  List<MaterialSampleHierarchyObject> getHierarchy(@Param("id") Integer id);

}
