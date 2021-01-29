package ca.gc.aafc.collection.api.datetime;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Rsql visitor to support rsql date filtering with partial dates. Partial dates are resolved using {@link
 * ISODateTime#parse(String)}
 */
@AllArgsConstructor
public class IsoRsqlPrecisionResolver implements RSQLVisitor<IsoRsqlPrecisionResolver.PrecisionNode, Set<String>> {

  private static final RSQLParser RSQL_PARSER = new RSQLParser();
  private final Map<String,String> fieldList;

  /**
   * Returns the given rsql string with partial dates resolved for fields tracked by the resolver. Partial
   * dates are resolved using {@link * ISODateTime#parse(String)}
   *
   * @param rsql rsql string to resolve
   * @return the given rsql string with partial dates resolved
   */
  public String resolveDates(String rsql) {
    return RSQL_PARSER.parse(rsql).accept(this, fieldList.keySet()).toString();
  }

  @Override
  public PrecisionNode visit(AndNode andNode, Set<String> field) {
    List<Node> nodes = new ArrayList<>();
    Map<String,Integer> precisionMap = new HashMap<>();
    for (Node node : andNode.getChildren()) {
      PrecisionNode accept = node.accept(this, field);
      nodes.add(accept.getNode());
      precisionMap.putAll(accept.getHighestPrecision());
    }
    return PrecisionNode.builder()
      .node(andNode.withChildren(nodes))
      .highestPrecision(precisionMap)
      .build();
  }

  @Override
  public PrecisionNode visit(OrNode orNode, Set<String> field) {
    List<Node> nodes = new ArrayList<>();
    Map<String,Integer> precisionMap = new HashMap<>();
    for (Node node : orNode.getChildren()) {
      PrecisionNode accept = node.accept(this, field);
      nodes.add(accept.getNode());
      precisionMap.putAll(accept.getHighestPrecision());
    }
    return PrecisionNode.builder()
      .node(orNode.withChildren(nodes))
      .highestPrecision(precisionMap)
      .build();
  }

  @Override
  public PrecisionNode visit(ComparisonNode node, Set<String> field) {
    if (field.stream().anyMatch(f -> node.getSelector().equalsIgnoreCase(f))) {
      List<String> mappedArguments = new ArrayList<>();
      int highestPrecision = 0;
      for (String s : node.getArguments()) {
        ISODateTime dateTime = ISODateTime.parse(s);
        highestPrecision = Math.max(highestPrecision, dateTime.getFormat().getPrecision());
        mappedArguments.add(dateTime.getLocalDateTime().toString());
      }
      return PrecisionNode.builder()
        .node(new ComparisonNode(node.getOperator(), node.getSelector(), mappedArguments))
        .highestPrecision(Map.of(fieldList.get(node.getSelector()), highestPrecision)).build();
    } else {
      return PrecisionNode.builder().node(node).highestPrecision(null).build();
    }
  }

  @Builder
  @Getter
  public static final class PrecisionNode {
    private final Node node;
    private final Map<String,Integer> highestPrecision;

    @Override
    public String toString() {
      return this.getNode().toString();
    }
  }
}
