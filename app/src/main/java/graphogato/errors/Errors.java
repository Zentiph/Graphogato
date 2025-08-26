package graphogato.errors;

import java.util.Locale;

/**
 * Utility class for all errors.
 *
 * @author Gavin Borne
 */
public class Errors {
   private Errors() {
   }

   /**
    * Convert an enum value to an error code String.
    *
    * @param e - The enum value
    * @return Error code String version of the enum
    */
   public static String enumToString(Enum<?> e) {
      return e.name().replace("_", "-").toLowerCase(Locale.ROOT);
   }
}
