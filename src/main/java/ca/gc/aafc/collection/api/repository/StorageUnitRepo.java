package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.exceptionmapping.HierarchyExceptionMappingUtils;
import ca.gc.aafc.collection.api.service.StorageUnitService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.auth.DinaAuthorizationService;
import ca.gc.aafc.dina.service.AuditService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceException;
import java.util.Optional;
import java.util.function.Supplier;

@Repository
public class StorageUnitRepo extends DinaRepository<StorageUnitDto, StorageUnit> {

  public static final String HIERARCHY_INCLUDE_PARAM = "hierarchy";
  private Optional<DinaAuthenticatedUser> authenticatedUser;
  private final MessageSource messageSource;

  public StorageUnitRepo(
    @NonNull StorageUnitService sus,
    DinaAuthorizationService groupAuthorizationService,
    @NonNull AuditService auditService,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull BuildProperties buildProperties,
    MessageSource messageSource, ObjectMapper objectMapper
  ) {
    super(
      sus,
      groupAuthorizationService,
      Optional.of(auditService),
      new DinaMapper<>(StorageUnitDto.class),
      StorageUnitDto.class,
      StorageUnit.class,
      null,
      null,
      buildProperties, objectMapper);
    this.authenticatedUser = authenticatedUser;
    this.messageSource = messageSource;
  }

  @Override
  public <S extends StorageUnitDto> S create(S resource) {
    authenticatedUser.ifPresent(user -> resource.setCreatedBy(user.getUsername()));
    return checkForHierarchyViolation(() -> super.create(resource));
  }

  @Override
  public <S extends StorageUnitDto> S save(S resource) {
    return checkForHierarchyViolation(() -> super.save(resource));
  }

  private <S extends StorageUnitDto> S checkForHierarchyViolation(Supplier<S> operation) {
    try {
      return operation.get();
    } catch (PersistenceException e) {
      HierarchyExceptionMappingUtils.throwIfHierarchyViolation(e,
          key -> messageSource.getMessage(key, null, LocaleContextHolder.getLocale()));
      throw e;
    }
  }
}
