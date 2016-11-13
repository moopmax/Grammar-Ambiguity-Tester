import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/* CFG - A class that represents a context-free grammar. */
class CFG {

	/*
	 * terminals - A string set the contains the grammar's terminals, which are
	 * represented by strings
	 */
	private Set<String> terminals = new LinkedHashSet<>();

	/*
	 * derivationRules - A map that represents the grammar's derivation rules.
	 * It maps every nonterminal, which is a string, to a set of rules, each
	 * represented by a list of literals (terminals or nonterminals).
	 */
	private Map<String, Set<List<String>>> derivationRules = new LinkedHashMap<>();

	/*
	 * isVocabularyEmpty - A boolean that states whether the grammar's
	 * vocabulary is empty or not. The grammar's vocabulary is the Kleene star
	 * of it's terminals set.
	 */
	private boolean isVocabularyEmpty = false;

	/*
	 * nullableNonterminals - A set of all grammar's nullable nonterminals. A
	 * nonterminal is called nullable if the word epsilon can be derived from
	 * it, using the grammar's derivation rules.
	 */
	private Set<String> nullableNonterminals = new LinkedHashSet<>();

	/*
	 * The CFG class' constructor. 
	 * 		file - A file that contains the grammar's representation.
	 */
	CFG(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			String[] ruleArr = scanner.nextLine().split("\\s+->\\s+");
			Set<List<String>> ruleSet = new LinkedHashSet<>();
			for (String str : ruleArr[1].split("\\s+\\|\\s+")) {
				List<String> strList = new ArrayList<>(Arrays.asList(str
						.split("\\s+")));
				terminals.addAll(strList); // First, add all grammar's literals
											// to the terminals set.
				ruleSet.add(strList);
			}
			String nonterminal = ruleArr[0];
			if (derivationRules.containsKey(nonterminal)) { // If
															// derivationRules
															// already contains
															// a derivation rule
															// for nonterminal.
				Set<List<String>> oldRuleSet = derivationRules.get(nonterminal);
				oldRuleSet.addAll(ruleSet);
				derivationRules.put(nonterminal, oldRuleSet);
			} else {
				derivationRules.put(nonterminal, ruleSet);
			}
		}
		terminals.removeAll(derivationRules.keySet()); // The grammar's
														// nonterminals are
														// exactly the keys of
														// the derivationRules
														// map, so they should
														// be removed from
														// terminals set.
		boolean grammarContainsEpsilon = terminals.remove("epsilon"); // Epsilon
																		// is
																		// not a
																		// terminal...
		if (terminals.isEmpty() && !grammarContainsEpsilon) { // If the grammar
																// does not
																// contain any
																// terminals,
																// and the
																// epsilon
																// literal, it's
																// vocabulary is
																// empty.
			isVocabularyEmpty = true;
		}
		computeNullables();
		scanner.close();
	}

	/*
	 * A method that computes the grammar's nullable nonterminals, and adds them
	 * to the nullableNonterminals field.
	 */
	private void computeNullables() {
		List<String> epsilon = new ArrayList<>();
		epsilon.add("epsilon");
		for (String nonterminal : derivationRules.keySet()) {
			if (derivationRules.get(nonterminal).contains(epsilon)) {
				nullableNonterminals.add(nonterminal);
			}
		}
		boolean updatedNullables;
		do {
			updatedNullables = false;
			for (String nonterminal : derivationRules.keySet()) {
				for (List<String> rule : derivationRules.get(nonterminal)) {
					if (nullableNonterminals.containsAll(rule)) {
						if (nullableNonterminals.add(nonterminal)) {
							updatedNullables = true;
						}
					}
				}
			}
		} while (updatedNullables);
	}

	/* A getter for the terminals field. */
	public Set<String> getTerminals() {
		return terminals;
	}

	/* A getter for the derivationRules field. */
	public Map<String, Set<List<String>>> getDerivationRules() {
		return derivationRules;
	}

	/* A getter for the isVocabularyEmpty field. */
	public boolean isVocabularyEmpty() {
		return isVocabularyEmpty;
	}

	/* A getter for the nullableNonterminals field. */
	public Set<String> getNullableNonterminals() {
		return nullableNonterminals;
	}

}
