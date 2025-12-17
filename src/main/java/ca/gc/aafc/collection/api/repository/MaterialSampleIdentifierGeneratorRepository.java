package ca.gc.aafc.collection.api.repository;

import org.apache.commons.collections4.CollectionUtils;
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

import ca.gc.aafc.collection.api.dto.MaterialSampleIdentifierGeneratorDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleNameGeneration;
import ca.gc.aafc.collection.api.service.MaterialSampleIdentifierGenerator;
import ca.gc.aafc.dina.dto.JsonApiDto;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.repository.JsonApiModelAssistant;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Used to generate next identifier based on current value.
 * Identifiers are only generated, they are not reserved and not guarantee to be unique.
 */
@RestController
@RequestMapping(value = "${dina.apiPrefix:}", produces = JSON_API_VALUE)
public class MaterialSampleIdentifierGeneratorRepository {

  private static final int MAX_GENERATION_QTY = 500;

  private final ObjectMapper objectMapper;
  private final MaterialSampleIdentifierGenerator identifierGenerator;
  private final JsonApiModelAssistant<MaterialSampleIdentifierGeneratorDto> jsonApiModelAssistant;

  public MaterialSampleIdentifierGeneratorRepository(MaterialSampleIdentifierGenerator identifierGenerator,
                                                     BuildProperties buildProperties,
                                                     ObjectMapper objectMapper) {
    this.identifierGenerator = identifierGenerator;
    this.objectMapper = objectMapper;
    this.jsonApiModelAssistant = new JsonApiModelAssistant<>(buildProperties.getVersion());
  }

  @PostMapping(MaterialSampleIdentifierGeneratorDto.TYPENAME)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onCreate(@RequestBody JsonApiDocument postedDocument)
      throws ResourceNotFoundException {

    MaterialSampleIdentifierGeneratorDto generatorDto = objectMapper.convertValue(postedDocument.getAttributes(), MaterialSampleIdentifierGeneratorDto.class);

    // Make sure we have sane default values
    int quantity = generatorDto.getQuantity() == null ? 1 : generatorDto.getQuantity();
    MaterialSampleNameGeneration.CharacterType characterType = generatorDto.getCharacterType() == null ?
      MaterialSampleNameGeneration.CharacterType.LOWER_LETTER : generatorDto.getCharacterType();
    MaterialSampleNameGeneration.IdentifierGenerationStrategy strategy = generatorDto.getStrategy() == null ?
      MaterialSampleNameGeneration.IdentifierGenerationStrategy.DIRECT_PARENT : generatorDto.getStrategy();

    // Sanity checks
    if (quantity > MAX_GENERATION_QTY) {
      throw new IllegalArgumentException("over maximum quantity");
    }

    if (generatorDto.getCurrentParentUUID() != null && CollectionUtils.isNotEmpty(generatorDto.getCurrentParentsUUID())) {
      throw new IllegalArgumentException("currentParentUUID or currentParentsUUID (list) can be provided but not both");
    }

    if (generatorDto.getStrategy() == MaterialSampleNameGeneration.IdentifierGenerationStrategy.TYPE_BASED &&
      generatorDto.getMaterialSampleType() == null) {
      throw new IllegalArgumentException("materialSampleType must be provided for strategy TYPE_BASED");
    }

    MaterialSampleIdentifierGeneratorDto responseDto =
      CollectionUtils.isEmpty(generatorDto.getCurrentParentsUUID()) ?
        handleSingleParent(generatorDto, characterType, strategy, quantity) :
        handleMultipleParents(generatorDto, characterType, strategy);

    JsonApiModelBuilder builder = this.jsonApiModelAssistant.createJsonApiModelBuilder(
      JsonApiDto.<MaterialSampleIdentifierGeneratorDto>builder().dto(responseDto).build());
    RepresentationModel<?> model = builder.build();
    URI uri = URI.create(MaterialSampleIdentifierGeneratorDto.TYPENAME);
    return ResponseEntity.created(uri).body(model);
  }

  private MaterialSampleIdentifierGeneratorDto handleSingleParent(MaterialSampleIdentifierGeneratorDto dto,
                                                                  MaterialSampleNameGeneration.CharacterType characterType,
                                                                  MaterialSampleNameGeneration.IdentifierGenerationStrategy strategy,
                                                                  int qty) {
    Objects.requireNonNull(dto.getCurrentParentUUID());

    List<String> nextIdentifiers = new ArrayList<>(qty);
    String lastIdentifier = identifierGenerator.generateNextIdentifier(dto.getCurrentParentUUID(),
      strategy, dto.getMaterialSampleType(), characterType, dto.getSeparator());
    nextIdentifiers.add(lastIdentifier);
    for (int i = 1; i < qty; i++) {
      lastIdentifier = identifierGenerator.generateNextIdentifier(lastIdentifier);
      nextIdentifiers.add(lastIdentifier);
    }
    dto.setId(UUID.randomUUID());
    dto.setNextIdentifiers(Map.of(dto.getCurrentParentUUID(), nextIdentifiers));
    return dto;
  }

  private MaterialSampleIdentifierGeneratorDto handleMultipleParents(MaterialSampleIdentifierGeneratorDto dto,
                                                                     MaterialSampleNameGeneration.CharacterType characterType,
                                                                     MaterialSampleNameGeneration.IdentifierGenerationStrategy strategy) {
    Objects.requireNonNull(dto.getCurrentParentsUUID());

    List<String> nextIdentifiers = new ArrayList<>(dto.getCurrentParentsUUID().size());
    Map<UUID, List<String>> responseMap = new HashMap<>();

    for (UUID parentUUID : dto.getCurrentParentsUUID()) {
      String nextIdentifier = identifierGenerator.generateNextIdentifier(parentUUID,
        strategy, dto.getMaterialSampleType(), characterType, dto.getSeparator());

      if (!nextIdentifiers.contains(nextIdentifier)) {
        nextIdentifiers.add(nextIdentifier);
      } else {
        nextIdentifier = findNextIdentifier(nextIdentifiers, nextIdentifier);
        nextIdentifiers.add(nextIdentifier);
      }
      responseMap.put(parentUUID, List.of(nextIdentifier));
    }

    dto.setId(UUID.randomUUID());
    dto.setNextIdentifiers(responseMap);
    return dto;
  }

  private String findNextIdentifier(List<String> nextIdentifiers, String currentIdentifier) {
    String nextIdentifier = identifierGenerator.generateNextIdentifier(currentIdentifier);

    if (!nextIdentifiers.contains(nextIdentifier)) {
      return nextIdentifier;
    }
    return findNextIdentifier(nextIdentifiers, nextIdentifier);
  }
}
