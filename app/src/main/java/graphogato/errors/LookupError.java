package graphogato.errors;

/**
 * An error that occurs due to an incorrect value lookup.
 */
public class LookupError extends Error {
   /**
    * Create a new LookupError with an error code.
    *
    * @param code - Error code
    */
   public LookupError(ErrorCode code) {
      super("LOOKUP ERROR: " + Errors.enumToString(code) + " (err 4, code " + code.ordinal() + ").");
   }

   public enum ErrorCode {
      ITEM_NOT_FOUND,
      /** Reserved value for an unimplemented error enum value */
      UNKNOWN_LOOKUP_ERROR;
   }
}
