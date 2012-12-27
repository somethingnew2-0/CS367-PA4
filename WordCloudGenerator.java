///////////////////////////////////////////////////////////////////////////////
// Title:            WordCloudGenerator
// Files:            WordCloudGenerator.java, ArrayHeap.java,
//						BSTDictornary.java, BSTDictionaryIterator.java,
//						BSTnode.java, DictionaryADT.java, KeyWord.java,
//						DuplicateKeyException.java, Prioritizable.java,
//						PriorityQueueADT.java
// Semester:         Fall 2011
//
// Author:           Peter Collins pmcollins2@wisc.edu
// CS Login:         pcollins
// Lecturer's Name:  Beck, Hasti
// Lab Section:      NA
//
///////////////////////////////////////////////////////////////////////////////

import java.util.*;
import java.io.*;

/**
 * An application meant for making an html word cloud
 * 
 * <p>
 * Bugs: none known
 * 
 * @author Peter Collins
 */
public class WordCloudGenerator {
	/**
	 * The main method generates a word cloud as described in the program
	 * write-up. You will need to add to the code given here.
	 * 
	 * @param args
	 *            the command-line arguments that determine where input and
	 *            output is done:
	 *            <ul>
	 *            <li>args[0] is the name of the input file</li>
	 *            <li>args[1] is the name of the output file</li>
	 *            <li>args[2] is the name of the file containing the words to
	 *            ignore when generating the word cloud</li>
	 *            <li>args[3] is the maximum number of words to include in the
	 *            word cloud</li>
	 *            </ul>
	 */
	public static void main(String[] args) {
		Scanner in = null; // for input from text file
		PrintStream out = null; // for output to html file
		Scanner inIgnore = null; // for input from ignore file
		int maxWords = 0; // for representation of the max words allowed
		// dictionary used to store keywords
		DictionaryADT<KeyWord> dictionary = new BSTDictionary<KeyWord>();

		// Check the command-line arguments and set up the input and output

		// Make sure there are exactly four command-line arguments
		if (args.length != 4) {
			System.err
					.println("Usage: java WordCloudGenerator inputFileName outputFileName ignoreFileName maxWords");
			System.exit(1);
		}

		// Make sure the input file exists and can be read
		try {
			File inFile = new File(args[0]);
			if (!inFile.exists() || !inFile.canRead()) {
				System.err.println("Error: cannot access file " + args[0]);
				System.exit(1);
			}

			in = new Scanner(inFile);
		} catch (FileNotFoundException e) {
			System.err.println("Error: cannot access file " + args[0]);
			System.exit(1);
		}

		// Make sure the ignore input file exists and can be read as well
		try {
			File inFile = new File(args[2]);
			if (!inFile.exists() || !inFile.canRead()) {
				System.err.println("Error: cannot access file " + args[2]);
				System.exit(1);
			}

			inIgnore = new Scanner(inFile);
		} catch (FileNotFoundException e) {
			System.err.println("Error: cannot access file " + args[2]);
			System.exit(1);
		}

		// Parse the max words command line argument and make sure it's positive
		try {
			maxWords = Integer.parseInt(args[3]);
			if (maxWords <= 0) {
				System.err
						.println("Error: maxWords must be a positive integer");
				System.exit(1);
			}
		} catch (NumberFormatException e) {
			System.err.println("Error: maxWords must be a positive integer");
			System.exit(1);
		}

		// Make sure we can write to the given output filename
		try {
			// write to given file name
			File outFile = new File(args[1]);
			// warn if it exists
			if (outFile.exists()) {
				System.err.println("Warning: file " + args[1]
						+ " already exists, will be overwritten");
			}
			// stop if we can't write to the file
			if (outFile.exists() && !outFile.canWrite()) {
				System.err.println("Error: cannot write to file " + args[1]);
				System.exit(1);
			}

			// open the file to write to
			out = new PrintStream(outFile);

		} catch (FileNotFoundException e) {
			// catch if we can't write
			System.err.println("Error: cannot write to file " + args[1]);
			System.exit(1);
		}

		// Create the dictionary of words to ignore
		// You do not need to change this code.
		DictionaryADT<String> ignore = new BSTDictionary<String>();
		while (inIgnore.hasNext()) {
			try {
				ignore.insert(inIgnore.next().toLowerCase());
			} catch (DuplicateKeyException e) {
				// if there is a duplicate, we'll just ignore it
			}
		}

		// Process the input file line by line
		// Note: the code below just prints out the words contained in each
		// line. You will need to replace that code with code to generate
		// the dictionary of KeyWords.
		while (in.hasNext()) {
			String line = in.nextLine();
			List<String> words = parseLine(line);

			// Iterate through all the word on this line
			KeyWord keyword;
			for (String word : words) {
				// Skip any ignored words if found
				if (ignore.lookup(word.toLowerCase()) == null) {
					try {
						// Try inserting the keyword
						keyword = new KeyWord(word.toLowerCase());
						keyword.increment();
						dictionary.insert(keyword);
					} catch (DuplicateKeyException e) {
						// If the keyword is already in the dictionary just
						// increment the occurrences
						keyword = dictionary.lookup(new KeyWord(word
								.toLowerCase()));
						keyword.increment();
					}
				}
			}

		} // end while

		// Add the dictionary to the priority queue by iterating through the
		// dictionary
		PriorityQueueADT<KeyWord> priorityQueue = new ArrayHeap<KeyWord>();
		for (KeyWord keyword : dictionary) {
			priorityQueue.insert(keyword);
		}

		// Construct a new dictionary and add the max number of words back to it
		dictionary = new BSTDictionary<KeyWord>();
		for (int i = 0; i < maxWords; i++) {
			// Make sure we actually have enough words in the priority queue
			if (!priorityQueue.isEmpty()) {
				try {
					// Insert the max from the priority queue back into the
					// dictionary
					dictionary.insert(priorityQueue.removeMax());
				} catch (DuplicateKeyException e) {
					// Can't really happen, but catch anyways
				}
			} else {
				// If we don't, we're done, break!
				break;
			}
		}

		// Great the html and save it into the output filename!
		generateHtml(dictionary, out);
	}

