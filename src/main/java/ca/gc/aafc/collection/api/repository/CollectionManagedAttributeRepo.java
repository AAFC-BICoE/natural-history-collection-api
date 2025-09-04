package ca.gc.aafc.collection.api.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.mapper.CollectionManagedAttributeMapper;
import ca.gc.aafc.collection.api.security.SuperUserInGroupCUDAuthorizationService;
import ca.gc.aafc.collection.api.service.CollectionManagedAttributeService;
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
import ca.gc.aafc.dina.security.TextHtmlSanitizer;
import lombok.NonNull;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "${dina.apiPrefix:}", produces = JSON_API_VALUE)
public class CollectionManagedAttributeRepo extends 
    DinaRepositoryV2<CollectionManagedAttributeDto, CollectionManagedAttribute> {

  public static final Pattern KEY_LOOKUP_PATTERN = Pattern.compile("(.*)\\.(.*)");

  private final CollectionManagedAttributeService dinaService;

  // Bean does not exist with keycloak disabled.
  private final DinaAuthenticatedUser authenticatedUser;

  public CollectionManagedAttributeRepo(
    @NonNull CollectionManagedAttributeService dinaService,
    @NonNull SuperUserInGroupCUDAuthorizationService authorizationService,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull BuildProperties props,
    @NonNull AuditService auditService,
    @NonNull ObjectMapper objMapper
  ) {
    super(
      dinaService, authorizationService,
      Optional.of(auditService),
      CollectionManagedAttributeMapper.INSTANCE,
      CollectionManagedAttributeDto.class,
      CollectionManagedAttribute.class,
      props, objMapper);
    this.authenticatedUser = authenticatedUser.orElse(null);
    this.dinaService = dinaService;
  }

  @Override
  protected Link generateLinkToResource(CollectionManagedAttributeDto dto) {
    try {
      return linkTo(methodOn(CollectionManagedAttributeRepo.class).onFindOne(dto.getUuid().toString(), null)).withSelfRel();
    } catch (ResourceNotFoundException | ResourceGoneException e) {
      throw new RuntimeException(e);
    }
  }

  @GetMapping(CollectionManagedAttributeDto.TYPENAME + "/{id}")
  public ResponseEntity<RepresentationModel<?>> onFindOne(@PathVariable String id, HttpServletRequest req)
      throws ResourceNotFoundException, ResourceGoneException {

    // Allow lookup by component type + key.
    // e.g. collecting_event.attribute_name
    var matcher = KEY_LOOKUP_PATTERN.matcher(id);
    if (matcher.groupCount() == 2) {
      if (matcher.find()) {
        CollectionManagedAttribute.ManagedAttributeComponent component =
          CollectionManagedAttribute.ManagedAttributeComponent
            .fromString(matcher.group(1));
        String attributeKey = matcher.group(2);

        CollectionManagedAttribute managedAttribute =
          dinaService.findOneByKeyAndComponent(attributeKey, component);

        if (managedAttribute != null) {
          return handleFindOne(managedAttribute.getUuid(), req);
        } else {
          throw ResourceNotFoundException.create(CollectionManagedAttributeDto.TYPENAME,
            TextHtmlSanitizer.sanitizeText(id));
        }
      }
    }
    return handleFindOne(UUID.fromString(id), req);
  }

  @PostMapping(path = CollectionManagedAttributeDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_LOAD_PATH,
    consumes = JSON_API_BULK)
  public ResponseEntity<RepresentationModel<?>> onBulkLoad(@RequestBody
                                                           JsonApiBulkResourceIdentifierDocument jsonApiBulkDocument,
                                                           HttpServletRequest req)
      throws ResourcesNotFoundException, ResourcesGoneException {
    return handleBulkLoad(jsonApiBulkDocument, req);
  }

  @GetMapping(CollectionManagedAttributeDto.TYPENAME)
  public ResponseEntity<RepresentationModel<?>> onFindAll(HttpServletRequest req) {
    return handleFindAll(req);
  }

  @PostMapping(path = CollectionManagedAttributeDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkCreate(@RequestBody
                                                             JsonApiBulkDocument jsonApiBulkDocument) {
    return handleBulkCreate(jsonApiBulkDocument, dto -> {
      if (authenticatedUser != null) {
        dto.setCreatedBy(authenticatedUser.getUsername());
      }
    });
  }

  @PostMapping(CollectionManagedAttributeDto.TYPENAME)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onCreate(@RequestBody JsonApiDocument postedDocument) {

    return handleCreate(postedDocument, dto -> {
      if (authenticatedUser != null) {
        dto.setCreatedBy(authenticatedUser.getUsername());
      }
    });
  }

  @PatchMapping(path = CollectionManagedAttributeDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkUpdate(@RequestBody JsonApiBulkDocument jsonApiBulkDocument)
      throws ResourceNotFoundException, ResourceGoneException {
    return handleBulkUpdate(jsonApiBulkDocument);
  }

  @PatchMapping(CollectionManagedAttributeDto.TYPENAME + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onUpdate(@RequestBody JsonApiDocument partialPatchDto,
                                                         @PathVariable UUID id) throws ResourceNotFoundException, ResourceGoneException {
    return handleUpdate(partialPatchDto, id);
  }

  @DeleteMapping(path = CollectionManagedAttributeDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkDelete(@RequestBody
                                                             JsonApiBulkResourceIdentifierDocument jsonApiBulkDocument)
      throws ResourceNotFoundException, ResourceGoneException {
    return handleBulkDelete(jsonApiBulkDocument);
  }

  @DeleteMapping(CollectionManagedAttributeDto.TYPENAME + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onDelete(@PathVariable UUID id) throws ResourceNotFoundException, ResourceGoneException {
    return handleDelete(id);
  }
}
