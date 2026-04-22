package ca.gc.aafc.collection.api.repository;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.gc.aafc.collection.api.config.ApiInfoConfiguration;
import ca.gc.aafc.collection.api.security.DinaAdminAuthorizationService;
import ca.gc.aafc.dina.dto.ApiInfoDto;

import static com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder.jsonApiModel;
import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

@RestController
@RequestMapping(value = "${dina.apiPrefix:}", produces = JSON_API_VALUE)
public class ApiInfoRepository {

  private final DinaAdminAuthorizationService dinaAdminAuthorizationService;
  private final ApiInfoConfiguration apiInfoConfiguration;

  public ApiInfoRepository(DinaAdminAuthorizationService dinaAdminAuthorizationService,
                           ApiInfoConfiguration apiInfoConfiguration) {
    this.dinaAdminAuthorizationService = dinaAdminAuthorizationService;
    this.apiInfoConfiguration = apiInfoConfiguration;
  }

  @GetMapping(ApiInfoDto.TYPE_NAME)
  public ResponseEntity<RepresentationModel<?>> onApiInfo() {
    // admin only
    dinaAdminAuthorizationService.authorize();

    ApiInfoDto infoDto = apiInfoConfiguration.buildApiInfoDto();
    return ResponseEntity.ok(jsonApiModel().model(RepresentationModel.of(infoDto)).build());
  }
}