	/**
	 * Parses the given line into an array of words.
	 * 
	 * @param line
	 *            a line of input to parse
	 * @return a list of words extracted from the line of input in the order
	 *         they appear in the line
	 * 
	 *         DO NOT CHANGE THIS METHOD.
	 */
	private static List<String> parseLine(String line) {
		String[] tokens = line.split("[ ]+");
		ArrayList<String> words = new ArrayList<String>();
		for (int i = 0; i < tokens.length; i++) { // for each word

			// find index of first digit/letter
			boolean done = false;
			int first = 0;
			String word = tokens[i];
			while (first < word.length() && !done) {
				if (Character.isDigit(word.charAt(first))
						|| Character.isLetter(word.charAt(first)))
					done = true;
				else
					first++;
			}

			// find index of last digit/letter
			int last = word.length() - 1;
			done = false;
			while (last > first && !done) {
				if (Character.isDigit(word.charAt(last))
						|| Character.isLetter(word.charAt(last)))
					done = true;
				else
					last--;
			}

			// trim from beginning and end of string so that is starts and
			// ends with a letter or digit
			word = word.substring(first, last + 1);

			// make sure there is at least one letter in the word
			done = false;
			first = 0;
			while (first < word.length() && !done)
				if (Character.isLetter(word.charAt(first)))
					done = true;
				else
					first++;
			if (done)
				words.add(word);
		}

		return words;
	}

	/**
	 * Generates the html file using the given list of words. The html file is
	 * printed to the provided PrintStream.
	 * 
	 * @param words
	 *            a list of KeyWords
	 * @param out
	 *            the PrintStream to print the html file to
	 * 
	 *            DO NOT CHANGE THIS METHOD
	 */
	private static void generateHtml(DictionaryADT<KeyWord> words,
			PrintStream out) {
		String[] colors = { "6F", "6A", "65", "60", "5F", "5A", "55", "50",
				"4F", "4A", "45", "40", "3F", "3A", "35", "30", "2F", "2A",
				"25", "20", "1F", "1A", "15", "10", "0F", "0A", "05", "00" };
		int initFontSize = 80;

		// Print the header information including the styles
		out.println("<head>\n<title>Word Cloud</title>");
		out.println("<style type=\"text/css\">");

		// Each style is of the form:
		// .styleN {
		// font-size: X%;
		// color: #YYAA;
		// }
		// where N and X are integers and Y is two hexadecimal digits
		for (int i = 0; i < colors.length; i++)
			out.println(".style" + i + " {\n    font-size: "
					+ (initFontSize + i * 20) + "%;\n    color: #" + colors[i]
					+ colors[i] + "AA;\n}");

		out.println("</style>\n</head>\n<body><p>");

		// Find the minimum and maximum values in the collection of words
		int min = Integer.MAX_VALUE, max = 0;
		for (KeyWord word : words) {
			int occur = word.getOccurrences();
			if (occur > max)
				max = occur;
			else if (occur < min)
				min = occur;
		}

		double slope = (colors.length - 1.0) / (max - min);

		for (KeyWord word : words) {
			out.print("<span class=\"style");

			// Determine the appropriate style for this value using
			// linear interpolation
			// y = slope *(x - min) (rounded to nearest integer)
			// where y = the style number
			// and x = number of occurrences
			int index = (int) Math.round(slope * (word.getOccurrences() - min));

			out.println(index + "\">" + word.getWord() + "</span>&nbsp;");
		}

		// Print the closing tags
		out.println("</p></body>\n</html>");
	}
}
