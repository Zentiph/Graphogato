package graphogato.errors;

/**
 * An error that occurs due to a division by zero.
 */
public class ZeroDivisionError extends Error {
   /**
    * Create a new ZeroDivisionError with an error code.
    *
    * @param code - Error code
    */
   public ZeroDivisionError(ErrorCode code) {
      super("ZERO DIVISION ERROR: " + Errors.enumToString(code) + " (err 3, code " + code.ordinal() + ").");
   }

   public enum ErrorCode {
      DIVISION_BY_ZERO,
      /** Reserved value for an unimplemented error enum value */
      UNKNOWN_ZERO_DIVISION_ERROR;
   }
}
