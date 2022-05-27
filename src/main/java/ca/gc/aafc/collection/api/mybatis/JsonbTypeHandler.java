package ca.gc.aafc.collection.api.mybatis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// TODO replace with JsonNodeTypeHandler from dina-base 0.89
@MappedTypes(JsonNode.class)
public class JsonbTypeHandler extends BaseTypeHandler<JsonNode> {

  private static final ObjectMapper OM = new ObjectMapper();

  private static final ObjectReader JSON_NODE_READER;
  static {
    JSON_NODE_READER = OM.readerFor(JsonNode.class);
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, JsonNode parameter, JdbcType jdbcType)
          throws SQLException {
    if (ps != null) {
      PGobject jsonObject = new PGobject();
      jsonObject.setType("jsonb");
      try {
        jsonObject.setValue(OM.writeValueAsString(parameter));
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
      ps.setObject(i, jsonObject);
    }
  }

  @Override
  public JsonNode getNullableResult(ResultSet rs, String columnName) throws SQLException {
    if( rs.getString(columnName) == null) {
      return null;
    }
    try {
      return JSON_NODE_READER.readTree(rs.getString(columnName));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public JsonNode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    if( rs.getString(columnIndex) == null) {
      return null;
    }
    try {
      return JSON_NODE_READER.readTree(rs.getString(columnIndex));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public JsonNode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    if( cs.getString(columnIndex) == null) {
      return null;
    }
    try {
      return JSON_NODE_READER.readTree(cs.getString(columnIndex));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
