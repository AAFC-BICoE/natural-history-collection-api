package ca.gc.aafc.collection.api.testsupport.factories;

import java.net.URL;
import java.time.LocalDate;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import lombok.SneakyThrows;

public class DeterminationFactory implements TestableEntityFactory<Determination> {
  
  @Override
    public Determination getEntityInstance() {
      return newDetermination().build();
    }

    /**
     * Static method that can be called to return a configured builder that can be
     * further customized to return the actual entity object, call the .build()
     * method on a builder.
     *
     * @return Pre-configured builder with all mandatory fields set
     */

  @SneakyThrows
  public static Determination.DeterminationBuilder newDetermination() {
    return Determination.builder()
      .verbatimDeterminer(RandomStringUtils.randomAlphabetic(3))
      .verbatimDate(LocalDate.now().toString())
      .isPrimary(false)
      .verbatimScientificName(RandomStringUtils.randomAlphabetic(3))
      .scientificNameDetails(Determination.ScientificNameSourceDetails.builder()
        .sourceUrl(new URL("https://www.google.com").toString())
        .recordedOn(LocalDate.now().minusDays(1))
        .build())
      .transcriberRemarks(RandomStringUtils.randomAlphabetic(50))
      .verbatimRemarks(RandomStringUtils.randomAlphabetic(50))
      .determinationRemarks(RandomStringUtils.randomAlphabetic(50));
  }
  
}
