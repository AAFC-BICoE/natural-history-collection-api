package ca.gc.aafc.collection.api.config;

import java.util.concurrent.atomic.AtomicBoolean;

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

  private final AtomicBoolean attentionRequired;

  public ApiInfoConfiguration(BuildProperties buildProperties) {
    this.apiVersion = buildProperties.getVersion();
    this.attentionRequired = new AtomicBoolean(false);
  }

  public void setAttentionRequired(boolean attentionRequired) {
    this.attentionRequired.set(attentionRequired);
  }

  public ApiInfoDto buildApiInfoDto() {
    ApiInfoDto infoDto = new ApiInfoDto();
    infoDto.setModuleVersion(apiVersion);
    infoDto.setAttentionRequired(attentionRequired.get());

    infoDto.setMessageProducer(isProducer);
    infoDto.setMessageConsumer(isConsumer);
    return infoDto;
  }
}
