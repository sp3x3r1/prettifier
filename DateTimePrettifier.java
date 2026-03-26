import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimePrettifier {
    
    // matches: YYYY-MM-DDTHH:mm(Z|[+-]\d{2}:\d{2})
    private static final Pattern ISO_PATTERN = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2})(Z|[+\\-\\u2212]\\d{2}:\\d{2})$");

    public static String formatIsoDate(String type, String isoString) {
        Matcher m = ISO_PATTERN.matcher(isoString);
        if (!m.matches()) {
            return null; // Malformed
        }
        
        String yyyy = m.group(1);
        String mm = m.group(2);
        String dd = m.group(3);
        String hhStr = m.group(4);
        String minStr = m.group(5);
        String offset = m.group(6);
        
        // normalize offset
        if (offset.equals("Z")) {
            offset = "(+00:00)";
        } else {
            // replace unicode minus with ascii hyphen
            offset = offset.replace('\u2212', '-');
            // e.g. "-02:00" -> "(-02:00)"
            offset = "(" + offset + ")";
        }
        
        if (type.equals("D")) {
            return dd + " " + getMonth(mm) + " " + yyyy;
        } else if (type.equals("T12")) {
            int hh = Integer.parseInt(hhStr);
            String period = (hh >= 12) ? "PM" : "AM";
            int hh12 = hh % 12;
            if (hh12 == 0) hh12 = 12; // 00:00 -> 12:00AM, 12:00 -> 12:00PM
            String hh12Str = (hh12 < 10 ? "0" : "") + hh12;
            return hh12Str + ":" + minStr + period + " " + offset;
        } else if (type.equals("T24")) {
            return hhStr + ":" + minStr + " " + offset;
        }
        
        return null; // Should not reach here
    }

    private static String getMonth(String mm) {
        switch (mm) {
            case "01": return "Jan";
            case "02": return "Feb";
            case "03": return "Mar";
            case "04": return "Apr";
            case "05": return "May";
            case "06": return "Jun";
            case "07": return "Jul";
            case "08": return "Aug";
            case "09": return "Sep";
            case "10": return "Oct";
            case "11": return "Nov";
            case "12": return "Dec";
            default: return mm;
        }
    }
}
