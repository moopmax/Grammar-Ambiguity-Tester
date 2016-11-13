import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * A class that is used to test ambiguity of a context-free grammar. Please note 
 * that testing grammar's ambiguity is an undecidable problem, so there will be 
 * difficult grammars that will make the test throw a StackOverflowException, 
 * but it should handle reasonable grammars.
 */
public class GrammarAmbiguityTester {
	
	public static void main(String[] args) {
		GrammarAmbiguityTester.testGrammar("cfg3.txt", 3);
	}

	/* The method that performs the ambiguity test. */
	public static void testGrammar(String filePath, int maxWordLengthToCheck) {
		CFG cfg = null;
		try {
			cfg = new CFG(new File(filePath));
		} catch (FileNotFoundException e) {
			System.out.println("Can't find the specified file...");
			return;
		}
		if (cfg.isVocabularyEmpty()) { // If the vocabulary is empty, the
										// grammar is ambiguous in an empty way!
			System.out
			        .println("This grammar is ambiguous in an empty way (It has no terminals)!!!");
			return;
		}
		VocabularyGenerator generator = new VocabularyGenerator(
				cfg.getTerminals());
		List<String> word = generator.nextWord();
		do {
			List<List<List<String>>> leftDerivationsFound = new ArrayList<>();
			int result = -1;
			try {
				result = testWord(cfg.getTerminals(), cfg.getDerivationRules(),
						cfg.getNullableNonterminals(), word,
						leftDerivationsFound);
			} catch (StackOverflowError e) {
				System.out
				        .println("The tester threw a StackOverflowError, sorry...");
				return;
			}
			System.out.println("******** Found " + result
					+ " left derivations of the word: " + buildWord(word)
					+ " ********");
			if (result > 1) {
				System.out.println("The first left derivation sequence is:");
				printDerivationSequence(leftDerivationsFound.get(0));
				System.out.println("The second left derivation sequence is:");
				printDerivationSequence(leftDerivationsFound.get(1));
				System.out.println("This grammar is ambiguous!!!");
				return;
			} else {
				if (result == 1) {
					System.out.println("The only left derivation sequence is:");
					printDerivationSequence(leftDerivationsFound.get(0));
				}
			}
		} while ((!cfg.getTerminals().isEmpty())
				&& ((word = generator.nextWord()).size() <= maxWordLengthToCheck));
		System.out.println("This grammar is not ambiguous!!!");
		return;
	}

	/*
	 * A method that prints a left derivation sequence in a table form. The left
	 * column of the table contains the resulting string in every derivation,
	 * while the right column contains the applied derivation rule in each step.
	 */
	private static void printDerivationSequence(
			List<List<String>> derivationSequence) {
		int maxDerivedStringLength = -1;
		int maxDerivationRuleLength = -1;
		for (int i = 0; i < derivationSequence.size(); ++i) {
			if (i % 2 == 0) {
				int derivedStringLength = buildWord(derivationSequence.get(i))
						.length();
				if (derivedStringLength > maxDerivedStringLength) {
					maxDerivedStringLength = derivedStringLength;
				}
			} else {
				int derivationRuleLength = buildWord(derivationSequence.get(i))
						.length();
				if (derivationRuleLength > maxDerivationRuleLength) {
					maxDerivationRuleLength = derivationRuleLength;
				}
			}
		}
		int leftColumnVerticalSize = maxDerivedStringLength + 2;
		int rightColumnVerticalSize = maxDerivationRuleLength + 2;
		int defaultLeftColumnVerticalSize = " Derived String ".length();
		int defaultRightColumnVerticalSize = " Applied Rule ".length();
		if (leftColumnVerticalSize < defaultLeftColumnVerticalSize) {
			leftColumnVerticalSize = defaultLeftColumnVerticalSize;
		}
		if (rightColumnVerticalSize < defaultRightColumnVerticalSize) {
			rightColumnVerticalSize = defaultRightColumnVerticalSize;
		}
		System.out.println(" "
				+ new String(new char[leftColumnVerticalSize
						+ rightColumnVerticalSize + 1]).replace("\0", "_")
				+ " ");
		System.out.print("|");
		System.out.print(" Derived String "
				+ new String(new char[leftColumnVerticalSize
						- defaultLeftColumnVerticalSize]).replace("\0", " "));
		System.out.print("|");
		System.out.print(" Applied Rule "
				+ new String(new char[rightColumnVerticalSize
						- defaultRightColumnVerticalSize]).replace("\0", " "));
		System.out.print("|");
		System.out.println();
		for (int i = 0; i < derivationSequence.size(); i += 2) {
			printUnderscoreLine(leftColumnVerticalSize, rightColumnVerticalSize);
			System.out.print("|");
			String derivedString = " " + buildWord(derivationSequence.get(i))
					+ " ";
			String appliedRule;
			if (i + 1 < derivationSequence.size()) {
				appliedRule = " " + buildWord(derivationSequence.get(i + 1))
						+ " ";
			} else {
				appliedRule = " Done!!! ";
			}
			System.out.print(derivedString
					+ new String(new char[leftColumnVerticalSize
							- derivedString.length()]).replace("\0", " "));
			System.out.print("|");
			System.out.print(appliedRule
					+ new String(new char[rightColumnVerticalSize
							- appliedRule.length()]).replace("\0", " "));
			System.out.print("|");
			System.out.println();
		}
		printUnderscoreLine(leftColumnVerticalSize, rightColumnVerticalSize);
		System.out.println();
		return;
	}

