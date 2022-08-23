package ca.gc.aafc.collection.api.mybatis;

import ca.gc.aafc.collection.api.dto.MaterialSampleHierarchyObject;
import ca.gc.aafc.dina.mybatis.JacksonBasedTypeHandler;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(MaterialSampleHierarchyObject.DeterminationSummary.class)
public class DeterminationSummaryTypeHandler extends JacksonBasedTypeHandler<MaterialSampleHierarchyObject.DeterminationSummary> {

  @Override
  protected Class<MaterialSampleHierarchyObject.DeterminationSummary> getTypeHandlerClass() {
    return MaterialSampleHierarchyObject.DeterminationSummary.class;
  }
}
