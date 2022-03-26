package com.igvig.wordler;

import java.io.Console;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Wordler {

	private static Console console;
	private static final LinkedList<WordInfo> initialList = WordInfo.initialList();

	private static void showStats(final List<WordInfo> wordlist) {
		console.printf("%d words\n", wordlist.size());
		List<String> top10 = wordlist.stream().map(w -> w.getWord()).limit(10).collect(Collectors.toList());
		console.printf("'Best' words: %s\n", String.join(", ", top10));
	}

	private static String getWordEntered() {
		String retVal = null;
		while (retVal == null) {
			retVal = console.readLine("- Word you entered: ");
			retVal = retVal.trim().toLowerCase();
			if (retVal.length() != 5) {
				retVal = null;
				console.printf("  *** Invalid word: words must have exactly 5 characters\n");
			} else if (retVal.matches(".*[^a-z].*")) {
				retVal = null;
				console.printf("  *** Invalid word: words can only contain letters (a to z)\n");
			}
		}
		return retVal;
	}

	private static String getWordleResponse() {
		String retVal = null;
		while (retVal == null) {
			retVal = console.readLine("- Wordled result (. for black, G for green, Y for yellow): ");
			retVal = retVal.trim().toLowerCase();
			if (retVal.length() == 0) {
				return "";
			} else if (retVal.length() != 5) {
				retVal = null;
				console.printf("  *** Invalid response: responses must have exactly 5 characters\n");
			} else if (retVal.matches(".*[^\\.gy].*")) {
				retVal = null;
				console.printf(
						"  *** Invalid response: responses should consists of the three characters '.', 'G' (or 'g') and 'Y' (or 'y')");
			}
		}
		return retVal;
	}

	public static List<WordInfo> processBlack(final List<WordInfo> list, final char c, final int pos,
			final Set<String> knownLetters) {
		if (knownLetters.contains(String.valueOf(c))) {
			return list.stream().filter(wi -> (wi.getWord().charAt(pos) != c)).collect(Collectors.toList());
		} else {
			return list.stream().filter(wi -> !wi.getWord().contains(String.valueOf(c))).collect(Collectors.toList());
		}
	}

	public static List<WordInfo> processGreen(final List<WordInfo> list, final char c, final int pos) {
		return list.stream().filter(wi -> (wi.getWord().charAt(pos) == c)).collect(Collectors.toList());
	}

	public static List<WordInfo> processYellow(final List<WordInfo> list, final char c, final int pos) {
		return list.stream().filter(wi -> wi.getWord().contains(String.valueOf(c)))
				.filter(wi -> (wi.getWord().charAt(pos) != c)).collect(Collectors.toList());
	}

	public static void main(String[] args) throws Exception {
		console = System.console();
		String playAgain = "y";

		while ("y".equalsIgnoreCase(playAgain)) {
			console.printf("\n");
			List<WordInfo> currentList = initialList.stream().map(wi -> new WordInfo(wi.getWord(), wi.getScore()))
					.collect(Collectors.toList());
			boolean win = false;
			for (int round = 1; round < 7; round++) {
				console.printf("ROUND %d -------------------------------------\n", round);
				showStats(currentList);
				String guess = null;
				String result = "";
				while (result.length() == 0) {
					guess = getWordEntered();
					result = getWordleResponse();
				}
				if (result.equals("ggggg")) {
					console.printf("yay. you sooo good.\n");
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
				console.printf("wow. just wow. 'sucks' is a 5-letter word, eh?\n");
			}

			playAgain = console.readLine("\nPlay again (Y/N)?: ");
			if (playAgain.length() > 1) {
				playAgain = playAgain.substring(0, 1);
			}

		}
	}

}
