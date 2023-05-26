package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.MaterialSampleSummaryDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleSummary;
import ca.gc.aafc.collection.api.service.MaterialSampleSummaryService;

import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ReadOnlyResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * MaterialSampleSummary loads a minimum set of data and returns the effective determination.
 *
 */
@Repository
public class MaterialSampleSummaryRepository extends ReadOnlyResourceRepositoryBase<MaterialSampleSummaryDto, UUID> {

  private final MaterialSampleSummaryService mssService;

  protected MaterialSampleSummaryRepository(MaterialSampleSummaryService mssService) {
    super(MaterialSampleSummaryDto.class);
    this.mssService = mssService;
  }

  @Transactional(
    readOnly = true
  )
  @Override
  public MaterialSampleSummaryDto findOne(UUID uuid, QuerySpec querySpec) {
    MaterialSampleSummary mss = mssService.findMaterialSampleSummary(uuid);

    if (mss == null) {
      throw new ResourceNotFoundException("MaterialSampleSummary with ID " + uuid + " Not Found.");
    }

    return MaterialSampleSummaryDto.builder()
      .uuid(mss.getUuid())
      .materialSampleName(mss.getMaterialSampleName())
      .effectiveDetermination(mss.getEffectiveDetermination())
      .build();
  }

  @Override
  public ResourceList<MaterialSampleSummaryDto> findAll(QuerySpec querySpec) {
    throw new MethodNotAllowedException("method not allowed");
  }
}
