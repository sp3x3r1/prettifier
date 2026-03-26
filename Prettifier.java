import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class Prettifier {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("-h")) {
            printUsage();
            return;
        }
        
        if (args.length != 3) {
            printUsage();
            return;
        }

        String inputPath = args[0];
        String outputPath = args[1];
        String lookupPath = args[2];

        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            System.out.println("Input not found");
            return;
        }

        File lookupFile = new File(lookupPath);
        if (!lookupFile.exists()) {
            System.out.println("Airport lookup not found");
            return;
        }

        AirportLookup lookup;
        try {
            lookup = new AirportLookup(lookupPath);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return;
        } catch (Exception e) {
            System.out.println("Airport lookup malformed");
            return;
        }

        String inputData;
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(inputPath));
            inputData = new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Input not found");
            return;
        }

        // Process raw for file
        String processedRaw = ItineraryProcessor.process(inputData, lookup, false);
        // Process colored for stdout
        String processedAnsi = ItineraryProcessor.process(inputData, lookup, true);

        // Write to output file. Output file must not be created if error.
        // We do it before printing to stdout so we only print if writing succeeds.
        try {
            Files.write(Paths.get(outputPath), processedRaw.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Error writing to output file");
            return;
        }

        // Output to stdout as an extra impressive feature, formatted.
        System.out.println(processedAnsi);
    }

    private static void printUsage() {
        System.out.println("itinerary usage:");
        System.out.println("$ java Prettifier.java ./input.txt ./output.txt ./airport-lookup.csv");
    }
}
