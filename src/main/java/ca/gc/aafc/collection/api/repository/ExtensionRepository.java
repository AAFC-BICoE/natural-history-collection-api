package ca.gc.aafc.collection.api.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;

import ca.gc.aafc.collection.api.dto.ExtensionDto;
import ca.gc.aafc.collection.api.service.ExtensionService;
import ca.gc.aafc.dina.extension.FieldExtensionDefinition;
import ca.gc.aafc.dina.repository.ReadOnlyDinaRepositoryV2;

import static com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder.jsonApiModel;
import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.NonNull;

@RestController
@RequestMapping(value = "${dina.apiPrefix:}", produces = JSON_API_VALUE)
public class ExtensionRepository extends ReadOnlyDinaRepositoryV2<String, ExtensionDto> {

  //private final List<ExtensionDto> extension;

  protected ExtensionRepository(
    @NonNull ExtensionService extensionService) {
    super(extensionService);
   // checkArguments(extensionConfiguration.getExtension());

//    extension = extensionConfiguration.getExtension()
//      .entrySet()
//      .stream()
//      .map( entry -> new ExtensionDto(entry.getKey(), entry.getValue()))
//      .collect( Collectors.toList());
  }

//  @GetMapping(ExtensionDto.TYPENAME)
//  public ResponseEntity<RepresentationModel<?>> handleFindAll() {
//
//    FieldExtensionValueDto dto = findAll()
//    if (dto == null) {
//      return ResponseEntity.notFound().build();
//    }
//
//    JsonApiModelBuilder builder = jsonApiModel().model(RepresentationModel.of(dto));
//
//    return ResponseEntity.ok(builder.build());
//  }

  @GetMapping(ExtensionDto.TYPENAME + "/{id}")
  public ResponseEntity<RepresentationModel<?>> handleFindOne(@PathVariable String id) {

    ExtensionDto dto = findOne(id);

    if (dto == null) {
      return ResponseEntity.notFound().build();
    }

    JsonApiModelBuilder builder = jsonApiModel().model(RepresentationModel.of(dto));

    return ResponseEntity.ok(builder.build());
  }

  @GetMapping(ExtensionDto.TYPENAME)
  public ResponseEntity<RepresentationModel<?>> handleFindAll(HttpServletRequest req) {

    String queryString = StringUtils.isBlank(req.getQueryString()) ? "" :
      URLDecoder.decode(req.getQueryString(), StandardCharsets.UTF_8);

    List<ExtensionDto> dtos ;
    try {
      dtos = findAll(queryString);
    } catch (IllegalArgumentException iaEx) {
      return ResponseEntity.badRequest().build();
    }

    JsonApiModelBuilder builder = jsonApiModel().model(CollectionModel.of(dtos));

    return ResponseEntity.ok(builder.build());
  }

  /**
   * Very common configuration issue that could lead to errors.
   *
   * @param extension
   */
  private void checkArguments(Map<String, FieldExtensionDefinition.Extension> extension) {
    for (var entry : extension.entrySet()) {
      if (!entry.getKey().equals(entry.getValue().getKey())) {
        throw new IllegalStateException("Extension map key not matching extension key: "
                + entry.getKey() + " vs " + entry.getValue().getKey());
      }
    }
  }

//  @Override
//  public ResourceList<ExtensionDto> findAll(QuerySpec querySpec) {
//    return querySpec.apply(extension);
//  }
}
