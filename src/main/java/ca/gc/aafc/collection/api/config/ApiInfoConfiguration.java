package ca.gc.aafc.collection.api.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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

  // variables where the content could be changed at runtime
  private final AtomicBoolean attentionRequired;
  private final ConcurrentHashMap<String, List<String>> moduleInfo = new ConcurrentHashMap<>();

  public ApiInfoConfiguration(BuildProperties buildProperties) {
    this.apiVersion = buildProperties.getVersion();
    this.attentionRequired = new AtomicBoolean(false);
  }

  public void setAttentionRequired(boolean newValue) {
    attentionRequired.set(newValue);
  }

  public void addModuleInfo(String key, String value) {
    moduleInfo.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
  }

  public ApiInfoDto buildApiInfoDto() {
    ApiInfoDto infoDto = new ApiInfoDto();
    infoDto.setModuleVersion(apiVersion);
    infoDto.setAttentionRequired(attentionRequired.get());

    if (!moduleInfo.isEmpty()) {
      infoDto.setModuleInfo(new HashMap<>(moduleInfo));
    }

    infoDto.setMessageProducer(isProducer);
    infoDto.setMessageConsumer(isConsumer);
    return infoDto;
  }
}
