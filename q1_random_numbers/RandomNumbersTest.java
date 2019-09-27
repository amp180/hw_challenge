import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

class TestResult {
    final String name;
    final boolean passed;
    final List<String> errors;

    TestResult(final String name, final boolean passed, final List<String> errors) {
        this.name = name;
        this.passed = passed;
        this.errors = errors;
    }

    public String toString(){
        String s = String.format("%s: %b\n", this.name, this.passed);
        s = s + String.join("\n", this.errors) + "\n";
        return s;
    }
}

public class RandomNumbersTest {
    
    public static void main(final String[] args) {
        final String filename = getFilenameFromArgs(args);
        final Path filePath = getPathFromFilename(filename);
        final List<String> lines = getLines(filePath);

        final TestResult lineNumbersResult = testLineNumbers(lines);
        final TestResult randomNumbersResult = testRandomNumbers(lines);
        final TestResult testLineResult = testLinePattern(lines);

        final String output = lineNumbersResult.toString() 
            + randomNumbersResult.toString() 
            + testLineResult.toString();
        
        writeOutput(output);
    }

    static String getFilenameFromArgs(final String[] args){
        /* Get the filename from the arguments array or exit the program with a usage message. */
        final long ARGSCOUNT = 1;
        if(args.length != ARGSCOUNT){
            System.err.println("Usage: java RandomNumbersTest \"filenameToTest\"");
            System.exit(-1);
        }
        return args[0];
    }

    static void writeOutput(final String output){
        final String outputFile = "output.txt";
        File file = null;
        FileWriter writer = null;
        try {
            file = new File(outputFile);
            writer = new FileWriter(file);
            writer.write(output);
        } catch (IOException e) {
            System.err.printf("Could not write to %s.\n", outputFile);
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException er) {
                er.printStackTrace();
            }
            
        }

    }

    static Path getPathFromFilename(final String pathString){
        Path p = null;
        try {
            p = Paths.get(pathString);
        } catch (InvalidPathException e){
            System.err.println(e);
            System.err.printf("Could not find file: \"%s\"\n", pathString);
            System.exit(-2);
        }
        return p;
    }

    static List<String> getLines(final Path path){
        /* Read all lines in a file into an array, or exit on failure */
        List<String> lines = null;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            System.err.println(e);
            System.err.println("Error while reading lines from file.");
            System.exit(-3);
        };
        return lines;
    };

    static TestResult testLineNumbers(final List<String> lines) {
        final String numberedLineRegex= "([0-9]{1,}).*";
        final Pattern numberedLinePattern = Pattern.compile(numberedLineRegex);
        boolean passed = true;
        ArrayList<String> errors = new ArrayList<>();

        for(int i = 0; i<lines.size(); i++){
            
            final String line = lines.get(i);
            final int lineNumber = i + 1;
            //Check that the line starts with digits and capture them.
            final Matcher match = numberedLinePattern.matcher(line);
            if (!match.matches()){
                passed = false;
                String error = String.format("Line %d: Line does not start with digits.", lineNumber);
                errors.add(error);
                continue;
            }
            
            // Parse the digits into an int.
            final String numberString = match.group(1);
            Integer readLineNumber = null;
            try {
                readLineNumber = Integer.parseInt(numberString);
            } catch (NumberFormatException e) {
                // Allow readLineNumber to be null.
            }

            // Check the number parsed successfully.
            if (readLineNumber == null){
                passed = false;
                String error = String.format("Line %d: Failed to parse integer.", lineNumber);
                errors.add(error);
                continue;
            }

            // Check the line number is correct.
            if(!readLineNumber.equals(lineNumber)) {
                passed = false;
                String error = String.format("Line %d: Has incorrect line number %d.", lineNumber, readLineNumber);
                errors.add(error);
            }

            // Check the line number is less than 50.
            if(readLineNumber.compareTo(51)== 0) {
                passed = false;
                String error = String.format("Line %d: Greater than 50 lines.", lineNumber, readLineNumber);
                errors.add(error);
            }
        }
        return new TestResult("Test Line Numbers", passed, errors);
    }


    static TestResult testRandomNumbers(final List<String> lines) {
        final String lineEndRegex= "^.*?([0-9]+)$";
        final Pattern lineEndPattern = Pattern.compile(lineEndRegex);
        //parse the line
        boolean passed = true;
        ArrayList<String> errors = new ArrayList<>();

        for(int i = 0; i<lines.size(); i++){
            final String line = lines.get(i);
            final int lineNumber = i + 1;
            //Check that the line ends with digits and capture them.
            final Matcher match = lineEndPattern.matcher(line);
            if (!match.matches()){
                passed = false;
                String error = String.format("Line %d: Line does not end with digits.", lineNumber);
                errors.add(error);
                continue;
            }
            
            // Parse the digits into an int.
            final String numberString = match.group(1);
            Integer randomNumber = null;
            try {
                randomNumber = Integer.parseInt(numberString);
            } catch (NumberFormatException e) {
                // Allow randomNumber to be null.
            }

            // Fail the test if the number isn't parsable.
            if (randomNumber == null){
                passed = false;
                String error = String.format("Line %d: Failed to parse integer.", lineNumber);
                errors.add(error);
                continue;
            }

            // Check the random number is greater than or equal to than 100
            if(randomNumber.compareTo(100) != 1) {
                passed = false;
                String error = String.format("Line %d: Random number is less than 100. Number is: %d", lineNumber, randomNumber);
                errors.add(error);
            }

            // Check the random number is less than or equal to 500
            if(randomNumber.compareTo(500) != -1) {
                passed = false;
                String error = String.format("Line %d: Random number is greater than 500. Number is %d", lineNumber, randomNumber);
                errors.add(error);
            }

            // Warn if the random number equals the line number, but don't fail.
            if(randomNumber.equals(lineNumber)) {
                String error = String.format("Warning line %d: Random number is equal to line number, This shouldn't happen often.", lineNumber, randomNumber);
                errors.add(error);
            }
        }
        return new TestResult("Test Random Numbers", passed, errors);
    }

    static TestResult testLinePattern(final List<String> lines) {
        final String lineFullRegex= "([0-9]{1,2})\t([0-9]{3})";
        final Pattern lineFullPattern = Pattern.compile(lineFullRegex);
        //parse the line
        boolean passed = true;
        ArrayList<String> errors = new ArrayList<>();

        for(int i = 0; i<lines.size(); i++){
            final String line = lines.get(i);
            final int lineNumber = i + 1;
			// Check that each line follows the full pattern.
            final Matcher match = lineFullPattern.matcher(line);
            if (!match.matches()){
                passed = false;
                String error = String.format("Line %d: Line is not a two digit number followed by a tab followed by three digits.", lineNumber);
                errors.add(error);
                continue;
            }
        }
        return new TestResult("Test Line Pattern", passed, errors);
    }

}