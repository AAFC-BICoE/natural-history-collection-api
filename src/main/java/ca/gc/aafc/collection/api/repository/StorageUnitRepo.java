package ca.gc.aafc.collection.api.repository;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.exceptionmapping.HierarchyExceptionMappingUtils;
import ca.gc.aafc.collection.api.mapper.StorageUnitMapper;
import ca.gc.aafc.collection.api.service.StorageUnitService;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.exception.ResourcesGoneException;
import ca.gc.aafc.dina.exception.ResourcesNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiBulkDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiBulkResourceIdentifierDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.repository.DinaRepositoryV2;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.auth.DinaAuthorizationService;
import ca.gc.aafc.dina.service.AuditService;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import lombok.NonNull;

@RestController
@RequestMapping(value = "${dina.apiPrefix:}", produces = JSON_API_VALUE)
public class StorageUnitRepo extends DinaRepositoryV2<StorageUnitDto, StorageUnit> {

  public static final String HIERARCHY_INCLUDE_PARAM = "hierarchy";

  private final DinaAuthenticatedUser dinaAuthenticatedUser;

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
      StorageUnitMapper.INSTANCE,
      StorageUnitDto.class,
      StorageUnit.class,
      buildProperties, objectMapper);
    this.dinaAuthenticatedUser = authenticatedUser.orElse(null);
    this.messageSource = messageSource;
  }

  @Override
  protected Link generateLinkToResource(StorageUnitDto dto) {
    try {
      return linkTo(methodOn(StorageUnitRepo.class).onFindOne(dto.getUuid(), null)).withSelfRel();
    } catch (ResourceNotFoundException | ResourceGoneException e) {
      throw new RuntimeException(e);
    }
  }

  @PostMapping(path = StorageUnitDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_LOAD_PATH, consumes = JSON_API_BULK)
  public ResponseEntity<RepresentationModel<?>> onBulkLoad(@RequestBody
                                                           JsonApiBulkResourceIdentifierDocument jsonApiBulkDocument,
                                                           HttpServletRequest req)
    throws ResourcesNotFoundException, ResourcesGoneException {
    return handleBulkLoad(jsonApiBulkDocument, req);
  }

  @GetMapping(StorageUnitDto.TYPENAME + "/{id}")
  public ResponseEntity<RepresentationModel<?>> onFindOne(@PathVariable UUID id, HttpServletRequest req)
    throws ResourceNotFoundException, ResourceGoneException {
    return handleFindOne(id, req);
  }

  @GetMapping(StorageUnitDto.TYPENAME)
  public ResponseEntity<RepresentationModel<?>> onFindAll(HttpServletRequest req) {
    return handleFindAll(req);
  }

  @PostMapping(path = StorageUnitDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkCreate(@RequestBody
                                                             JsonApiBulkDocument jsonApiBulkDocument) {
    return handleBulkCreate(jsonApiBulkDocument, dto -> {
      if (dinaAuthenticatedUser != null) {
        dto.setCreatedBy(dinaAuthenticatedUser.getUsername());
      }
    });
  }

  @PostMapping(StorageUnitDto.TYPENAME)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onCreate(@RequestBody JsonApiDocument postedDocument) {
    try {
      return handleCreate(postedDocument, dto -> {
        if (dinaAuthenticatedUser != null) {
          dto.setCreatedBy(dinaAuthenticatedUser.getUsername());
        }
      });
    } catch (PersistenceException e) {
      HierarchyExceptionMappingUtils.throwIfHierarchyViolation(e,
        key -> messageSource.getMessage(key, null, LocaleContextHolder.getLocale()));
      throw e;
    }
  }

  @PatchMapping(StorageUnitDto.TYPENAME + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onUpdate(@RequestBody JsonApiDocument partialPatchDto,
                                                         @PathVariable UUID id) throws ResourceNotFoundException, ResourceGoneException {
    return handleUpdate(partialPatchDto, id);
  }

  @PatchMapping(path = StorageUnitDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkUpdate(@RequestBody JsonApiBulkDocument jsonApiBulkDocument)
    throws ResourceNotFoundException, ResourceGoneException {
    return handleBulkUpdate(jsonApiBulkDocument);
  }

  @DeleteMapping(path = StorageUnitDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkDelete(@RequestBody
                                                             JsonApiBulkResourceIdentifierDocument jsonApiBulkDocument)
      throws ResourceNotFoundException, ResourceGoneException {
    return handleBulkDelete(jsonApiBulkDocument);
  }

  @DeleteMapping(StorageUnitDto.TYPENAME + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onDelete(@PathVariable UUID id) throws ResourceNotFoundException, ResourceGoneException {
    return handleDelete(id);
  }

//  @Override
//  public <S extends StorageUnitDto> S create(S resource) {
//    authenticatedUser.ifPresent(user -> resource.setCreatedBy(user.getUsername()));
//    return checkForHierarchyViolation(() -> super.create(resource));
//  }
//
//  @Override
//  public <S extends StorageUnitDto> S save(S resource) {
//    return checkForHierarchyViolation(() -> super.save(resource));
//  }

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
