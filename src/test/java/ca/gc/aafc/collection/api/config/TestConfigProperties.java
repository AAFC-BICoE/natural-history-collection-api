package ca.gc.aafc.collection.api.config;

import java.util.Properties;

import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfigProperties {

  @Bean
  public BuildProperties buildProperties() {
    Properties props = new Properties();
    props.setProperty("version", "collection-module-version");
    return new BuildProperties(props);
  }

}
