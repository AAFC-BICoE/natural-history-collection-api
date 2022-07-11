package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.FormTemplateDto;
import ca.gc.aafc.collection.api.entities.FormTemplate;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
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
            .name(RandomStringUtils.randomAlphabetic(12))
            .components(List.of(
                    newFormComponent()
                            .sections(List.of(newFormSection().items(List.of(newSectionItem().build())).build())).build()
            ));
  }

  public static FormTemplate.FormComponent.FormComponentBuilder newFormComponent() {
    return FormTemplate.FormComponent.builder()
            .name("component 1");
  }

  public static FormTemplate.FormSection.FormSectionBuilder newFormSection () {
    return FormTemplate.FormSection.builder()
            .name("section 1");
  }

  public static FormTemplate.SectionItem.SectionItemBuilder newSectionItem() {
    return FormTemplate.SectionItem
            .builder()
            .name("Item 1");
  }

}
