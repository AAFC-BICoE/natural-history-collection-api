package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CoordinateSystemConfigDto;
import ca.gc.aafc.collection.api.dto.SrsConfigDto;
import io.crnk.core.queryspec.QuerySpec;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

class LocationConfigRepoIT extends CollectionModuleBaseIT {

  @Inject
  private CoordinateSystemConfigRepo coordinateSystemConfigRepo;

  @Inject
  private SrsConfigRepo srsConfigRepo;

  @Test
  void findAll_CoordinateSystem() {
    MatcherAssert.assertThat(
      coordinateSystemConfigRepo.findAll(new QuerySpec(CoordinateSystemConfigDto.class))
        .get(0).getCoordinateSystem(),
      Matchers.contains(
        "decimal degrees",
        "degrees decimal minutes",
        "degrees minutes seconds",
        "UTM"));
  }

  @Test
  void findAll_SrsSystem() {
    MatcherAssert.assertThat(
      srsConfigRepo.findAll(new QuerySpec(SrsConfigDto.class)).get(0).getSrs(),
      Matchers.contains("WGS84 (EPSG:4326)", "NAD27 (EPSG:4276)"));
  }
}