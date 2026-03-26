import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AirportLookup {
    private final Map<String, String> iataToName = new HashMap<>();
    private final Map<String, String> icaoToName = new HashMap<>();
    private final Map<String, String> iataToCity = new HashMap<>();
    private final Map<String, String> icaoToCity = new HashMap<>();

    public AirportLookup(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("Airport lookup not found");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                throw new IllegalStateException("Airport lookup malformed");
            }

            // Split by comma outside quotes
            String[] headers = splitCSV(headerLine);
            
            int nameIdx = -1, muniIdx = -1, icaoIdx = -1, iataIdx = -1;
            boolean[] requiredColumns = new boolean[6]; 
            
            for (int i = 0; i < headers.length; i++) {
                String h = headers[i].trim();
                switch (h) {
                    case "name": nameIdx = i; requiredColumns[0] = true; break;
                    case "iso_country": requiredColumns[1] = true; break;
                    case "municipality": muniIdx = i; requiredColumns[2] = true; break;
                    case "icao_code": icaoIdx = i; requiredColumns[3] = true; break;
                    case "iata_code": iataIdx = i; requiredColumns[4] = true; break;
                    case "coordinates": requiredColumns[5] = true; break;
                }
            }

            for (boolean req : requiredColumns) {
                if (!req) {
                    throw new IllegalStateException("Airport lookup malformed");
                }
            }

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] cols = splitCSV(line);
                if (cols.length < headers.length) {
                    throw new IllegalStateException("Airport lookup malformed");
                }
                for (String col : cols) {
                    if (col.trim().isEmpty()) {
                        throw new IllegalStateException("Airport lookup malformed");
                    }
                }
                
                String name = cols[nameIdx].trim();
                String icao = cols[icaoIdx].trim();
                String iata = cols[iataIdx].trim();
                String muni = cols[muniIdx].trim();

                icaoToName.put(icao, name);
                iataToName.put(iata, name);
                icaoToCity.put(icao, muni);
                iataToCity.put(iata, muni);
            }
        }
    }

    private String[] splitCSV(String line) {
        // -1 limit ensures trailing empty commas are preserved as empty strings
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }

    public String getName(String code, boolean isIcao) {
        return isIcao ? icaoToName.get(code) : iataToName.get(code);
    }

    public String getCity(String code, boolean isIcao) {
        return isIcao ? icaoToCity.get(code) : iataToCity.get(code);
    }
}
