package ca.gc.aafc.collection.api.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.info.BuildProperties;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyDto;
import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyItemDto;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabulary;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.collection.api.mapper.CollectionControlledVocabularyItemMapper;
import ca.gc.aafc.collection.api.security.SuperUserInGroupCUDAuthorizationService;
import ca.gc.aafc.collection.api.service.CollectionControlledVocabularyItemService;
import ca.gc.aafc.collection.api.service.CollectionControlledVocabularyService;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.exception.ResourcesGoneException;
import ca.gc.aafc.dina.exception.ResourcesNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiBulkDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiBulkResourceIdentifierDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.repository.DinaRepositoryV2;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.AuditService;
import ca.gc.aafc.dina.util.UUIDHelper;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import lombok.NonNull;

@RestController
@RequestMapping(value = "${dina.apiPrefix:}", produces = JSON_API_VALUE)
public class CollectionControlledVocabularyItemRepository extends DinaRepositoryV2<CollectionControlledVocabularyItemDto, CollectionControlledVocabularyItem> {

  // Bean does not exist with keycloak disabled.
  private final DinaAuthenticatedUser authenticatedUser;

  private final CollectionControlledVocabularyService collectionControlledVocabularyService;
  private final CollectionControlledVocabularyItemService collectionControlledVocabularyItemService;

  public CollectionControlledVocabularyItemRepository(
    @NonNull CollectionControlledVocabularyItemService dinaService,
    @NonNull CollectionControlledVocabularyService collectionControlledVocabularyService,
    @NonNull SuperUserInGroupCUDAuthorizationService authorizationService,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull BuildProperties props,
    @NonNull AuditService auditService,
    @NonNull ObjectMapper objMapper
  ) {
    super(
      dinaService, authorizationService,
      Optional.of(auditService),
      CollectionControlledVocabularyItemMapper.INSTANCE,
      CollectionControlledVocabularyItemDto.class,
      CollectionControlledVocabularyItem.class,
      props, objMapper);
    this.authenticatedUser = authenticatedUser.orElse(null);
    this.collectionControlledVocabularyItemService = dinaService;
    this.collectionControlledVocabularyService = collectionControlledVocabularyService;
  }

  @Override
  protected Link generateLinkToResource(CollectionControlledVocabularyItemDto dto) {
    try {
      return linkTo(methodOn(CollectionControlledVocabularyItemRepository.class).onFindOne(dto.getUuid().toString(), null)).withSelfRel();
    } catch (ResourceNotFoundException | ResourceGoneException e) {
      throw new RuntimeException(e);
    }
  }

  @GetMapping(CollectionControlledVocabularyItemDto.TYPENAME + "/{idOrKey}")
  public ResponseEntity<RepresentationModel<?>> onFindOne(@PathVariable String idOrKey, HttpServletRequest req)
      throws ResourceNotFoundException, ResourceGoneException {

    if (StringUtils.isBlank(idOrKey)) {
      throw ResourceNotFoundException.create(CollectionControlledVocabularyItemDto.TYPENAME, "");
    }

    Optional<UUID> id = UUIDHelper.toUUID(idOrKey);
    if (id.isPresent()) {
      return handleFindOne(id.get(), req);
    }

    // key is always a compound key vocabKey.itemKey[.dinaComponent]
    String[] keyParts = StringUtils.split(idOrKey, ".");

    if (keyParts.length == 2 || keyParts.length == 3) {
      CollectionControlledVocabulary vocab = collectionControlledVocabularyService.findOneByKey(keyParts[0]);
      if (vocab != null) {
        CollectionControlledVocabularyItem item = collectionControlledVocabularyItemService.findOneByKey(keyParts[1], vocab.getUuid(),
          keyParts.length == 3 ? keyParts[2] : null);
        return handleFindOne(item.getUuid(), req);
      }
    }
    throw ResourceNotFoundException.create(CollectionControlledVocabularyDto.TYPENAME, idOrKey);
  }

  @PostMapping(path = CollectionControlledVocabularyItemDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_LOAD_PATH,
    consumes = JSON_API_BULK)
  public ResponseEntity<RepresentationModel<?>> onBulkLoad(@RequestBody
                                                           JsonApiBulkResourceIdentifierDocument jsonApiBulkDocument,
                                                           HttpServletRequest req)
      throws ResourcesNotFoundException, ResourcesGoneException {
    return handleBulkLoad(jsonApiBulkDocument, req);
  }

  @GetMapping(CollectionControlledVocabularyItemDto.TYPENAME)
  public ResponseEntity<RepresentationModel<?>> onFindAll(HttpServletRequest req) {
    return handleFindAll(req);
  }

  @PostMapping(path = CollectionControlledVocabularyItemDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkCreate(@RequestBody
                                                             JsonApiBulkDocument jsonApiBulkDocument) {
    return handleBulkCreate(jsonApiBulkDocument, dto -> {
      if (authenticatedUser != null) {
        dto.setCreatedBy(authenticatedUser.getUsername());
      }
    });
  }

  @PostMapping(CollectionControlledVocabularyItemDto.TYPENAME)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onCreate(@RequestBody JsonApiDocument postedDocument) {

    return handleCreate(postedDocument, dto -> {
      if (authenticatedUser != null) {
        dto.setCreatedBy(authenticatedUser.getUsername());
      }
    });
  }

  @PatchMapping(path = CollectionControlledVocabularyItemDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkUpdate(@RequestBody JsonApiBulkDocument jsonApiBulkDocument)
      throws ResourceNotFoundException, ResourceGoneException {
    return handleBulkUpdate(jsonApiBulkDocument);
  }

  @PatchMapping(CollectionControlledVocabularyItemDto.TYPENAME + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onUpdate(@RequestBody JsonApiDocument partialPatchDto,
                                                         @PathVariable UUID id) throws ResourceNotFoundException, ResourceGoneException {
    return handleUpdate(partialPatchDto, id);
  }

  @DeleteMapping(path = CollectionControlledVocabularyItemDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkDelete(@RequestBody
                                                             JsonApiBulkResourceIdentifierDocument jsonApiBulkDocument)
      throws ResourceNotFoundException, ResourceGoneException {
    return handleBulkDelete(jsonApiBulkDocument);
  }

  @DeleteMapping(CollectionControlledVocabularyItemDto.TYPENAME + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onDelete(@PathVariable UUID id) throws ResourceNotFoundException, ResourceGoneException {
    return handleDelete(id);
  }
}
