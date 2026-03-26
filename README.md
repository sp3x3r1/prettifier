# Itinerary Prettifier

"Anywhere Holidays" command line tool for automating the prettification of administrator-friendly flight itinerary text files into customer-friendly documents.

## Project Overview
This project reads an administrator text-based itinerary, processes ISO 8601 dates/times into human-readable customer versions, translates internal airport/city codes into their full names via a dynamically parsed CSV, and properly cleans repetitive whitespace formatting.

## Setup and Installation Instructions
1. Ensure you have Java installed on your machine (Java 8 or higher).
2. Open your terminal or command prompt.
3. Navigate to the project directory `/Users/taneljarve/Documents/vibe/antigrav pretti/`.
4. Run the following command to compile the source code:
   `javac *.java`

## Usage Guide
The tool requires three arguments: the filepath to the raw itinerary, the filepath to the desired formatted output, and the filepath to a valid airport lookup CSV.

```bash
$ java Prettifier ./input.txt ./output.txt ./airport-lookup.csv
```

To display usage information, you can run the tool with the `-h` flag:
```bash
$ java Prettifier -h
```

## Error Handling
If you provide an invalid file path or a corrupted airport lookup CSV, the program will gracefully decline to execute and immediately exit with an explanation (e.g., "Input not found", "Airport lookup malformed"). **Under no circumstances will the prettifier overwrite an existing output file if an error occurs.**

## Bonus Features Implemented
1. **Dynamic Airport Lookup Columns**: The CSV reader relies on finding the header names logically instead of relying on exact index locations. Order your CSV freely as long as all required columns exist!
2. **City Name Processing**: Utilizing the `*` prefix combined with standard codes (e.g., `*#LHR` for London or `*##EGLL`), you can explicitly request the city name linked to the airport.
3. **ANSI Output Formatting**: While your `.txt` destination file retains a strict, unformatted raw data string ensuring wide compatibility with your travel systems, the app prints a vividly highlighted ANSI-colored variant to standard output `stdout` for a more impressive terminal experience! Dates feature magenta highlights and airport names display in cyan!
