package ca.gc.aafc.collection.api.datetime;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
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
  private final Map<String, String> fieldToPrecisionNameMap;

  /**
   * Returns the given rsql string with partial dates resolved for fields tracked by the resolver. Partial
   * dates are resolved using {@link * ISODateTime#parse(String)}
   *
   * @param rsql rsql string to resolve
   * @return the given rsql string with partial dates resolved
   */
  public String resolveDates(String rsql) {
    return RSQL_PARSER.parse(rsql).accept(this, fieldToPrecisionNameMap.keySet()).toString();
  }

  @Override
  public PrecisionNode visit(AndNode andNode, Set<String> field) {
    return resolveLogicalNode(andNode, field);
  }

  @Override
  public PrecisionNode visit(OrNode orNode, Set<String> field) {
    return resolveLogicalNode(orNode, field);
  }

  private PrecisionNode resolveLogicalNode(LogicalNode logicalNode, Set<String> field) {
    List<Node> nodes = new ArrayList<>();
    List<ComparisonNode> precisionMap = new ArrayList<>();
    for (Node node : logicalNode.getChildren()) {
      PrecisionNode accept = node.accept(this, field);
      nodes.add(accept.getNode());
      if (CollectionUtils.isNotEmpty(accept.getPrecisionComparisons())) {
        precisionMap.addAll(accept.getPrecisionComparisons());
      }
    }
    return PrecisionNode.builder()
      .node(logicalNode.withChildren(nodes))
      .precisionComparisons(precisionMap)
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
        .precisionComparisons(newPrecisionComparison(highestPrecision, node.getSelector()))
        .build();
    } else {
      return PrecisionNode.builder().node(node).precisionComparisons(null).build();
    }
  }

  private List<ComparisonNode> newPrecisionComparison(int precision, String selector) {
    return List.of(new ComparisonNode(
      new ComparisonOperator(RSQLOperators.GREATER_THAN_OR_EQUAL.getSymbol()),
      fieldToPrecisionNameMap.get(selector),
      List.of(Integer.toString(precision))));
  }

  @Builder
  @Getter
  public static final class PrecisionNode {
    private final Node node;
    private final List<ComparisonNode> precisionComparisons;

    @Override
    public String toString() {
      if (CollectionUtils.isNotEmpty(precisionComparisons)) {
        List<Node> nodes = new ArrayList<>();
        nodes.add(node);
        nodes.addAll(precisionComparisons);
        return new AndNode(nodes).toString();
      } else {
        return this.getNode().toString();
      }
    }
  }
}
