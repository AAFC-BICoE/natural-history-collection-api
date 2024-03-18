package ca.gc.aafc.collection.api.repository;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;

import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.dto.ResourceNameIdentifierResponseDto;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.dina.repository.ResourceNameIdentifierBaseRepository;
import ca.gc.aafc.dina.service.ResourceNameIdentifierService;

import static com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder.jsonApiModel;
import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

/**
 * Endpoint used to get identifiers (uuid) based on name.
 */
@RestController
@RequestMapping(value = "/api/v1", produces = JSON_API_VALUE)
public class ResourceNameIdentifierRepository extends ResourceNameIdentifierBaseRepository {

  public ResourceNameIdentifierRepository(
    ResourceNameIdentifierService resourceNameIdentifierService) {
    super(resourceNameIdentifierService, Map.of(CollectionDto.TYPENAME, Collection.class));
  }

  @GetMapping(ResourceNameIdentifierResponseDto.TYPE)
  public ResponseEntity<RepresentationModel<?>> findOne(HttpServletRequest req) {

    String query = URLDecoder.decode(req.getQueryString(), StandardCharsets.UTF_8);
    ResourceNameIdentifierResponseDto dto;
    try {
      Pair<String, UUID> identifier = findOne(query);
      dto = ResourceNameIdentifierResponseDto.builder()
        .id(identifier.getValue())
        .name(identifier.getKey())
        .build();
    } catch (IllegalArgumentException iaEx) {
      return ResponseEntity.badRequest().build();
    }

    JsonApiModelBuilder builder = jsonApiModel().model(dto);

    return ResponseEntity.ok(builder.build());
  }

}
