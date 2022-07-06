package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.FormTemplateDto;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Map;

public final class FormTemplateFixture {

  public static final String GROUP = "aafc";

  private FormTemplateFixture() {
    //utility class
  }

  public static FormTemplateDto.FormTemplateDtoBuilder newFormTemplate() {
    return FormTemplateDto.builder()
        .group(GROUP)
        .restrictToCreatedBy(false)
        .viewConfiguration(Map.of())
        .name(RandomStringUtils.randomAlphabetic(12));
  }
}
