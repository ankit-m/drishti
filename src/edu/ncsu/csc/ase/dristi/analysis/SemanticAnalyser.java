 package edu.ncsu.csc.ase.dristi.analysis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import edu.ncsu.csc.ase.dristi.NLPParser;
import edu.ncsu.csc.ase.dristi.TextAnalysisEngine;
import edu.ncsu.csc.ase.dristi.datastructure.ITuple;
import edu.ncsu.csc.ase.dristi.heuristics.semantic.PhraseReduction;
import edu.ncsu.csc.ase.dristi.heuristics.semantic.SentenceCompletion;
import edu.ncsu.csc.ase.dristi.heuristics.semantic.TokenReduction;
import edu.ncsu.csc.ase.dristi.heuristics.syntnax.POSSentenceSplitting;
import edu.ncsu.csc.ase.dristi.logging.MyLoggerFactory;
import edu.ncsu.csc.ase.dristi.util.ConsoleUtil;
import edu.ncsu.csc.ase.dristi.util.FileUtilExcel;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;

/**
 * This Class contains logic to perform Semantic Analysis of REST API Text
 * @author rahul_pandita
 *
 */
public class SemanticAnalyser 
{
	private static Logger logger = MyLoggerFactory.getLogger(SemanticAnalyser.class);
	
	private PhraseReduction phraseRed;
	
	private TokenReduction tokenRed;
	
	private SentenceCompletion sentenceCompletion;
	
	private POSSentenceSplitting sentenceSplitting;
	
	private NLPParser parser;
	
	public SemanticAnalyser()
	{
		phraseRed = PhraseReduction.getInstance();
		tokenRed = TokenReduction.getInstance();
		sentenceCompletion = SentenceCompletion.getInstance();
		sentenceSplitting = POSSentenceSplitting.getInstance();
		parser = NLPParser.getInstance();
	}
	
	public boolean isIntentSentnce(Tree tree)
	{
		return false;
	}
	
	public List<String> applyReduction(String text)
	{
		logger.debug("begin applying reduction.");
		text = tokenRed.applyReduction(text);
		text = phraseRed.applyReduction(text);
		text = sentenceCompletion.applyReduction(text);
		List<String> sentenceList = sentenceSplitting.applyReduction(text);
		return sentenceList;
	}
	
	public List<String> analyse(String text)
	{
		logger.debug(text);
		List<String> senList = applyReduction(text);
		
		
		
		List<SemanticGraph> semGraphList = new ArrayList<SemanticGraph>();
		
		for(String sen: senList)
		{
			System.out.println(sen);
			semGraphList.addAll(parser.getStanfordDependencies(sen));
		}
		
		List<String> conceptList = new ArrayList<String>();
		for(SemanticGraph semGraph : semGraphList)
		{
			try{
				ITuple t = TextAnalysisEngine.parse(semGraph);
				System.out.println(t.toString());
		//		conceptList.addAll(getConcept(t));
			}
			catch(Exception e)
			{
				logger.error("error parsing sentnce \n" + semGraph , e);
			}
		}
		return conceptList;	
	}

	public void store(List<String> conceptList) {
		try {
			for(String str:conceptList)
				FileUtilExcel.getInstance().writeDataToExcel(str,"");
			
			ConsoleUtil.readConsole("Wrote File. Press Enter to save.");
			
			
			FileUtilExcel.getInstance().commitXlS();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public List<String> getConcept(ITuple tuple) {
		List<String> tupleList = new ArrayList<String>();
		if (tuple == null)
			return tupleList;
		else if (tuple.isTerminal()) {
			tupleList.add(tuple.getEntity().getName());
		} else {
			tupleList.addAll(getConcept(tuple.getLeft()));
			tupleList.addAll(getConcept(tuple.getRight()));
		}
		return tupleList;
	}
	
	public static void main(String[] args) {
		SemanticAnalyser sa = new SemanticAnalyser();
		String s = ConsoleUtil.readConsole("Enter sentnce:");
		while (s.length()>0)
		{
			sa.analyse(s);
			s = ConsoleUtil.readConsole("Enter sentnce:");
		}
		
	}
	
}
