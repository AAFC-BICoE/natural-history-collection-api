package ca.gc.aafc.collection.api.repository;

import org.springframework.boot.info.BuildProperties;
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

import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.collection.api.mapper.StorageUnitTypeMapper;
import ca.gc.aafc.collection.api.service.StorageUnitTypeService;
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
import javax.servlet.http.HttpServletRequest;
import lombok.NonNull;

@RestController
@RequestMapping(value = "${dina.apiPrefix:}", produces = JSON_API_VALUE)
public class StorageUnitTypeRepo extends DinaRepositoryV2<StorageUnitTypeDto, StorageUnitType> {

  private final DinaAuthenticatedUser dinaAuthenticatedUser;

  public StorageUnitTypeRepo(
    @NonNull StorageUnitTypeService service,
    DinaAuthorizationService groupAuthorizationService,
    @NonNull AuditService auditService,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull BuildProperties buildProperties,
    ObjectMapper objectMapper
  ) {
    super(
      service,
      groupAuthorizationService,
      Optional.of(auditService),
      StorageUnitTypeMapper.INSTANCE,
      StorageUnitTypeDto.class,
      StorageUnitType.class,
      buildProperties, objectMapper);
    this.dinaAuthenticatedUser = authenticatedUser.orElse(null);
  }

  @Override
  protected Link generateLinkToResource(StorageUnitTypeDto dto) {
    try {
      return linkTo(methodOn(StorageUnitTypeRepo.class).onFindOne(dto.getUuid(), null)).withSelfRel();
    } catch (ResourceNotFoundException | ResourceGoneException e) {
      throw new RuntimeException(e);
    }
  }

  @PostMapping(path = StorageUnitTypeDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_LOAD_PATH, consumes = JSON_API_BULK)
  public ResponseEntity<RepresentationModel<?>> onBulkLoad(@RequestBody
                                                           JsonApiBulkResourceIdentifierDocument jsonApiBulkDocument,
                                                           HttpServletRequest req)
      throws ResourcesNotFoundException, ResourcesGoneException {
    return handleBulkLoad(jsonApiBulkDocument, req);
  }

  @GetMapping(StorageUnitTypeDto.TYPENAME + "/{id}")
  public ResponseEntity<RepresentationModel<?>> onFindOne(@PathVariable UUID id, HttpServletRequest req)
      throws ResourceNotFoundException, ResourceGoneException {
    return handleFindOne(id, req);
  }

  @GetMapping(StorageUnitTypeDto.TYPENAME)
  public ResponseEntity<RepresentationModel<?>> onFindAll(HttpServletRequest req) {
    return handleFindAll(req);
  }

  @PostMapping(path = StorageUnitTypeDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkCreate(@RequestBody
                                                             JsonApiBulkDocument jsonApiBulkDocument) {
    return handleBulkCreate(jsonApiBulkDocument, dto -> {
      if (dinaAuthenticatedUser != null) {
        dto.setCreatedBy(dinaAuthenticatedUser.getUsername());
      }
    });
  }

  @PostMapping(StorageUnitTypeDto.TYPENAME)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onCreate(@RequestBody JsonApiDocument postedDocument) {
    return handleCreate(postedDocument, dto -> {
      if (dinaAuthenticatedUser != null) {
        dto.setCreatedBy(dinaAuthenticatedUser.getUsername());
      }
    });
  }

  @PatchMapping(StorageUnitTypeDto.TYPENAME + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onUpdate(@RequestBody JsonApiDocument partialPatchDto,
                                                         @PathVariable UUID id) throws ResourceNotFoundException, ResourceGoneException {
    return handleUpdate(partialPatchDto, id);
  }

  @PatchMapping(path = StorageUnitTypeDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkUpdate(@RequestBody JsonApiBulkDocument jsonApiBulkDocument)
      throws ResourceNotFoundException, ResourceGoneException {
    return handleBulkUpdate(jsonApiBulkDocument);
  }

  @DeleteMapping(path = StorageUnitTypeDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkDelete(@RequestBody
                                                             JsonApiBulkResourceIdentifierDocument jsonApiBulkDocument)
      throws ResourceNotFoundException, ResourceGoneException {
    return handleBulkDelete(jsonApiBulkDocument);
  }

  @DeleteMapping(StorageUnitTypeDto.TYPENAME + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onDelete(@PathVariable UUID id) throws ResourceNotFoundException, ResourceGoneException {
    return handleDelete(id);
  }
}