	/*
	 * A method that is used in the above method to print an underscore line of
	 * the table.
	 */
	private static void printUnderscoreLine(int leftColumnVerticalSize,
			int rightColumnVerticalSize) {
		System.out.print("|");
		System.out.print(new String(new char[leftColumnVerticalSize]).replace(
				"\0", "_"));
		System.out.print("|");
		System.out.print(new String(new char[rightColumnVerticalSize]).replace(
				"\0", "_"));
		System.out.print("|");
		System.out.println();
		return;
	}

	/*
	 * A method that translates a string List that represents a word to a
	 * regular string, for printing uses.
	 */
	private static String buildWord(List<String> stringList) {
		if (stringList.isEmpty()) { // Epsilon is represented by an empty list.
			return "epsilon";
		}
		StringBuilder word = new StringBuilder();
		for (int i = 0; i < stringList.size(); ++i) {
			word.append(stringList.get(i) + " ");
		}
		return word.substring(0, word.length() - 1).toString();
	}

	/*
	 * A wrapping method to the countDerivations method. 
	 * 		terminals - Grammar's set of terminals. 
	 * 		derivationRules - Grammar's derivation rules mapping.
	 * 		nullableNonterminals - Grammar's set of nullable nonterminals.
	 * 		word - The last generated word of the grammar's vocabulary, that 
	 * 			   needs to be tested.
	 * 		leftDerivationsFound - A representation of word's left derivations
	 * 							   that were found so far. Each left derivation
	 * 							   sequence is represented by a list of string
	 *							   lists, in which every even indexed list is
	 *							   the derived string, and every odd indexed 
	 *							   list is the derivation rule that was used.
	 */
	private static int testWord(Set<String> terminals,
			Map<String, Set<List<String>>> derivationRules,
			Set<String> nullableNonterminals, List<String> word,
			List<List<List<String>>> leftDerivationsFound) {
		List<String> nonterminals = new ArrayList<>(derivationRules.keySet());
		List<String> startingNonterminal = new ArrayList<>();
		startingNonterminal.add(nonterminals.get(0));
		List<List<String>> newLeftDerivationSequence = new ArrayList<>();
		newLeftDerivationSequence.add(new ArrayList<>(startingNonterminal));
		// Every left derivations sequence starts with the starting nonterminal.
		leftDerivationsFound.add(newLeftDerivationSequence);
		return countDerivations(terminals, derivationRules,
				startingNonterminal, nullableNonterminals, word,
				leftDerivationsFound, 0);
	}

