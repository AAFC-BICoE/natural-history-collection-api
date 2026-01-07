package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.dina.repository.ReadOnlyDinaRepositoryV2;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;

import ca.gc.aafc.collection.api.dto.FieldExtensionValueDto;
import ca.gc.aafc.collection.api.service.FieldExtensionValueService;
import lombok.NonNull;

import static com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder.jsonApiModel;
import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

@RestController
@RequestMapping(value = "${dina.apiPrefix:}", produces = JSON_API_VALUE)
public class FieldExtensionValueRepository extends ReadOnlyDinaRepositoryV2<String, FieldExtensionValueDto> {

  protected FieldExtensionValueRepository(
      @NonNull FieldExtensionValueService service) {
    super(service);
  }

  @GetMapping(FieldExtensionValueDto.TYPENAME + "/{id}")
  public ResponseEntity<RepresentationModel<?>> handleFindOne(@PathVariable String id) {

    FieldExtensionValueDto dto = findOne(id);
    if (dto == null) {
      return ResponseEntity.notFound().build();
    }

    JsonApiModelBuilder builder = jsonApiModel().model(RepresentationModel.of(dto));

    return ResponseEntity.ok(builder.build());
  }
}
