package ca.gc.aafc.collection.api.repository;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.MaterialSampleCounterDto;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleCounter;
import ca.gc.aafc.collection.api.service.MaterialSampleCounterService;
import ca.gc.aafc.dina.security.DinaAuthorizationService;
import ca.gc.aafc.dina.security.TextHtmlSanitizer;

import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

/**
 * The counter works with POST (create). The logic is that a PATCH/PUT will record the data
 * you provide but the counter will always advance, you can't set its values.
 */
@Repository
public class MaterialSampleCounterRepository implements ResourceRepository<MaterialSampleCounterDto, Serializable> {

  private static final int MAX_COUNTER_INCREMENT = 500;

  private final MaterialSampleCounterService materialSampleCounterService;
  private final DinaAuthorizationService groupAuthorizationService;

  public MaterialSampleCounterRepository(
    MaterialSampleCounterService materialSampleCounterService,
    DinaAuthorizationService groupAuthorizationService
  ) {
    this.materialSampleCounterService = materialSampleCounterService;
    this.groupAuthorizationService = groupAuthorizationService;
  }

  @Override
  public Class<MaterialSampleCounterDto> getResourceClass() {
    return MaterialSampleCounterDto.class;
  }

  @Override
  public MaterialSampleCounterDto findOne(Serializable serializable, QuerySpec querySpec) {
    return null;
  }

  @Override
  public ResourceList<MaterialSampleCounterDto> findAll(QuerySpec querySpec) {
    return null;
  }

  @Override
  public ResourceList<MaterialSampleCounterDto> findAll(Collection<Serializable> collection,
                                                        QuerySpec querySpec) {
    return null;
  }

  @Override
  public <S extends MaterialSampleCounterDto> S save(S s) {
    return null;
  }

  @Override
  public <S extends MaterialSampleCounterDto> S create(S counterDto) {
    // counter name is the only String in the DTO
    if(!TextHtmlSanitizer.isSafeText(counterDto.getCounterName())) {
      throw new IllegalArgumentException("unsafe value detected in attribute");
    }

    int amount = counterDto.getAmount() == null ? 1 : counterDto.getAmount();
    if(amount > MAX_COUNTER_INCREMENT) {
      throw new IllegalArgumentException("over maximum amount");
    }

    // find the matching material-sample
    MaterialSample ms = materialSampleCounterService.findOne(counterDto.getMaterialSampleUUID(), MaterialSample.class);

    if (ms == null) {
      throw new ResourceNotFoundException(
        "material-sample with ID " + counterDto.getMaterialSampleUUID() + " Not Found.");
    }

    // check authorization using the material-sample since it has the group populated
    groupAuthorizationService.authorizeCreate(ms);

    // Try to find an existing MaterialSampleCounter
    MaterialSampleCounter msCounter = materialSampleCounterService.findOneByMaterialSampleCounterName(ms.getId(),
      counterDto.getCounterName());

    // if the entry doesn't exist, create it
    if (msCounter == null) {
      msCounter = materialSampleCounterService.create(
        buildMaterialSampleCounter(ms.getId(), counterDto.getCounterName()));
    }

    MaterialSampleCounter.IncrementFunctionOutput output =
      materialSampleCounterService.incrementCounter(msCounter.getId(), counterDto.getAmount());

    counterDto.setResult(output);
    //Set a UUID representing the increment operation. It will not be saved.
    counterDto.setUuid(UUID.randomUUID());

    return counterDto;
  }

  // delete is handled by material-sample
  @Override
  public void delete(Serializable serializable) {
    throw new MethodNotAllowedException("DELETE");
  }

  private static MaterialSampleCounter buildMaterialSampleCounter(Integer materialSampleId, String counterName) {
    MaterialSampleCounter msc = new MaterialSampleCounter();
    msc.setMaterialSampleId(materialSampleId);
    msc.setCounterName(counterName);
    return msc;
  }
}
