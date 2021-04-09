package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.LocationConfiguration;
import ca.gc.aafc.collection.api.dto.SrsConfigDto;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ReadOnlyResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SrsConfigRepo extends ReadOnlyResourceRepositoryBase<SrsConfigDto, String> {

  private final List<SrsConfigDto> configDto;

  protected SrsConfigRepo(@NonNull LocationConfiguration locationConfiguration) {
    super(SrsConfigDto.class);
    this.configDto = List.of(new SrsConfigDto(locationConfiguration.getSrs()));
  }

  @Override
  public ResourceList<SrsConfigDto> findAll(QuerySpec querySpec) {
    return querySpec.apply(configDto);
  }

}

