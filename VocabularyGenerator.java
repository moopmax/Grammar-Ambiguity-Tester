import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/* A class that is used to generate all words of the grammar's vocabulary, in a
 * lexicographical order. */
class VocabularyGenerator {

	/*
	 * tokens - A string list that contains all the grammar's tokens
	 * (terminals).
	 */
	private List<String> tokens = new ArrayList<>();

	/* numOfTokens - The number of grammar's tokens. */
	private int numOfTokens;

	/* currentWordLength - The length of the last word that was generated. */
	private int currentWordLength;

	/*
	 * currentWord - The last word that was generated. It is represented by a
	 * list of tokens
	 */
	private List<String> currentWord;

	/*
	 * The VocabularyGenerator class' constructor. tokenSet - A set of all
	 * grammar's tokens (terminals).
	 */
	VocabularyGenerator(Set<String> tokenSet) {
		tokens.addAll(tokenSet);
		numOfTokens = tokens.size();
		currentWordLength = -1;
	}

	/*
	 * A method that generates the grammar vocabulary's next word, using the
	 * currentWord field.
	 */
	List<String> nextWord() {
		if (currentWordLength < 0) { // No words were generated yet, so the next
										// word is epsilon (represented by an
										// empty string list).
			currentWord = new ArrayList<>();
			currentWordLength = 0;
			return currentWord;
		}
		if (currentWordLength == 0) { // The last word that was generated is
										// epsilon, so the next word is the
										// first token in tokens set.
			currentWord.add(tokens.get(0));
			currentWordLength = 1;
			return currentWord;
		}
		int maxTokenMinIndex = currentWordLength; // Indicates the last token
													// from right of
													// currentWord, such that
													// all the tokens right to
													// it are the maximal
													// tokens.
		for (; (maxTokenMinIndex > 0)
				&& (currentWord.get(maxTokenMinIndex - 1).equals(tokens
						.get(numOfTokens - 1))); --maxTokenMinIndex) {
		}
		for (int i = currentWordLength - 1; i >= maxTokenMinIndex; --i) {
			currentWord.set(i, tokens.get(0));
		}
		if (maxTokenMinIndex == 0) { // If currentWord consists of only the
										// maximal token, the next word should
										// be one token longer than currentWord.
			currentWord.add(0, tokens.get(0));
			++currentWordLength;
		} else {
			currentWord.set(maxTokenMinIndex - 1, tokens.get(tokens
					.indexOf(currentWord.get(maxTokenMinIndex - 1)) + 1));
		}
		return currentWord;
	}

}
