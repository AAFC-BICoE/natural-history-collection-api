package ca.gc.aafc.collection.api.dao;

import ca.gc.aafc.collection.api.dto.MaterialSampleHierarchyObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Similar to {@link ca.gc.aafc.dina.service.PostgresHierarchicalDataService} but specific to collection-api data.
 */
@Mapper
public interface CollectionHierarchicalDataDAO {

  /**
   * Definition loaded from mapper.xml file due to Java API annotation limitation
   * @param id
   * @return
   */
  List<MaterialSampleHierarchyObject> getHierarchy(@Param("id") Integer id);

}
