package edu.ncsu.csc.ase.dristi.configuration;

public class Config 
{
	// BEGIN Logging related configuration variables//
	/**
	 * Variable to control verbosity of parser 
	 * The six logging levels used by Log are (in order):
	 * 		TRACE (the least serious)
	 * 		DEBUG
	 * 		INFO
	 * 		WARN
	 * 		ERROR
	 * 		FATAL (the most serious)
	 */
	public static final String DEFAULT_LOG_LEVEL_KEY = "WARN";
	// END Logging related configuration variables//
	
	// BEGIN WordNet configuration//
	/**
	 * Path to folder containing WordNet Files
	 */
	public static final String WORD_NET_PATH = "C:\\rpandita\\WordNet-3.0";
	
	/**
	 * Path to WordNet Configuration File
	 */
	public static final String WORD_NET_CONFIG_XML_PATH = "wordnet.xml";
	
	/**
	 * Minimum Threshold for WordNet similarity 
	 */
	public static final double WORD_NET_MIN_THRESHOLD = 0.9; 
	
	// END WordNet configuration//
	
	// BEGIN WEB Crawling configuration//
	
}
