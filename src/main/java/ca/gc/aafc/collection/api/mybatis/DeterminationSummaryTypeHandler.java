package ca.gc.aafc.collection.api.mybatis;

import ca.gc.aafc.collection.api.dto.MaterialSampleHierarchyObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(MaterialSampleHierarchyObject.DeterminationSummary.class)
public class DeterminationSummaryTypeHandler extends BaseTypeHandler<MaterialSampleHierarchyObject.DeterminationSummary> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, MaterialSampleHierarchyObject.DeterminationSummary parameter, JdbcType jdbcType)
          throws SQLException {
    if (ps != null) {
      PGobject jsonObject = new PGobject();
      jsonObject.setType("jsonb");
      try {
        jsonObject.setValue(JsonbTypeHandlerMapper.writeValue(parameter));
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
      ps.setObject(i, jsonObject);
    }
  }

  @Override
  public MaterialSampleHierarchyObject.DeterminationSummary getNullableResult(ResultSet rs, String columnName) throws SQLException {
    if( rs.getString(columnName) == null) {
      return null;
    }
    try {
      return JsonbTypeHandlerMapper.readValue(rs.getString(columnName), MaterialSampleHierarchyObject.DeterminationSummary.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public MaterialSampleHierarchyObject.DeterminationSummary getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    if( rs.getString(columnIndex) == null) {
      return null;
    }
    try {
      return JsonbTypeHandlerMapper.readValue(rs.getString(columnIndex), MaterialSampleHierarchyObject.DeterminationSummary.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public MaterialSampleHierarchyObject.DeterminationSummary getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    if( cs.getString(columnIndex) == null) {
      return null;
    }
    try {
      return JsonbTypeHandlerMapper.readValue(cs.getString(columnIndex), MaterialSampleHierarchyObject.DeterminationSummary.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
