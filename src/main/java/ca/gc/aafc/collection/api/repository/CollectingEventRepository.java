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

import ca.gc.aafc.collection.api.datetime.IsoDateTimeFilterComponentHandler;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.mapper.CollectingEventMapper;
import ca.gc.aafc.collection.api.mapper.ExternalRelationshipMapper;
import ca.gc.aafc.collection.api.service.CollectingEventService;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.JsonApiExternalResource;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.exception.ResourcesGoneException;
import ca.gc.aafc.dina.exception.ResourcesNotFoundException;
import ca.gc.aafc.dina.filter.FilterComponent;
import ca.gc.aafc.dina.filter.FilterComponentMutator;
import ca.gc.aafc.dina.filter.FilterExpression;
import ca.gc.aafc.dina.filter.QueryComponent;
import ca.gc.aafc.dina.jsonapi.JsonApiBulkDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiBulkResourceIdentifierDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.mapper.DinaMappingRegistry;
import ca.gc.aafc.dina.repository.DinaRepositoryV2;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.TextHtmlSanitizer;
import ca.gc.aafc.dina.security.auth.DinaAuthorizationService;
import ca.gc.aafc.dina.service.AuditService;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.NonNull;

@RestController
@RequestMapping(value = "${dina.apiPrefix:}", produces = JSON_API_VALUE)
public class CollectingEventRepository extends DinaRepositoryV2<CollectingEventDto, CollectingEvent> {

  // Bean does not exist with keycloak disabled.
  private final DinaAuthenticatedUser dinaAuthenticatedUser;

  private static final Map<String, CollectingEvent.ISODateTimeAttribute>
    DATE_TIME_TRANSFORMATION_ATTRIBUTES = CollectingEvent.ISO_DATETIME_ATTRIBUTES.stream()
      .collect(Collectors.toMap(CollectingEvent.ISODateTimeAttribute::attribute, a -> a));

  public CollectingEventRepository(
    @NonNull CollectingEventService dinaService,
    DinaAuthorizationService groupAuthorizationService,
    @NonNull AuditService auditService,
    @NonNull BuildProperties props,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    ObjectMapper objectMapper
  ) {
    super(
      dinaService,
      groupAuthorizationService,
      Optional.of(auditService),
      CollectingEventMapper.INSTANCE,
      CollectingEventDto.class,
      CollectingEvent.class,
      props, objectMapper, new DinaMappingRegistry(CollectingEventDto.class, true));

    this.dinaAuthenticatedUser = authenticatedUser.orElse(null);
  }

  @Override
  protected Link generateLinkToResource(CollectingEventDto dto) {
    try {
      return linkTo(methodOn(CollectingEventRepository.class).onFindOne(dto.getUuid(), null)).withSelfRel();
    } catch (ResourceNotFoundException | ResourceGoneException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected QueryComponent transformQueryComponent(QueryComponent queryComponent) {

    Predicate<FilterComponent> predicate = component -> component instanceof FilterExpression expr &&
      DATE_TIME_TRANSFORMATION_ATTRIBUTES.containsKey(expr.attribute());

    FilterComponentMutator.FilterComponentMutation mutation = component -> {
      if (component instanceof FilterExpression expr) {

        return switch (expr.operator()) {
          case EQ -> IsoDateTimeFilterComponentHandler.equal(expr, DATE_TIME_TRANSFORMATION_ATTRIBUTES.get(expr.attribute()).precisionAttribute());
          case GT -> IsoDateTimeFilterComponentHandler.greater(expr);
          case GOE -> IsoDateTimeFilterComponentHandler.greaterOrEqual(expr, DATE_TIME_TRANSFORMATION_ATTRIBUTES.get(expr.attribute()).precisionAttribute());
          case LT -> IsoDateTimeFilterComponentHandler.less(expr);
          case LOE -> IsoDateTimeFilterComponentHandler.lessOrEqual(expr, DATE_TIME_TRANSFORMATION_ATTRIBUTES.get(expr.attribute()).precisionAttribute(),
            DATE_TIME_TRANSFORMATION_ATTRIBUTES.get(expr.attribute()).endAttribute());
          default -> null;
        };
      }
      return component;
    };

    return FilterComponentMutator.mutate(
      queryComponent,
      predicate,
      mutation
    );
  }

  @Override
  protected JsonApiExternalResource externalRelationDtoToJsonApiExternalResource(ExternalRelationDto externalRelationDto) {
    return ExternalRelationshipMapper.externalRelationDtoToJsonApiExternalResource(externalRelationDto);
  }

  @PostMapping(path = CollectingEventDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_LOAD_PATH, consumes = JSON_API_BULK)
  public ResponseEntity<RepresentationModel<?>> onBulkLoad(@RequestBody
                                                           JsonApiBulkResourceIdentifierDocument jsonApiBulkDocument,
                                                           HttpServletRequest req)
      throws ResourcesNotFoundException, ResourcesGoneException {
    return handleBulkLoad(jsonApiBulkDocument, req);
  }

  @GetMapping(CollectingEventDto.TYPENAME + "/{id}")
  public ResponseEntity<RepresentationModel<?>> onFindOne(@PathVariable UUID id, HttpServletRequest req)
      throws ResourceNotFoundException, ResourceGoneException {
    return handleFindOne(id, req);
  }

  @GetMapping(CollectingEventDto.TYPENAME)
  public ResponseEntity<RepresentationModel<?>> onFindAll(HttpServletRequest req) {
    return handleFindAll(req);
  }

  @PostMapping(path = CollectingEventDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkCreate(@RequestBody
                                                             JsonApiBulkDocument jsonApiBulkDocument) {
    return handleBulkCreate(jsonApiBulkDocument, dto -> {
      if (dinaAuthenticatedUser != null) {
        dto.setCreatedBy(dinaAuthenticatedUser.getUsername());
      }
    });
  }

  @PostMapping(CollectingEventDto.TYPENAME)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onCreate(@RequestBody JsonApiDocument postedDocument) {
    return handleCreate(postedDocument, dto -> {
      if (dinaAuthenticatedUser != null) {
        dto.setCreatedBy(dinaAuthenticatedUser.getUsername());
      }
    });
  }

  @PatchMapping(CollectingEventDto.TYPENAME + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onUpdate(@RequestBody JsonApiDocument partialPatchDto,
                                                         @PathVariable UUID id) throws ResourceNotFoundException, ResourceGoneException {
    return handleUpdate(partialPatchDto, id);
  }

  @PatchMapping(path = CollectingEventDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkUpdate(@RequestBody JsonApiBulkDocument jsonApiBulkDocument)
      throws ResourceNotFoundException, ResourceGoneException {
    return handleBulkUpdate(jsonApiBulkDocument);
  }

  @DeleteMapping(path = CollectingEventDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkDelete(@RequestBody
                                                             JsonApiBulkResourceIdentifierDocument jsonApiBulkDocument)
      throws ResourceNotFoundException, ResourceGoneException {
    return handleBulkDelete(jsonApiBulkDocument);
  }

  @DeleteMapping(CollectingEventDto.TYPENAME + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onDelete(@PathVariable UUID id) throws ResourceNotFoundException, ResourceGoneException {
    return handleDelete(id);
  }

  @Override
  protected Predicate<String> supplyCheckSubmittedDataPredicate() {
    return txt -> TextHtmlSanitizer.isSafeText(txt) || TextHtmlSanitizer.isAcceptableText(txt);
  }
}
