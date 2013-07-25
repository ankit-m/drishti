package edu.ncsu.csc.ase.dristi.heuristics.semantic;



/**
 * This Class holds the logic to deal with incomplete sentences
 * 
 * @author rahul_pandita
 * 
 */
public class SentenceCompletion {
	
	private static SentenceCompletion instance;

	/**
	 * Private Constructor for Singleton Pattern
	 */
	private SentenceCompletion() {
		populatePatternMap();
	}

	/**
	 * Method to access the singleton object
	 * 
	 * @return Singleton SentenceCompletion object
	 */
	public static synchronized SentenceCompletion getInstance() {
		if (instance == null)
			instance = new SentenceCompletion();
		return instance;
	}

	/**
	 * Populates the patternMap
	 */
	private void populatePatternMap() {
		//TODO Determine if a generic Pattern can be constructed
	}

	public String applyReduction(String sentence) {

		if(sentence.startsWith("Returns"))
			sentence = sentence.replace("Returns", "This implemenatation returns");
		else if(sentence.startsWith("Sets"))
			sentence = sentence.replace("Sets", "This implemenatation sets");
		else if(sentence.startsWith("Creates"))
			sentence = sentence.replace("Creates", "This implemenatation creates");
		else if(sentence.startsWith("Restores"))
			sentence = sentence.replace("Restores", "This implemenatation restores");
		else if(sentence.startsWith("Uploads"))
			sentence = sentence.replace("Uploads", "This implemenatation uploads");
		return sentence;
	}
}
