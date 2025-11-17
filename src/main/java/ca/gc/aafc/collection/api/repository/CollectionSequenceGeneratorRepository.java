package ca.gc.aafc.collection.api.repository;

import org.springframework.boot.info.BuildProperties;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;

import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.dto.CollectionSequenceGeneratorDto;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.CollectionSequenceGenerationRequest;
import ca.gc.aafc.collection.api.mapper.CollectionSequenceGeneratorMapper;
import ca.gc.aafc.collection.api.service.CollectionSequenceGeneratorService;
import ca.gc.aafc.collection.api.service.CollectionService;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiImmutable;
import ca.gc.aafc.dina.repository.DinaRepositoryV2;
import ca.gc.aafc.dina.security.auth.DinaAuthorizationService;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;

@RestController
@RequestMapping(value = "${dina.apiPrefix:}", produces = JSON_API_VALUE)
public class CollectionSequenceGeneratorRepository extends DinaRepositoryV2<CollectionSequenceGeneratorDto, CollectionSequenceGenerationRequest> {

  private final CollectionSequenceGeneratorService collectionSequenceGeneratorService;
  private final CollectionService collectionService;
  private final DinaAuthorizationService groupAuthorizationService;

  public CollectionSequenceGeneratorRepository(
    @NonNull CollectionSequenceGeneratorService dinaService,
    CollectionService collectionService,
    DinaAuthorizationService groupAuthorizationService,
    @NonNull BuildProperties buildProperties,
    ObjectMapper objectMapper
  ) {
    super(
      dinaService,
      groupAuthorizationService,
      Optional.empty(),
      CollectionSequenceGeneratorMapper.INSTANCE,
      CollectionSequenceGeneratorDto.class,
      CollectionSequenceGenerationRequest.class,
      buildProperties, objectMapper);

    this.collectionSequenceGeneratorService = dinaService;
    this.collectionService = collectionService;
    this.groupAuthorizationService = groupAuthorizationService;
  }

  @PostMapping(CollectionSequenceGeneratorDto.TYPENAME)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onCreate(@RequestBody JsonApiDocument postedDocument)
      throws ResourceNotFoundException {

    this.checkSubmittedData(postedDocument.getAttributes());
    CollectionSequenceGeneratorDto dto = objMapper.convertValue(postedDocument.getAttributes(), CollectionSequenceGeneratorDto.class);

    // if no collectionId was provided use the document id
    if (dto.getCollectionId() == null && postedDocument.getId() != null) {
      dto.setCollectionId(postedDocument.getId());
    }

    // Retrieve the collections group to use to check if the current user has permission to perform this.
    Collection collection = collectionService.findOne(dto.getCollectionId(), Collection.class);
    if (collection == null) {
      throw ResourceNotFoundException.create(CollectionDto.TYPENAME, dto.getCollectionId());
    }
    dto.setGroup(collection.getGroup());

    Set<String> attributesToConsider = new HashSet<>(this.registry.getAttributesPerClass().get(CollectionSequenceGeneratorDto.class));
    attributesToConsider.removeAll(this.registry.getImmutableAttributesForClass(CollectionSequenceGeneratorDto.class, JsonApiImmutable.ImmutableOn.CREATE));
    CollectionSequenceGenerationRequest entity = CollectionSequenceGeneratorMapper.INSTANCE.toEntity(dto, attributesToConsider, null);
    groupAuthorizationService.authorizeCreate(entity);
    collectionSequenceGeneratorService.create(entity);

    // Return the resource back to the results from the service.
    dto.setResult(entity.getResult());

    var jsonDto = this.jsonApiDtoAssistant.toJsonApiDto(dto, Map.of(dto.getJsonApiType(), registry.getAttributesPerClass().get(CollectionSequenceGeneratorDto.class).stream().toList()), Set.of());

    JsonApiModelBuilder builder = this.jsonApiModelAssistant.createJsonApiModelBuilder(jsonDto);
    RepresentationModel<?> model = builder.build();
    URI uri = URI.create(dto.getJsonApiType());
    return ResponseEntity.created(uri).body(model);
  }
}
