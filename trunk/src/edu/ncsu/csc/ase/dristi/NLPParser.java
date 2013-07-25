package edu.ncsu.csc.ase.dristi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.ncsu.csc.ase.dristi.datastructure.ITuple;
import edu.ncsu.csc.ase.dristi.shallowparser.ParserFactory;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * This class contains methods for accessing Stanford NLP Library
 * @author rahul_pandita
 *
 */
public class NLPParser 
{
	private static NLPParser instance;
	
	private static StanfordCoreNLP pipeline;
	
	
	public static synchronized NLPParser getInstance()
	{
		if(instance == null)
			instance = new NLPParser();
		return instance;
	}
	
	/**
	 * Creates the instance of Stanford Parser
	 */
	private NLPParser()
	{
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	    pipeline = new StanfordCoreNLP(props);
	}
	
	public List<CoreMap> getSentenceList(String text)
	{
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		return sentences;
	}
	
	/**
	 * Returns the List of POS trees representation of text, one for each sentence.
	 * Uses Stanford's standard sentence boundary detection.
	 * 
	 * @param text: the text whose POS Trees representation needs to be created
	 * @return ordered list of POS Trees one, for each sentence in the text.
	 */
	public List<Tree> getPOSTree(String text)
	{
		List<Tree> posTreeList = new ArrayList<Tree>(); 
		Annotation document = new Annotation(text);
		
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences)
		{
			posTreeList.add(sentence.get(TreeAnnotation.class));
		}
		return posTreeList;
	}
	
	/**
	 * Returns the Stanford-typed Dependencies representation of text, one for each sentence.
	 * Uses Stanford's standard sentence boundary detection.
	 * 
	 * @param text: the text whose Stanford dependency representation needs to be created
	 * @return ordered list of Stanford dependency representation, one for each sentence in the text.
	 */
	public List<SemanticGraph> getStanfordDependencies(String text)
	{
		List<SemanticGraph> semanticDepList = new ArrayList<SemanticGraph>(); 
		Annotation document = new Annotation(text);
		
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences)
		{
			semanticDepList.add(sentence.get(CollapsedCCProcessedDependenciesAnnotation.class));
		}
		return semanticDepList;
	}
	
	/**
	 * Returns the Tuple representation of text, one for each sentence.
	 * Uses Stanford's standard sentence boundary detection.
	 * 
	 * @param text: the text whose Tuple representation needs to be created
	 * @return ordered list of Tuple representation, one for each sentence in the text.
	 */
	public List<ITuple> getTuples(String text)
	{
		List<ITuple> tupleList = new ArrayList<ITuple>(); 
		Annotation document = new Annotation(text);
		
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences)
		{
			tupleList.add(parse(sentence.get(CollapsedCCProcessedDependenciesAnnotation.class)));
		}
		return tupleList;
	}
	
	/**
	 * For exploration yet to determine how to use 
	 * @param text
	 * @return
	 */
	public List<SemanticGraph> getNamedEntities(String text)
	{
		List<SemanticGraph> semanticDepList = new ArrayList<SemanticGraph>(); 
		Annotation document = new Annotation(text);
		
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences)
		{
			// traversing the words in the current sentence
		    // a CoreLabel is a CoreMap with additional token-specific methods
		    for (CoreLabel token: sentence.get(TokensAnnotation.class)) 
		    {
		        // this is the text of the token
		        String word = token.get(TextAnnotation.class);
		        // this is the POS tag of the token
		        String pos = token.get(PartOfSpeechAnnotation.class);
		        // this is the NER label of the token
		        String ne = token.get(NamedEntityTagAnnotation.class); 
		        System.out.println(token.word() + " \t:\t " + word + " \t:\t " + pos + " \t:\t " + ne);
		    }
		    
		    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			semanticDepList.add(sentence.get(CollapsedCCProcessedDependenciesAnnotation.class));
		}
		return semanticDepList;
	}
	
	/**
	 * Parses the Stanford-typed Dependencies of a sentence and returns a Tuple representation
	 * 
	 * @param dependency: Stanford Dependency representation of a sentence
	 * @return Tuple Representation of the sentence represented by dependency 
	 */
	public ITuple parse(SemanticGraph dependency) {
		Set<IndexedWord> visited = new HashSet<IndexedWord>();
		if(dependency.vertexSet().size()!=0)
		{
			if(dependency.getRoots().size()>0)
				return ParserFactory.getInstance().getParser("root").parse(dependency.getFirstRoot(),dependency,visited);
		} 
		return null;
	}
	
	/**
	 * Parses the Stanford-typed Dependencies of a sentence and returns Co-Reference Chains
	 * @param text : the text whose Co-Reference Chains needs to be created
	 * @return List of CorefChain of the text
	 */
	public List<CorefChain> getCoRefDependencies(String text)
	{
		List<CorefChain> decorefDepList = new ArrayList<CorefChain>(); 
		Annotation document = new Annotation(text);
		
		pipeline.annotate(document);
		Map<Integer, CorefChain> decorefMap = document.get(CorefChainAnnotation.class);
		for(Integer mapIter: decorefMap.keySet())
		{
			decorefDepList.add(decorefMap.get(mapIter));
		}
		return decorefDepList;
	}
	
	/**
	 * Main method for testing to be removed in production or create a unit test
	 * @param args
	 */
	public static void main(String[] args) {
		String text = "This implementation of the DELETE operation deletes the bucket named in the URI. All objects (including all object versions and Delete Markers) in the bucket must be deleted before the bucket itself can be deleted.";
		
		NLPParser parser = NLPParser.getInstance();
		
		//List<Tree> posTreeList = parser.getPOSTree(text);
		//for(Tree postree: posTreeList)
		//	postree.pennPrint();
		List<CorefChain> decorefDepList = parser.getCoRefDependencies(text);
		for(CorefChain c: decorefDepList)
		{
			System.out.println(c.getRepresentativeMention());
			System.out.println();
			for(CorefMention cm : c.getCorefMentions())
			{
				System.out.println(cm);
				System.out.println(cm.corefClusterID);
				
			}
		}
	}
}
