package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CollectingEventValidationTest extends CollectionModuleBaseIT {
  @Inject
  private ApplicationContext context;

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = context.getBean(LocalValidatorFactoryBean.class).getValidator();
  }

  @Test
  void validate_whenDatesInTheFuture_ConstraintViolated() {
    CollectingEvent event = newEvent(2050, 2099);
    Set<ConstraintViolation<CollectingEvent>> validate = validator.validate(event);
    MatcherAssert.assertThat(
      validate.stream().map(c -> c.getPropertyPath().toString()).collect(Collectors.toSet()),
      Matchers.containsInAnyOrder("startEventDateTime", "endEventDateTime"));
    MatcherAssert.assertThat(
      validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()),
      Matchers.containsInAnyOrder("must be a past date", "must be a past date"));
  }

  @Test
  void validate_whenEndDateIsBeforeStartDate_ConstraintViolated() {
    CollectingEvent event = newEvent(2000, 1900);
    Set<ConstraintViolation<CollectingEvent>> validate = validator.validate(event);
    MatcherAssert.assertThat(
      validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()),
      Matchers.contains("The start and end dates do not create a valid time line."));
  }

  @Test
  void validate_whenEndDateWithoutStartDate_ConstraintViolated() {
    CollectingEvent event = CollectingEvent.builder()
      .uuid(UUID.randomUUID())
      .createdBy("jon")
      .startEventDateTime(null)
      .endEventDateTime(LocalDateTime.of(1999, 1, 1, 1, 1))
      .build();
    Set<ConstraintViolation<CollectingEvent>> validate = validator.validate(event);
    MatcherAssert.assertThat(
      validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()),
      Matchers.contains("The start and end dates do not create a valid time line."));
  }

  private CollectingEvent newEvent(int startYear, int endYear) {
    return CollectingEvent.builder()
      .uuid(UUID.randomUUID())
      .createdBy("jon")
      .startEventDateTime(LocalDateTime.of(startYear, 1, 1, 1, 1))
      .endEventDateTime(LocalDateTime.of(endYear, 1, 1, 1, 1))
      .build();
  }
}
