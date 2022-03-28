package com.igvig.wordler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Wordler {

	private static Scanner scanner;
	private static final LinkedList<WordInfo> initialList = WordInfo.initialList();

	private static void out(final String format, Object... objects) {
		System.out.printf(format, objects);
	}

	private static String in(final String prompt) {
		System.out.printf(prompt);
		return scanner.nextLine();
	}

	private static String getWordEntered() {
		String retVal = null;
		while (retVal == null) {
			retVal = in("- Word you entered: ");
			retVal = retVal.trim().toLowerCase();
			if (retVal.length() != 5) {
				retVal = null;
				out("  *** Invalid word: words must have exactly 5 characters\n");
			} else if (retVal.matches(".*[^a-z].*")) {
				retVal = null;
				out("  *** Invalid word: words can only contain letters (a to z)\n");
			}
		}
		return retVal;
	}

	private static String getWordleResponse() {
		String retVal = null;
		while (retVal == null) {
			retVal = in("- Wordled result (. for black, G for green, Y for yellow): ");
			retVal = retVal.trim().toLowerCase();
			if (retVal.length() == 0) {
				return "";
			} else if (retVal.length() != 5) {
				retVal = null;
				out("  *** Invalid response: responses must have exactly 5 characters\n");
			} else if (retVal.matches(".*[^\\.gy].*")) {
				retVal = null;
				out("  *** Invalid response: responses should consists of the three characters '.', 'G' (or 'g') and 'Y' (or 'y')");
			}
		}
		return retVal;
	}

	public static void main(String[] args) throws Exception {
		scanner = new Scanner(System.in);

		if ((args != null) && (args.length > 0)) {
			final List<String> badArguments = Arrays.stream(args).filter(arg -> {
				return (!arg.equalsIgnoreCase("--dump") && !arg.equalsIgnoreCase("--help"));
			}).collect(Collectors.toList());
			if (!badArguments.isEmpty()) {
				out("Unrecognized arguments: '%s'\n", String.join("','", badArguments));
				printHelp();
			} else if (args.length > 1) {
				out("Only one argument is allowed\n");
				printHelp();
			} else if (args[0].equalsIgnoreCase("--help")) {
				printHelp();
			} else if (args[0].equalsIgnoreCase("--dump")) {
				List<WordInfo> wordList = initialList.stream().map(wi -> new WordInfo(wi.getWord(), wi.getScore()))
						.collect(Collectors.toList());
				wordList.forEach(wi -> out("%s,%d\n", wi.getWord(), wi.getScore()));
			} else {
				out("How did we get here??!?!?!");
			}
			return;
		}
		String playAgain = "y";

		while ("y".equalsIgnoreCase(playAgain)) {
			out("\n");
			List<WordInfo> currentList = initialList.stream().map(wi -> new WordInfo(wi.getWord(), wi.getScore()))
					.collect(Collectors.toList());
			boolean win = false;
			for (int round = 1; round < 7; round++) {
				out("ROUND %d -------------------------------------\n", round);
				showStats(currentList);
				String guess = null;
				String result = "";
				while (result.length() == 0) {
					guess = getWordEntered();
					result = getWordleResponse();
				}
				if (result.equals("ggggg")) {
					out("yay. you sooo good.\n");
					win = true;
					break;
				} else {
					final Set<String> knownLetters = new HashSet<>();
					for (int i = 0; i < 5; i++) {
						char r = result.charAt(i);
						char c = guess.charAt(i);
						if (r == '.') {
							currentList = processBlack(currentList, c, i, knownLetters);
						} else if (r == 'g') {
							knownLetters.add(String.valueOf(c));
							currentList = processGreen(currentList, c, i);
						} else if (r == 'y') {
							knownLetters.add(String.valueOf(c));
							currentList = processYellow(currentList, c, i);
						}
					}
				}
			}
			if (!win) {
				out("wow. just wow. 'sucks' is a 5-letter word, eh?\n");
			}

			playAgain = in("\nPlay again (Y/N)?: ");
			if (playAgain.length() > 1) {
				playAgain = playAgain.substring(0, 1);
			}

		}
	}

	private static void printHelp() {
		out("\nNAME\n\twordler - a program to help guess the New York Times 'Wordle' puzzle.\n\nSYNOPSIS\n");
		out("\twordle [--help|--dump]\n\nDESCRIPTION\n");
		out("\tWhen  run without arguments, this utility will help solve the New York Times 'Wordle' online puzzle.  It\n");
		out("\tdoes  so by proposing words to 'guess', then prompting the user for the Wordle response. The response is\n");
		out("\tthen analyzed and a new set of 'guess' words is presented.\n\n");
		out("OPTIONS\n\t--help\n\t\tDisplays this help message.\n\t--dump\n");
		out("\t\tPrints  out the internal dictionary of words. Each line consists of the word and the internal score\n");
		out("\t\tassigned to each word.\n");
	}

	private static List<WordInfo> processBlack(final List<WordInfo> list, final char c, final int pos,
			final Set<String> knownLetters) {
		if (knownLetters.contains(String.valueOf(c))) {
			return list.stream().filter(wi -> (wi.getWord().charAt(pos) != c)).collect(Collectors.toList());
		} else {
			return list.stream().filter(wi -> !wi.getWord().contains(String.valueOf(c))).collect(Collectors.toList());
		}
	}

	private static List<WordInfo> processGreen(final List<WordInfo> list, final char c, final int pos) {
		return list.stream().filter(wi -> (wi.getWord().charAt(pos) == c)).collect(Collectors.toList());
	}

	private static List<WordInfo> processYellow(final List<WordInfo> list, final char c, final int pos) {
		return list.stream().filter(wi -> wi.getWord().contains(String.valueOf(c)))
				.filter(wi -> (wi.getWord().charAt(pos) != c)).collect(Collectors.toList());
	}

	private static void showStats(final List<WordInfo> wordlist) {
		out("%d words\n", wordlist.size());
		List<String> top10 = wordlist.stream().map(w -> w.getWord()).limit(10).collect(Collectors.toList());
		out("'Best' words: %s\n", String.join(", ", top10));
	}

}
