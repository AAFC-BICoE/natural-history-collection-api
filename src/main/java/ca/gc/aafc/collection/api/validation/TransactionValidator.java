package ca.gc.aafc.collection.api.validation;

import java.util.HashSet;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.entities.AgentRolesWithDate;
import ca.gc.aafc.collection.api.entities.Transaction;

import lombok.NonNull;

@Component
public class TransactionValidator implements Validator {

  private final MessageSource messageSource;

  static final String DUPLICATED_UUID_FOUND_KEY = "transaction.duplicateRoleUUID";

  public TransactionValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return Transaction.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("TransactionValidator not supported for class " + target.getClass());
    }
    Transaction transaction = (Transaction) target;
    validateUniqueAgentUUID(transaction, errors);
  }

  /**
   * If AgentRoles are provided, go through to check for any duplicate UUID values.
   * 
   * Please note that a AgentRoles has a array of strings to use for the roles. If a user wants to
   * attach two roles, it would be using that array. Not by creating two AgentRoles with the same
   * UUID.
   * 
   * @param transaction the transaction entity to check the agentRoles against.
   * @param errors Error list to add errors to if validation violation is found.
   */
  private void validateUniqueAgentUUID(Transaction transaction, Errors errors) {
    // Check if transaction has any agent roles.
    if (CollectionUtils.isNotEmpty(transaction.getAgentRoles())) {

      // Check if any agent UUIDs are duplicated. 
      // allMatch will return false if a UUID can't be put into a HashSet (Which means a duplicate was found.)
      if (!transaction.getAgentRoles().stream()
          .map(AgentRolesWithDate::getAgent)
              .allMatch(new HashSet<>()::add)) {
        // Duplicate was found, report the error.
        addError(errors, DUPLICATED_UUID_FOUND_KEY);
      }
    }
  }

  /**
   * Internal method to add an error to the provided Errors object with a message from the
   * message bundle.
   * @param errors
   * @param messageBundleKey
   */
  private void addError(Errors errors, String messageBundleKey) {
    String errorMessage = messageSource.getMessage(
        messageBundleKey,
        null,
        LocaleContextHolder.getLocale());
    errors.reject(messageBundleKey, errorMessage);
  }
}
