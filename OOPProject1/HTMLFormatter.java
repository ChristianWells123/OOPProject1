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
	public static void formatHTMLToText(final Scanner html, final PrintWriter output){
		String htmlContents = "";
		while (html.hasNextLine()) {
			htmlContents = htmlContents + html.nextLine();
		}
		System.out.println(htmlContents);
		if (htmlContents.toLowerCase().contains("<title")) {

		}
	}

	public static void printParagraph(final String text, final PrintWriter output) {
		String out = wrapText(text);
		output.println(out);
		output.println();
	}

	public static void printHeading(final String heading, final PrintWriter output) {
		String out = wrapText(heading.toUpperCase());
		output.println(out);
		output.println();
	}

	public static void printTitle(final String title, final PrintWriter output) {
		String out = title.toUpperCase();
		//If title is too big to fit on one line, separate it into multiple lines
		String[] lines = wrapText(out).split("\n");
		for (String line : lines) {
			final int spaces = (OUTPUT_WIDTH / 2) - (line.length() / 2);
			//Print spaces until title is centered
			for (int i = 0; i < spaces; i++) {
				line = " " + line;
			}
			output.println(line);
		}
		output.println();
	}

	public static String getContentsOfTag(final String tagText) {
		int indexOfOpeningTag = tagText.indexOf(">");
		int indexOfClosingTag = tagText.lastIndexOf("<");
		return tagText.substring(indexOfOpeningTag, indexOfClosingTag);
	}

	public static String wrapText(final String text) {
		int currentPos = 0;
		String out = text;
		while (currentPos + OUTPUT_WIDTH < out.length()) {
			currentPos += OUTPUT_WIDTH;
			//Check for spaces before the current position
			int spaceIndex = out.lastIndexOf(" ", currentPos);
			//If there is no space in this chunk of characters (i.e. one long word)...
			if (spaceIndex <= currentPos - OUTPUT_WIDTH) {
				spaceIndex = currentPos;
				//add a newline at the currentPos
				out = out.substring(0, spaceIndex) + "\n" + out.substring(spaceIndex);
			}
			else {
				//replace the space with a newline
				out = out.substring(0, spaceIndex) + "\n" + out.substring(spaceIndex + 1);
			}
			currentPos = spaceIndex;
		}
		return out;
	}

	public static void main(final String[] args) throws IOException {
		String inputFileName;
		String outputFileName;
		if (args.length >= 2) {
			// Get File names from command line arguments if possible
			inputFileName = args[0];
			outputFileName = args[1];
		} else {
			final Scanner prompt = new Scanner(System.in);
			System.out.println("Enter path for input file: ");
			inputFileName = prompt.nextLine();
			System.out.println("Enter path for output file: ");
			outputFileName = prompt.nextLine();
			prompt.close();
		}
		final File inputFile = new File(inputFileName);
		if (!inputFile.exists() || inputFile.isDirectory()) {
			System.out.println("Expected input file at " + inputFileName);
			return;
		}
		Scanner input = new Scanner(inputFile);
		PrintWriter output = new PrintWriter(outputFileName);
		System.out.println(inputFileName);
		formatHTMLToText(input, output);
		input.close();
		output.close();
		
	}
}