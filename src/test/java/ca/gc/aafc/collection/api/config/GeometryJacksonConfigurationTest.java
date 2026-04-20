package ca.gc.aafc.collection.api.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import jakarta.inject.Inject;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.CollectionModuleBaseIT;

/*
  This configuration class responsible for Jackson configuration verification test
  Purpose of this test is 
  Fail Fast if:
    Jackson config changes
    GeolatteGeomModule is removed ex: GeoLatteConfig class
    Geometry<G2D> cannot deserialize GeoJSON
    Introduces conflicting module
*/
@SpringBootTest(classes = CollectionModuleApiLauncher.class)
public class GeometryJacksonConfigurationTest extends CollectionModuleBaseIT {

  @Inject
  private ObjectMapper objectMapper;

  @Test
  void deserializePolygonGeoJson_shouldReturnPolygon() throws Exception {

    String geoJson = """
        {
          "type": "Polygon",
          "coordinates": [[[100.0,0.0],[101.0,0.0],[101.0,1.0],[100.0,1.0],[100.0,0.0]]]
        }
        """;
    Geometry<G2D> geometry = objectMapper.readValue(geoJson,
        new com.fasterxml.jackson.core.type.TypeReference<Geometry<G2D>>() {
        });
    assertNotNull(geometry);
    assertEquals(GeometryType.POLYGON, geometry.getGeometryType());
  }
}