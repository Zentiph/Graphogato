package graphogato.errors;

/**
 * An error that occurs due to memory overflow.
 */
public class OverflowError extends Error {
   /**
    * Create a new OverflowError with an error code.
    *
    * @param code - Error code
    */
   public OverflowError(ErrorCode code) {
      super("OVERFLOW ERROR: " + Errors.enumToString(code) + " (err 2, code " + code.ordinal() + ").");
   }

   public enum ErrorCode {
      ARITHMETIC_OVERFLOW,
      INTEGER_OVERFLOW,
      FLOATING_POINT_OVERFLOW,
      /** Reserved value for an unimplemented error enum value */
      UNKNOWN_OVERFLOW_ERROR;
   }
}
