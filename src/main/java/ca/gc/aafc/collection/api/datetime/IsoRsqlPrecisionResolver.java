package ca.gc.aafc.collection.api.datetime;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Rsql visitor to support rsql date filtering with partial dates. Partial dates are resolved using {@link
 * ISODateTime#parse(String)}
 */
@AllArgsConstructor
public class IsoRsqlPrecisionResolver implements RSQLVisitor<Node, List<String>> {

  private static final RSQLParser RSQL_PARSER = new RSQLParser();
  private final List<String> fieldList;

  /**
   * Returns the given rsql string with partial dates resolved for fields tracked by the resolver. Partial
   * dates are resolved using {@link * ISODateTime#parse(String)}
   *
   * @param rsql rsql string to resolve
   * @return the given rsql string with partial dates resolved
   */
  public String resolveDates(String rsql) {
    return RSQL_PARSER.parse(rsql).accept(this, fieldList).toString();
  }

  @Override
  public Node visit(AndNode andNode, List<String> field) {
    return andNode.withChildren(andNode.getChildren()
      .stream()
      .map(node -> node.accept(this, field))
      .collect(Collectors.toList()));
  }

  @Override
  public Node visit(OrNode orNode, List<String> field) {
    return orNode.withChildren(orNode.getChildren()
      .stream()
      .map(node -> node.accept(this, field))
      .collect(Collectors.toList()));
  }

  @Override
  public Node visit(ComparisonNode comparisonNode, List<String> field) {
    if (field.stream().anyMatch(f -> comparisonNode.getSelector().equalsIgnoreCase(f))) {
      List<String> mappedArguments = comparisonNode.getArguments()
        .stream()
        .map(s -> ISODateTime.parse(s).getLocalDateTime().toString())
        .collect(Collectors.toList());
      return new ComparisonNode(comparisonNode.getOperator(), comparisonNode.getSelector(), mappedArguments);
    } else {
      return comparisonNode;
    }
  }
}
