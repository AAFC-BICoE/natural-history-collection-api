package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.exceptionmapping.PersistenceExceptionMapper;
import ca.gc.aafc.collection.api.service.StorageUnitService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.DinaAuthorizationService;
import io.crnk.core.exception.BadRequestException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.resource.list.ResourceList;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

@Repository
public class StorageUnitRepo extends DinaRepository<StorageUnitDto, StorageUnit> {

  public static final String HIERARCHY_INCLUDE_PARAM = "hierarchy";
  private Optional<DinaAuthenticatedUser> authenticatedUser;

  public StorageUnitRepo(
    @NonNull StorageUnitService sus,
    DinaAuthorizationService groupAuthorizationService,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull BuildProperties buildProperties
  ) {
    super(
      sus,
      groupAuthorizationService,
      Optional.empty(),
      new DinaMapper<>(StorageUnitDto.class),
      StorageUnitDto.class,
      StorageUnit.class,
      null,
      null,
      buildProperties);
    this.authenticatedUser = authenticatedUser;
  }

  @Override
  public <S extends StorageUnitDto> S create(S resource) {
    authenticatedUser.ifPresent(user -> resource.setCreatedBy(user.getUsername()));
    return checkHierarchyViolation(() -> super.create(resource));
  }

  @Override
  public <S extends StorageUnitDto> S save(S resource) {
    return checkHierarchyViolation(() -> super.save(resource));
  }

  private <S extends StorageUnitDto> S checkHierarchyViolation(Supplier<S> operation) {
    try {
      return operation.get();
    } catch (PersistenceException e) {
      if (PersistenceExceptionMapper.isHierarchyViolation(e)) {
        throw new BadRequestException(""); //TODO get message from exception
      } else {
        throw e;
      }
    }
  }

  @Override
  public ResourceList<StorageUnitDto> findAll(Collection<Serializable> ids, QuerySpec querySpec) {
    ResourceList<StorageUnitDto> resourceList = super.findAll(ids, querySpec);
    if (CollectionUtils.isNotEmpty(resourceList) && !isHierarchyIncluded(querySpec)) {
      resourceList.forEach(r -> r.setHierarchy(null));
    }
    return resourceList;
  }

  private static boolean isHierarchyIncluded(QuerySpec querySpec) {
    return querySpec.getIncludedRelations().stream()
      .anyMatch(r -> r.getAttributePath().get(0).equalsIgnoreCase(HIERARCHY_INCLUDE_PARAM));
  }
}
