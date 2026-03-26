import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItineraryProcessor {
    
    // Patterns for matching
    private static final Pattern CITY_ICAO_PATTERN = Pattern.compile("\\*\\#\\#([A-Z0-9]{4})");
    private static final Pattern CITY_IATA_PATTERN = Pattern.compile("\\*\\#([A-Z0-9]{3})");
    private static final Pattern AIRP_ICAO_PATTERN = Pattern.compile("\\#\\#([A-Z0-9]{4})");
    private static final Pattern AIRP_IATA_PATTERN = Pattern.compile("\\#([A-Z0-9]{3})");
    
    private static final Pattern DATE_PATTERN = Pattern.compile("([DT](?:12|24)?)\\(([^)]+)\\)");

    public static String process(String input, AirportLookup lookup, boolean ansi) {
        // Whitespace conversion (\v -> \x0B, \f, \r) -> \n
        String processed = input.replaceAll("[\\x0B\\f\\r]", "\n");

        // City Replacements
        processed = replaceCodes(processed, CITY_ICAO_PATTERN, lookup, true, true, ansi);
        processed = replaceCodes(processed, CITY_IATA_PATTERN, lookup, false, true, ansi);

        // Airport Replacements
        processed = replaceCodes(processed, AIRP_ICAO_PATTERN, lookup, true, false, ansi);
        processed = replaceCodes(processed, AIRP_IATA_PATTERN, lookup, false, false, ansi);

        // Date Replacements
        processed = replaceDates(processed, ansi);

        // Trim excess newlines
        processed = processed.replaceAll("\\n{3,}", "\n\n");

        return processed;
    }

    private static String replaceCodes(String text, Pattern pattern, AirportLookup lookup, boolean isIcao, boolean isCity, boolean ansi) {
        Matcher m = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String code = m.group(1);
            String name = isCity ? lookup.getCity(code, isIcao) : lookup.getName(code, isIcao);
            
            if (name != null) {
                if (ansi) {
                    name = "\u001B[1;36m" + name + "\u001B[0m"; // Cyan
                }
                m.appendReplacement(sb, Matcher.quoteReplacement(name));
            } else {
                m.appendReplacement(sb, Matcher.quoteReplacement(m.group(0)));
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String replaceDates(String text, boolean ansi) {
        Matcher m = DATE_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String type = m.group(1);
            String isoString = m.group(2);
            
            if (type.equals("D") || type.equals("T12") || type.equals("T24")) {
                String formatted = DateTimePrettifier.formatIsoDate(type, isoString);
                if (formatted != null) {
                    if (ansi) {
                        formatted = "\u001B[1;35m" + formatted + "\u001B[0m"; // Magenta
                    }
                    m.appendReplacement(sb, Matcher.quoteReplacement(formatted));
                } else {
                    m.appendReplacement(sb, Matcher.quoteReplacement(m.group(0)));
                }
            } else {
                m.appendReplacement(sb, Matcher.quoteReplacement(m.group(0)));
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
