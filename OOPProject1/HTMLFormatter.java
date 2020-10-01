import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class HTMLFormatter {

	final static int OUTPUT_WIDTH = 80;
	final static String[] VOID_TAGS = {
		"area", "base", "br", "embed", "hr", "iframe", "img", 
		"input", "link", "meta", "param", "source", "track"
	};

	// This function take an HTML file as input, and writes to a text file as an
	// output.
	// This function formats Titles, Headers, Paragraphs, and tables in plain text.
	// Inputs:
	// Scanner html - HTML file to read from and format.
	// PrintWriter output - file to write the formatted plain text to.
	public static void formatHTMLToText(final Scanner html, final PrintWriter output){
		String htmlContents = "";
		while (html.hasNextLine()) {
			//Read in the HTML an ignore newlines + whitespace.
			htmlContents = htmlContents + html.nextLine().trim();
		}
		//This is not for text content, but to check for tags independent of case
		String lowerCaseHtmlContents = htmlContents.toLowerCase();
		if (lowerCaseHtmlContents.contains("<title")) {
			//print the contents of the title tag as a title
			printTitle(getContentsOfTag(getFirstTag(htmlContents, "title")), output);
		}
		String bodyContents = getContentsOfTag(getFirstTag(htmlContents, "body"));
		printBody(bodyContents, output);
	}

	//Handles printing the body of the HTML file.
	public static void printBody(final String text, final PrintWriter output) {
		int currentPos = 0; 
		String body = text.trim();
		while (currentPos < body.length()) {
			//Indicates the first tag after current pos
			int tagOpeningStart = body.indexOf("<", currentPos) + 1;
			if (tagOpeningStart == 0) return; //i.e. body.indexOf("<", currentPos) == -1
			//The end of the first openeing tag after current pos
			int tagOpeningEnd = body.indexOf(">", tagOpeningStart);
			//The end of the name of the first tag after current pos
			int tagNameEnd = Math.min(body.indexOf(" ", tagOpeningStart), tagOpeningEnd);
			//The name of the first tag after current pos
			String tagName = body.substring(tagOpeningStart, tagNameEnd).trim();
			if (tagName.equals("br")) {
				currentPos += (tagOpeningEnd - tagOpeningStart); 
				output.println();
				continue;
			} else if (isVoidTag(tagName)) {
				currentPos += (tagOpeningEnd - tagOpeningStart);
				continue;
			}
			String tagContent = getFirstTag(body.substring(currentPos), tagName);
			if (tagName.equals("h1") || tagName.equals("h2") || tagName.equals("h3") ||
			tagName.equals("h4") || tagName.equals("h5") || tagName.equals("h6")) {
				printHeading(getContentsOfTag(tagContent).trim(), output);
			}
			else if (tagName.equals("p")) {
				printParagraph(getContentsOfTag(tagContent).trim(), output);
			}
			else if (tagName.equals("table")) {

				printTable(getContentsOfTag(tagContent).trim(), output);
			}
			/*else {
				System.out.println(tagName);
				printBody(getContentsOfTag(tagContent).trim(), output);
			}*/
			currentPos += tagContent.length();
		}
	}

	//Prints the specified String to output as a paragraph.
	public static void printParagraph(final String text, final PrintWriter output) {
		String[] lines = text.split("<br>");
		for (String line : lines) {
			String out = wrapText(purgeTags(line.trim()));
			output.println(out);
		}
		output.println();
	}

	//Prints the specified String to output as a heading.
	public static void printHeading(final String heading, final PrintWriter output) {
		String[] lines = heading.split("<br>");
		for (String line : lines) {
			String out = wrapText(purgeTags(line.trim().toUpperCase()));
			output.println(out);
		}
		output.println();
	}

	//Prints the specified String to output as a title.
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
		output.println();
	}

	public static void printTable(final String table, final PrintWriter output) {
		//remove thead and tbody tags
		String fixedTable = table.trim()
			.replaceAll("<thead>", "")
			.replaceAll("</thead>", "")
			.replaceAll("<tbody>", "")
			.replaceAll("</tbody>", "");
		
		int currentPos = 0;
		while (currentPos < fixedTable.length()) {
			//Get the first row after the current pos
			String rowTag = getFirstTag(fixedTable.substring(currentPos), "tr");
			String row = getContentsOfTag(rowTag);
			String line = "";
			int currentRowPos = 0;
			//While the row still has th or td tags...
			while (row.substring(currentRowPos).contains("<th") || row.substring(currentRowPos).contains("<td")) {
				String dataTag = "";
				if (row.substring(currentRowPos).contains("<th")) dataTag = getFirstTag(row.substring(currentRowPos), "th"); 
				else dataTag = getFirstTag(row.substring(currentRowPos), "td");
				//The contents of the data tag, purged of any internal tags
				String data = purgeTags(getContentsOfTag(dataTag));
				line += data;
				currentRowPos += dataTag.length();
				//If this is not the last data tag...
				if (row.substring(currentRowPos).contains("<th") || row.substring(currentRowPos).contains("<td")){
					line += ", ";
				}
			}
			currentPos += rowTag.length();
			output.println(wrapText(line));
		}
		output.println();
	}

	//Returns the contents, including the tag itself, of the first of the indicated tag in input.
	public static String getFirstTag(final String input, final String tag) {
		String inputLower = input.toLowerCase();
		int indexOfTag = inputLower.indexOf("<" + tag);
		if (indexOfTag == -1) return "";
		int tagOpeningEnd = input.indexOf(">", indexOfTag) + 1;
		int tagClosingStart = inputLower.indexOf("</" + tag, tagOpeningEnd);
		int tagClosingEnd = inputLower.indexOf(">", tagClosingStart);

		//The number of the same tag that is between the base tag and the current end point
		/* E.g. <table>... <table> </table> ... </table>
									^ current position
							seen one, need to account for it */
		int numberOfSimilarTags = numberOfOccurences(inputLower.substring(tagOpeningEnd, tagClosingStart + 1), "<" + tag);
		int numberOfSimilarTagsAccountedFor = 0;

		while(numberOfSimilarTags > numberOfSimilarTagsAccountedFor) {
			//Look at the closing tag AFTER the one we saw if there are any unnacounted same tags
			tagClosingStart = inputLower.indexOf("</" + tag, tagClosingEnd);
			tagClosingEnd = inputLower.indexOf(">", tagClosingStart);
			numberOfSimilarTagsAccountedFor ++;
			numberOfSimilarTags = numberOfOccurences(inputLower.substring(tagOpeningEnd, tagClosingStart + 1), "<" + tag);
			
		}
		return input.substring(indexOfTag, tagClosingEnd + 1).trim();
	}

	//Returns the number of times checkFor occurs in str.
	static int numberOfOccurences(String str, String checkFor) {
		int pos = 0;
		int out = 0;
		while (str.indexOf(checkFor, pos+1) >= 0) {
			out += 1;
			pos = str.indexOf(checkFor, pos+1);
		}
		return out;
	}

	//Returns true if the given tag name is in the array of void tag names.
	static boolean isVoidTag(String tag) {
		boolean in = false;
		for (String vtag : VOID_TAGS) {
			if (vtag.equals(tag)) in = true;
		}
		return in;
	}

	//input is the HTML tag and it outputs the contents only, without the tag itself.
	public static String getContentsOfTag(final String tagText) {
		int indexOfOpeningTag = tagText.indexOf(">");
		int indexOfClosingTag = tagText.lastIndexOf("<");
		return tagText.substring(indexOfOpeningTag + 1, indexOfClosingTag).trim();
	}

	public static String purgeTags(String input) {
		String text = input.trim();
		while (text.contains("<")) {
			text = text.substring(0, text.indexOf("<")) + text.substring(text.indexOf(">") + 1);
		}
		return text;
	}

	//The output of this function is the input with newlines so that no line is more than 
	//OUTPUT_LENGTH.
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
		final File outputFile = new File(outputFileName);
		if (!inputFile.exists() || inputFile.isDirectory()) {
			System.out.println("Expected input file at " + inputFileName);
			return;
		}
		Scanner input = new Scanner(inputFile);
		PrintWriter output = new PrintWriter(outputFile);
		formatHTMLToText(input, output);
		System.out.println("Wrote to file: " + outputFile.getAbsolutePath());
		input.close();
		output.close();
		
	}
}