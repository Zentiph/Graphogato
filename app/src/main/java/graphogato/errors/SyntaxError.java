package graphogato.errors;

/**
 * An error that occurs due to invalid syntax.
 */
public class SyntaxError extends Error {
   /**
    * Create a new SyntaxError with an error code.
    *
    * @param code - Error code
    */
   public SyntaxError(ErrorCode code) {
      super("SYNTAX ERROR: " + Errors.enumToString(code) + " (err 1, code " + code.ordinal() + ").");
   }

   public enum ErrorCode {
      INVALID_CHARACTER,
      MISSING_OPERATOR,
      MISSING_VALUE,
      UNMATCHED_PARENTHESIS,
      /** Reserved value for an unimplemented error enum value */
      UNKNOWN_SYNTAX_ERROR;
   }
}
