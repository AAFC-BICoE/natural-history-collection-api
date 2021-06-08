package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectingEventTestFixture;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.IncludeRelationSpec;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectingEventRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private CollectingEventRepository collectingEventRepository;

  // @BeforeEach
  // @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
  // public void setup() {
  //   geographicPlaceNameSourceDetail = CollectingEventTestFixture.newGeographicPlaceNameSourceDetail();
  // }

  @Test
  @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
  public void findCollectingEvent_whenNoFieldsAreSelected_CollectingEventReturnedWithAllFields() {
    CollectingEventDto testCollectingEvent = collectingEventRepository.create(CollectingEventTestFixture.newEventDto(
      CollectingEventTestFixture.startDate.toString(),
      CollectingEventTestFixture.endDate.toString()));

    QuerySpec querySpec = new QuerySpec(CollectingEventDto.class);

    List<IncludeRelationSpec> includeRelationSpec = Stream.of("geoReferenceAssertions")
      .map(Arrays::asList)
      .map(IncludeRelationSpec::new)
      .collect(Collectors.toList());

    querySpec.setIncludedRelations(includeRelationSpec);

    CollectingEventDto collectingEventDto = collectingEventRepository
      .findOne(testCollectingEvent.getUuid(), querySpec);

    assertNotNull(collectingEventDto);
    assertEquals(testCollectingEvent.getUuid(), collectingEventDto.getUuid());
    assertEquals(testCollectingEvent.getCreatedBy(), collectingEventDto.getCreatedBy());
    assertEquals(
      testCollectingEvent.getStartEventDateTime(),
      collectingEventDto.getStartEventDateTime());
    assertEquals(
      testCollectingEvent.getEndEventDateTime(),
      collectingEventDto.getEndEventDateTime());
    assertEquals(CollectingEventTestFixture.XI_02_1798, collectingEventDto.getVerbatimEventDateTime());

    assertEquals(
      12.123456,
      collectingEventDto.getGeoReferenceAssertions().iterator().next().getDwcDecimalLatitude());

    assertEquals(
      CollectingEventTestFixture.testGeoreferencedDate,
      collectingEventDto.getGeoReferenceAssertions().iterator().next().getDwcGeoreferencedDate());

    assertEquals(CollectingEventTestFixture.VER_COOR, collectingEventDto.getDwcVerbatimCoordinates());
    assertEquals(CollectingEventTestFixture.dwcRecordedBy, collectingEventDto.getDwcRecordedBy());
    assertEquals(
      testCollectingEvent.getAttachment().get(0).getId(),
      collectingEventDto.getAttachment().get(0).getId());
    assertEquals(
      testCollectingEvent.getCollectors().get(0).getId(),
      collectingEventDto.getCollectors().get(0).getId());
    assertEquals(CollectingEventTestFixture.dwcVerbatimLocality, collectingEventDto.getDwcVerbatimLocality());
    assertEquals(CollectingEventTestFixture.dwcVerbatimLatitude, collectingEventDto.getDwcVerbatimLatitude());
    assertEquals(CollectingEventTestFixture.dwcVerbatimLongitude, collectingEventDto.getDwcVerbatimLongitude());
    assertEquals(CollectingEventTestFixture.dwcVerbatimCoordinateSystem, collectingEventDto.getDwcVerbatimCoordinateSystem());
    assertEquals(CollectingEventTestFixture.dwcVerbatimSRS, collectingEventDto.getDwcVerbatimSRS());
    assertEquals(CollectingEventTestFixture.dwcVerbatimElevation, collectingEventDto.getDwcVerbatimElevation());
    assertEquals(CollectingEventTestFixture.dwcVerbatimDepth, collectingEventDto.getDwcVerbatimDepth());
    assertEquals(CollectingEventTestFixture.dwcOtherRecordNumbers[1], collectingEventDto.getDwcOtherRecordNumbers()[1]);
    assertEquals(CollectingEventTestFixture.geographicPlaceNameSource, collectingEventDto.getGeographicPlaceNameSource());
    assertEquals(
      CollectingEventTestFixture.TEST_COUNTRY,
      collectingEventDto.getGeographicPlaceNameSourceDetail().getCountry());
    // assigned server-side
    assertNotNull(collectingEventDto.getGeographicPlaceNameSourceDetail().getRecordedOn());
    assertNotEquals(2000, collectingEventDto.getGeographicPlaceNameSourceDetail().getRecordedOn().getYear());
    assertNotNull(collectingEventDto.getGeographicPlaceNameSourceDetail().getSourceUrl());
    assertEquals(
      CollectingEventTestFixture.TEST_PROVINCE,
      collectingEventDto.getGeographicPlaceNameSourceDetail().getStateProvince());
    assertEquals(CollectingEventTestFixture.habitat, collectingEventDto.getHabitat());
  }

  @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
  @Test
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    CollectingEventDto ce = CollectingEventTestFixture.newEventDto("2007-12-03T10:15:30", "2007-12-04T11:20:20");
    CollectingEventDto result = collectingEventRepository.findOne(
      collectingEventRepository.create(ce).getUuid(),
      new QuerySpec(CollectingEventDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(ce.getAttachment().get(0).getId(), result.getAttachment().get(0).getId());
    assertEquals(ce.getCollectors().get(0).getId(), result.getCollectors().get(0).getId());
    assertEquals(CollectingEventTestFixture.dwcRecordedBy, result.getDwcRecordedBy());
    assertEquals(CollectingEventTestFixture.dwcVerbatimLocality, result.getDwcVerbatimLocality());
    assertEquals(CollectingEventTestFixture.dwcVerbatimLatitude, result.getDwcVerbatimLatitude());
    assertEquals(CollectingEventTestFixture.dwcVerbatimLongitude, result.getDwcVerbatimLongitude());
    assertEquals(CollectingEventTestFixture.dwcVerbatimCoordinateSystem, result.getDwcVerbatimCoordinateSystem());
    assertEquals(CollectingEventTestFixture.dwcVerbatimSRS, result.getDwcVerbatimSRS());
    assertEquals(CollectingEventTestFixture.dwcVerbatimElevation, result.getDwcVerbatimElevation());
    assertEquals(CollectingEventTestFixture.dwcVerbatimDepth, result.getDwcVerbatimDepth());
    assertEquals(CollectingEventTestFixture.dwcVerbatimLatitude, result.getDwcVerbatimLatitude());
    assertEquals(CollectingEventTestFixture.dwcVerbatimLongitude, result.getDwcVerbatimLongitude());
    assertEquals(CollectingEventTestFixture.dwcVerbatimCoordinateSystem, result.getDwcVerbatimCoordinateSystem());
    assertEquals(CollectingEventTestFixture.dwcVerbatimSRS, result.getDwcVerbatimSRS());
    assertEquals(CollectingEventTestFixture.dwcVerbatimElevation, result.getDwcVerbatimElevation());
    assertEquals(CollectingEventTestFixture.dwcVerbatimDepth, result.getDwcVerbatimDepth());
    assertEquals(CollectingEventTestFixture.dwcVerbatimLatitude, result.getDwcVerbatimLatitude());
    assertEquals(CollectingEventTestFixture.dwcVerbatimLongitude, result.getDwcVerbatimLongitude());
    assertEquals(CollectingEventTestFixture.dwcVerbatimCoordinateSystem, result.getDwcVerbatimCoordinateSystem());
    assertEquals(CollectingEventTestFixture.dwcVerbatimSRS, result.getDwcVerbatimSRS());
    assertEquals(CollectingEventTestFixture.dwcVerbatimElevation, result.getDwcVerbatimElevation());
    assertEquals(CollectingEventTestFixture.dwcVerbatimDepth, result.getDwcVerbatimDepth());
    assertEquals(CollectingEventTestFixture.dwcOtherRecordNumbers[1], result.getDwcOtherRecordNumbers()[1]);
    assertEquals(CollectingEventTestFixture.habitat, result.getHabitat());
    assertAssertion(result.getGeoReferenceAssertions().get(0), ce.getGeoReferenceAssertions().get(0));
  }

  private void assertAssertion(
    GeoreferenceAssertionDto resultAssertion,
    GeoreferenceAssertionDto expectedAssertion
  ) {
    if (expectedAssertion == null) {
      assertNull(resultAssertion);
      return;
    }
    assertNotNull(resultAssertion);
    assertEquals(expectedAssertion.getDwcDecimalLongitude(), resultAssertion.getDwcDecimalLongitude());
    assertEquals(expectedAssertion.getDwcDecimalLatitude(), resultAssertion.getDwcDecimalLatitude());
    assertEquals(expectedAssertion.getIsPrimary(), resultAssertion.getIsPrimary());
  }

  @ParameterizedTest
  @MethodSource({"equalFilterSource", "lt_FilterSource", "gt_FilterSource"})
  @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
  void findAll_PrecisionBoundsTest_DateFilteredCorrectly(String startDate, String input, int expectedSize) {
    collectingEventRepository.create(CollectingEventTestFixture.newEventDto(startDate, "2020"));
    assertEquals(expectedSize, collectingEventRepository.findAll(newRsqlQuerySpec(input)).size());
  }

  private static Stream<Arguments> equalFilterSource() {
    return Stream.of(
      // Format YYYY
      Arguments.of("1999", "startEventDateTime==1999", 1),
      Arguments.of("1999", "startEventDateTime==1998", 0),
      // Format YYYY-MM
      Arguments.of("1999-03", "startEventDateTime==1999-03", 1),
      Arguments.of("1999-03", "startEventDateTime==1999-02", 0),
      // Format YYYY-MM-DD
      Arguments.of("1999-03-03", "startEventDateTime==1999-03-03", 1),
      Arguments.of("1999-03-03", "startEventDateTime==1999-03-02", 0),
      // Format YYYY-MM-DD-HH-MM
      Arguments.of("1999-03-03T03:00", "startEventDateTime==1999-03-03T03:00", 1),
      Arguments.of("1999-03-03T03:00", "startEventDateTime==1999-03-03T02:00", 0),
      // Format YYYY-MM-DD-HH-MM-SS
      Arguments.of("1999-03-03T03:00:03", "startEventDateTime==1999-03-03T03:00:03", 1),
      Arguments.of("1999-03-03T03:00:03", "startEventDateTime==1999-03-03T03:00:02", 0)
    );
  }

  private static Stream<Arguments> lt_FilterSource() {
    return Stream.of(
      // Format YYYY
      Arguments.of("1999", "startEventDateTime=le=1999", 1),
      Arguments.of("1999", "startEventDateTime=le=1998", 0),

      Arguments.of("1999", "startEventDateTime=lt=1999", 0),
      Arguments.of("1999", "startEventDateTime=lt=2000", 1),

      // Format YYYY-MM
      Arguments.of("1999", "startEventDateTime=le=1999-01", 0),
      Arguments.of("1999-01", "startEventDateTime=le=1999-01", 1),
      Arguments.of("1999-01", "startEventDateTime=le=1998-12", 0),

      Arguments.of("1999-01", "startEventDateTime=lt=1999-01", 0),
      Arguments.of("1999-01", "startEventDateTime=lt=1999-02", 1),

      // Format YYYY-MM-DD
      Arguments.of("1999-01", "startEventDateTime=le=1999-01-01", 0),
      Arguments.of("1999-01-02", "startEventDateTime=le=1999-01-02", 1),
      Arguments.of("1999-01-02", "startEventDateTime=le=1999-01-01", 0),

      Arguments.of("1999-01-02", "startEventDateTime=lt=1999-01-02", 0),
      Arguments.of("1999-01-02", "startEventDateTime=lt=1999-01-03", 1),
      // Format YYYY-MM-DD-HH-MM
      Arguments.of("1999-01-02", "startEventDateTime=le=1999-01-02T02:00", 0),
      Arguments.of("1999-01-02T01:00", "startEventDateTime=le=1999-01-02T02:00", 1),
      Arguments.of("1999-01-02T02:00", "startEventDateTime=le=1999-01-02T01:00", 0),

      Arguments.of("1999-01-02T02:00", "startEventDateTime=lt=1999-01-02T02:00", 0),
      Arguments.of("1999-01-02T02:00", "startEventDateTime=lt=1999-01-02T03:00", 1)
    );
  }

  private static Stream<Arguments> gt_FilterSource() {
    return Stream.of(
      // Format YYYY
      Arguments.of("2010", "startEventDateTime=ge=2010", 1),
      Arguments.of("2010", "startEventDateTime=ge=2011", 0),

      Arguments.of("2010", "startEventDateTime=gt=2010", 0),
      Arguments.of("2010", "startEventDateTime=gt=2009", 1),

      // Format YYYY-MM
      Arguments.of("2010", "startEventDateTime=ge=2010-01", 0),
      Arguments.of("2010-01", "startEventDateTime=ge=2010-01", 1),
      Arguments.of("2010-01", "startEventDateTime=ge=2010-02", 0),

      Arguments.of("2010-01", "startEventDateTime=gt=2010-01", 0),
      Arguments.of("2010-01", "startEventDateTime=gt=2009-12", 1),

      // Format YYYY-MM-DD
      Arguments.of("2010-01", "startEventDateTime=ge=2010-01-01", 0),
      Arguments.of("2010-01-02", "startEventDateTime=ge=2010-01-02", 1),
      Arguments.of("2010-01-02", "startEventDateTime=ge=2010-01-03", 0),

      Arguments.of("2010-01-02", "startEventDateTime=gt=2010-01-02", 0),
      Arguments.of("2010-01-02", "startEventDateTime=gt=2010-01-01", 1),
      // Format YYYY-MM-DD-HH-MM
      Arguments.of("2010-01-02", "startEventDateTime=ge=2010-01-02T02:00", 0),
      Arguments.of("2010-01-02T01:00", "startEventDateTime=ge=2010-01-02T02:00", 0),
      Arguments.of("2010-01-02T02:00", "startEventDateTime=ge=2010-01-02T01:00", 1),

      Arguments.of("2010-01-02T02:00", "startEventDateTime=gt=2010-01-02T02:00", 0),
      Arguments.of("2010-01-02T02:00", "startEventDateTime=gt=2010-01-02T01:00", 1)
    );
  }

  private static QuerySpec newRsqlQuerySpec(String rsql) {
    QuerySpec spec = new QuerySpec(CollectingEventDto.class);
    spec.addFilter(PathSpec.of("rsql").filter(FilterOperator.EQ, rsql));
    return spec;
  }

}
