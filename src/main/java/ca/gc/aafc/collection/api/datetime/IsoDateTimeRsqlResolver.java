package ca.gc.aafc.collection.api.datetime;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Used to resolve Iso Date time partial dates.
 */
@AllArgsConstructor
public class IsoDateTimeRsqlResolver implements RSQLVisitor<Node, Set<String>> {

  private static final RSQLParser RSQL_PARSER = new RSQLParser();
  private final Map<String, String> fieldList;

  /**
   * Resolves the given rsql string for dates tracked by the resolver.
   *
   * @param rsql - string to resolve
   * @return - Resolved rsql
   */
  public String resolveDates(String rsql) {
    return RSQL_PARSER.parse(rsql).accept(this, fieldList.keySet()).toString();
  }

  @Override
  public Node visit(AndNode andNode, Set<String> field) {
    return resolveLogicalNode(andNode, field);
  }

  @Override
  public Node visit(OrNode orNode, Set<String> field) {
    return resolveLogicalNode(orNode, field);
  }

  private Node resolveLogicalNode(LogicalNode logicalNode, Set<String> field) {
    return logicalNode.withChildren(logicalNode.getChildren()
      .stream()
      .map(node -> node.accept(this, field))
      .collect(Collectors.toList()));
  }

  @Override
  public Node visit(ComparisonNode node, Set<String> field) {
    String selector = node.getSelector();
    if (isSelectedField(field, selector) && node.getOperator().equals(RSQLOperators.EQUAL)) {
      ISODateTime argument = node.getArguments().stream().findFirst().map(ISODateTime::parse).orElseThrow();
      List<String> precision = List.of(Byte.toString(argument.getFormat().getPrecision()));
      return new AndNode(List.of(
        new ComparisonNode(RSQLOperators.EQUAL, selector, List.of(argument.getLocalDateTime().toString())),
        new ComparisonNode(RSQLOperators.EQUAL, fieldList.get(selector), precision)
      ));
    } else {
      return node;
    }
  }

  private static boolean isSelectedField(Set<String> field, String selector) {
    return field.stream().anyMatch(selector::equalsIgnoreCase);
  }

}
