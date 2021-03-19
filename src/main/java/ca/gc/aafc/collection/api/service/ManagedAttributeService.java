package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.ManagedAttribute;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ManagedAttributeService extends DefaultDinaService<ManagedAttribute> {

  private static final Pattern NON_ALPHANUMERICAL = Pattern.compile("[^a-z0-9]");

  public ManagedAttributeService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected void preCreate(ManagedAttribute entity) {
    entity.setUuid(UUID.randomUUID());

    if (StringUtils.isNotBlank(entity.getName())) {
      entity.setKey(generateKeyFromName(entity.getName()));
    }

  }

  @SneakyThrows
  @Override
  public void delete(ManagedAttribute entity) {
    throw new HttpRequestMethodNotSupportedException("DELETE");
  }

  /**
   * Transforms a name into a key. camelCase is supported.
   * "Aa bb !! mySuperAttribute # 11" will become aa_bb_my_super_attribute_11
   * @param name
   * @return
   */
  private static String generateKeyFromName(String name) {
    Objects.requireNonNull(name);

    return Arrays.stream(StringUtils.
        splitByCharacterTypeCamelCase(StringUtils.normalizeSpace(name)))
        .filter(StringUtils::isNotBlank)
        .map(ManagedAttributeService::processName)
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.joining("_"));
  }

  private static String processName(String name) {
    return RegExUtils.removeAll(name.toLowerCase(), NON_ALPHANUMERICAL);
  }

}
