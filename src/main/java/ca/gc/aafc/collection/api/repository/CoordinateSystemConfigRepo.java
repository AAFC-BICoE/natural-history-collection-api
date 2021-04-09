package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.LocationConfiguration;
import ca.gc.aafc.collection.api.dto.CoordinateSystemConfigDto;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ReadOnlyResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CoordinateSystemConfigRepo extends ReadOnlyResourceRepositoryBase<CoordinateSystemConfigDto, String> {

  private final List<CoordinateSystemConfigDto> configDto;

  protected CoordinateSystemConfigRepo(@NonNull LocationConfiguration locationConfiguration) {
    super(CoordinateSystemConfigDto.class);
    this.configDto = List.of(new CoordinateSystemConfigDto(locationConfiguration.getCoordinateSystem()));
  }

  @Override
  public ResourceList<CoordinateSystemConfigDto> findAll(QuerySpec querySpec) {
    return querySpec.apply(configDto);
  }
}