	/*
	 * A recursive method that counts the number of different left derivations
	 * of the given word. It uses a lot of "tricks" to handle as much grammar's
	 * as possible, such as tail recursion and randomness. terminals - Grammar's
	 * set of terminals. derivationRules - Grammar's derivation rules mapping.
	 * derivedString - The string, which can contain terminals and nonterminals,
	 * that was derived so far (The test always starts deriving from the
	 * starting nonterminal). nullableNonterminals - Grammar's set of nullable
	 * nonterminals. word - The last generated word of the grammar's vocabulary,
	 * that needs to be tested. leftDerivationsFound - A representation of
	 * word's left derivations that were found so far. Each left derivation
	 * sequence is represented by a list of string lists, in which every even
	 * indexed list is the derived string, and every odd indexed list is the
	 * derivation rule that was used. numDerivationsSoFar - A counter that
	 * counts the number of different left derivations that were found so far.
	 * It has a significant role in the tail recursion implementation.
	 */
	private static int countDerivations(Set<String> terminals,
			Map<String, Set<List<String>>> derivationRules,
			List<String> derivedString, Set<String> nullableNonterminals,
			List<String> word, List<List<List<String>>> leftDerivationsFound,
			int numDerivationsSoFar) {
		if (numDerivationsSoFar > 1) { // Already found more then one left
										// derivations, so no need to count more
										// for
										// the ambiguity test.
			return numDerivationsSoFar;
		}
		if (derivedString.equals(word)) { // Success!! (Found one more left
											// derivation.)
			return numDerivationsSoFar + 1;
		}
		if (word.isEmpty()
				&& (!nullableNonterminals.containsAll(derivedString))) {
			return numDerivationsSoFar; // To derive epsilon, all literals in
										// the
										// currently derived string must be
										// nullable.
		}
		if (derivedString.isEmpty()) { // Cannot derive further from epsilon.
			return numDerivationsSoFar;
		}
		int numOfTerminals = 0;
		int numOfNotNullableNonterminals = 0;
		int firstNonterminalIndex = -1;
		for (int i = 0; i < derivedString.size(); ++i) {
			String literal = derivedString.get(i);
			if (!terminals.contains(literal)) {
				if (firstNonterminalIndex < 0) {
					firstNonterminalIndex = i;
				}
				if (!nullableNonterminals.contains(literal)) {
					++numOfNotNullableNonterminals;
				}
			} else {
				++numOfTerminals;
			}
		}
		if (firstNonterminalIndex < 0) { // If derivedString consists of only
											// terminals, the derivation cannot
											// get further.
			return numDerivationsSoFar;
		}
		if ((firstNonterminalIndex > word.size())
				|| (!derivedString.subList(0, firstNonterminalIndex).equals(
						word.subList(0, firstNonterminalIndex)))
				|| (numOfTerminals + numOfNotNullableNonterminals > word.size())) {
			return numDerivationsSoFar; // If one of the three tests above
										// fails,
										// then there is no reason continuing
										// deriving the derivedString. These
										// tests are used to prevent as much
										// unnecessary derivations (derivations
										// that obviously cannot lead to the
										// tested word) as possible, in order to
										// prevent StackOverflowException.
		}
		String nonterminal = derivedString.get(firstNonterminalIndex);
		derivedString.remove(firstNonterminalIndex);
		List<String> tmp = new ArrayList<>(derivedString);
		/*
		 * Shuffling grammar's rules in a random order is also one of the tricks
		 * used to prevent StackOverflowException.
		 */
		derivationRules = shuffleNonterminalRules(derivationRules, nonterminal);
		for (List<String> ruleString : derivationRules.get(nonterminal)) {
			clearEpsilons(ruleString);
			derivedString.addAll(firstNonterminalIndex, ruleString);
			List<String> appliedRule = new ArrayList<>();
			appliedRule.add(nonterminal);
			appliedRule.add("->");
			if (ruleString.isEmpty()) {
				appliedRule.add("epsilon");
			} else {
				appliedRule.addAll(ruleString);
			}
			List<List<String>> derivationSequence = leftDerivationsFound
					.get(numDerivationsSoFar);
			// Saving old derivation sequence.
			List<List<String>> oldDerivationSequence = new ArrayList<>(
					derivationSequence);
			// Adding the applied derivation rule to the derivation sequence.
			derivationSequence.add(appliedRule);
			// Adding the new derived string to the derivation sequence.
			derivationSequence.add(new ArrayList<>(derivedString));
			// Performing a tail recursive call.
			numDerivationsSoFar = countDerivations(terminals, derivationRules,
					derivedString, nullableNonterminals, word,
					leftDerivationsFound, numDerivationsSoFar);
			if (numDerivationsSoFar > 1) { // Already found more then one left
											// derivations, so no need to count
											// more
											// for the ambiguity test.
				return numDerivationsSoFar;
			}
			/*
			 * Restoring the old derivation sequence. If numDerivationsSoFar was
			 * not changed, the old sequence will replace the current sequence.
			 * If not, then numDerivationsSoFar must have grown by 1, so the old
			 * sequence will be added as a new sequence.
			 */
			leftDerivationsFound
					.add(numDerivationsSoFar, oldDerivationSequence);
			derivedString = new ArrayList<>(tmp); // Preparing for the next
													// derivation rule.
		}
		return numDerivationsSoFar;
	}

	/*
	 * A method that shuffles grammar's derivation rules that start with the
	 * given nonterminal. This is an attempt to avoid problems with left
	 * recursion in Grammar's rules, which is one of the main reasons for
	 * StackOverflowException.
	 */
	private static Map<String, Set<List<String>>> shuffleNonterminalRules(
			Map<String, Set<List<String>>> derivationRules, String nonterminal) {
		Map<String, Set<List<String>>> newDerivationRules = deepCopyMap(derivationRules);
		List<List<String>> rules = new ArrayList<>(
				newDerivationRules.get(nonterminal));
		Collections.shuffle(rules);
		newDerivationRules.put(nonterminal, new LinkedHashSet<List<String>>(
				rules));
		return newDerivationRules;
	}

	/*
	 * A method that is used in the above method. Used to deep copy the
	 * derivation rules mapping.
	 */
	private static Map<String, Set<List<String>>> deepCopyMap(
			Map<String, Set<List<String>>> map) {
		Map<String, Set<List<String>>> newMap = new LinkedHashMap<>();
		for (String key : map.keySet()) {
			Set<List<String>> newSet = new LinkedHashSet<>();
			for (List<String> list : map.get(key)) {
				newSet.add(list);
			}
			newMap.put(key, newSet);
		}
		return newMap;
	}

	/* A method that cleans unnecessary epsilons in the derived string. */
	private static void clearEpsilons(List<String> stringList) {
		boolean containedEpsilon = true;
		while (containedEpsilon) {
			containedEpsilon = stringList.remove("epsilon");
		}
		return;
	}

}
