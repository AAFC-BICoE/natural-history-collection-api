package ca.gc.aafc.collection.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;

import ca.gc.aafc.dina.dto.ApiInfoDto;

@Configuration
public class ApiInfoConfiguration {

  @Value("${dina.messaging.isProducer:false}")
  private Boolean isProducer;

  @Value("${dina.messaging.isConsumer:false}")
  private Boolean isConsumer;

  private final String apiVersion;

  public ApiInfoConfiguration(BuildProperties buildProperties) {
    this.apiVersion = buildProperties.getVersion();
  }

  public ApiInfoDto buildApiInfoDto() {
    ApiInfoDto infoDto = new ApiInfoDto();
    infoDto.setModuleVersion(apiVersion);
    infoDto.setMessageProducer(isProducer);
    infoDto.setMessageConsumer(isConsumer);
    return infoDto;
  }
}
