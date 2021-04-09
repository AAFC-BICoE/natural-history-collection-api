package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CoordinateSystemConfigDto;
import io.crnk.core.queryspec.QuerySpec;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

class CoordinateSystemConfigRepoIT extends CollectionModuleBaseIT {

  @Inject
  private CoordinateSystemConfigRepo configRepo;

  @Test
  void findAll() {
    MatcherAssert.assertThat(
      configRepo.findAll(new QuerySpec(CoordinateSystemConfigDto.class)).get(0).getCoordinateSystem(),
      Matchers.contains(
        "decimal degrees",
        "degrees decimal minutes",
        "degrees minutes seconds",
        "UTM"));
  }
}