import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class HTMLFormatter {

	final static int OUTPUT_WIDTH = 80;

	// This function take an HTML file as input, and writes to a text file as an
	// output.
	// This function formats Titles, Headers, Paragraphs, and tables in plain text.
	// Inputs:
	// Scanner html - HTML file to read from and format.
	// PrintWriter output - file to write the formatted plain text to.
	public static void formatHTMLToText(final Scanner html, final PrintWriter output) throws IOException{
		
	}

	public static void printTitle(String title, final PrintWriter output){
		
	}

	public static String wrapText(String text) {
		int currentPos = 0;
		String out = text;
		while (currentPos + 80 < out.length()) {
			currentPos += OUTPUT_WIDTH;
			int spaceIndex = out.lastIndexOf(" ", currentPos);
			if (spaceIndex <= currentPos - OUTPUT_WIDTH) {
				spaceIndex = currentPos;
			}
			out = out.substring(0, spaceIndex) 
			+ "\n"
			+ out.substring(spaceIndex + 1); 
			currentPos = spaceIndex;
		}
		return out;
	}

	public static void main(final String[] args) throws IOException{
		/*String inputFileName;
		String outputFileName;
		if (args.length >= 2) {
			//Get File names from command line arguments if possible
			inputFileName = args[0];
			outputFileName = args[1];
		} else {
			Scanner prompt = new Scanner(System.in);
			System.out.println("Enter path for input file: ");
			inputFileName = prompt.nextLine();
			System.out.println("Enter path for output file: ");
			outputFileName = prompt.nextLine();
			prompt.close();
		}
		File inputFile = new File(inputFileName);
		if (!inputFile.exists() || inputFile.isDirectory()) {
			System.out.println("Expected input file at " + inputFileName);
			return;
		}
		Scanner input = new Scanner (inputFile);
		PrintWriter output = new PrintWriter(outputFileName);
		formatHTMLToText(input, output);
		input.close();
		output.close();*/
		System.out.println(wrapText("any nba player could make MIOM top 100 if they just spent their free time in the bubble playing netplay. they would maybe have to skip a few basketball practices, or reduce their playoff minutes to maintain focus, but with a few sacrifices we could def see JR Smith beating Amsah"));
	}
}