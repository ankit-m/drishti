package edu.ncsu.csc.ase.dristi;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import edu.ncsu.csc.ase.dristi.datastructure.ITuple;
import edu.ncsu.csc.ase.dristi.datastructure.Tuple;
import edu.ncsu.csc.ase.dristi.heuristics.semantic.WordNetSimilarity;
import edu.ncsu.csc.ase.dristi.knowledge.Knowledge;
import edu.ncsu.csc.ase.dristi.knowledge.KnowledgeAtom;
import edu.ncsu.csc.ase.dristi.logging.MyLoggerFactory;
import edu.ncsu.csc.ase.dristi.util.ConsoleUtil;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;

public class Whyper {

	public enum Permission {

		Read_Contact, Read_Calendar, Record_Audio
	}

	private Logger logger = MyLoggerFactory.getLogger(Whyper.class);
	
	// Change the assignment to refer to different permission
	// Current permissions supported are Read_Contacts, Read_Calendar,
	// Record_Audio
	private Permission permission = Permission.Read_Contact;

	public static void main(String[] args) {
		Whyper instance = new Whyper();
		try {
			String message = "Enter The Sentnce =>";
			String s = ConsoleUtil.readConsole(message);
			while (s.length() > 0) {

				if (instance.isPermissionSentnce(s)) {
					System.err.println("Describes Permission!");
				} else {
					System.err.println("Does Not Describe Permission!");
				}
				s = ConsoleUtil.readConsole(message);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isPermissionSentnce(String sentnce) {

		// Only the first Sentence is considered so provide input sentence by
		// sentence
		SemanticGraph semdep = NLPParser.getInstance()
				.getStanfordDependencies(sentnce).get(0);
		Tuple t = TextAnalysisEngine.parse(semdep);
		return isDescribingPermission(t);
	}

	private boolean isDescribingPermission(Tuple t) {
		if (t == null)
			return false;
		Knowledge k = Knowledge.getInstance();
		KnowledgeAtom a;
		List<ITuple> tList;
		switch (permission) {
		case Read_Contact:
			a = k.fetchAtom("contact");
			tList = searchAtom(t, a.getSynonyms(),new ArrayList<ITuple>());
			for (ITuple t1 : tList) {
				if ((t1 != null)
						&& (isUserOwned(t, t1) || isAction(a.getActions(), t, t1))
						|| isAugumentedAction(a.getActions(), t, t1)) {
					return true;
				}
			}

			a = k.fetchAtom("sms");
			tList = searchAtom(t, a.getSynonyms(), new ArrayList<ITuple>());
			for (ITuple t1 : tList) {
				if ((t1 != null)
						&& (isUserOwned(t, t1) || isAction(a.getActions(), t, t1))
						|| isAction(a.getActions(), t, t1))
					return true;
			}
			
			a = k.fetchAtom("email");
			tList = searchAtom(t, a.getSynonyms(), new ArrayList<ITuple>());
			for (ITuple t1 : tList) {
				if ((t1 != null)
						&& (isUserOwned(t, t1) || isAction(a.getActions(), t, t1))
						|| isAction(a.getActions(), t, t1))
					return true;
			}
			break;

		case Read_Calendar: {

			a = k.fetchAtom("calendar");
			tList = searchAtom(t, a.getSynonyms(), new ArrayList<ITuple>());
			for (ITuple t1 : tList) {
				if ((t1 != null)
						&& (isUserOwned(t, t1) || isAction(a.getActions(), t,
								t1))
						|| isAugumentedAction(a.getActions(), t, t1)) {
					return true;
				}
			}

			a = k.fetchAtom("event");
			tList = searchAtom(t, a.getSynonyms(), new ArrayList<ITuple>());
			for (ITuple t1 : tList) {
				if ((t1 != null)
						&& (isUserOwned(t, t1) || isAction(a.getActions(), t,
								t1)) || isAction(a.getActions(), t, t1))
					return true;
			}
		}
			break;

		case Record_Audio:
			a = k.fetchAtom("mic");
			tList = searchAtom(t, a.getSynonyms(), new ArrayList<ITuple>());
			for (ITuple t1 : tList) {
				if ((t1 != null)
						&& (isUserOwned(t, t1) || isAction(a.getActions(), t,
								t1))
						|| isAugumentedAction(a.getActions(), t, t1)) {
					return true;
				}
			}

			a = k.fetchAtom("audio");
			tList = searchAtom(t, a.getSynonyms(), new ArrayList<ITuple>());
			for (ITuple t1 : tList) {
				if ((t1 != null)
						&& (isUserOwned(t, t1) || isAction(a.getActions(), t,
								t1)) || isAction(a.getActions(), t, t1))
					return true;
			}
			break;

		default:
			logger.error("Unknown Permission!");
			break;
		}

		return false;
	}

	private static boolean isAugumentedAction(ArrayList<String> actions,
			Tuple t, ITuple t1) {
		String s = "";
		if (t1.isTerminal())
			s = t1.getEntity().getName();
		else
			s = t1.getRelation().getName();
		for (String action : actions) {
			if (s.contains(action)) {
				return true;
			}

		}

		return false;
	}

	private static boolean isUserOwned(Tuple t1, ITuple t2) {
		ITuple t_parent = t1.findParent(t2);
		ITuple t_Sibling = t1.findSibling(t2);
		String comp_Value = "";
		if ((t_parent != null) && !t_parent.same(t2)) {
			comp_Value = t_parent.getRelation().getName();

			try {
				if (WordNetSimilarity.getInstance()
						.isSimilar(comp_Value, "and")
						|| WordNetSimilarity.getInstance().isSimilar(
								comp_Value, "or")
						|| WordNetSimilarity.getInstance().isSimilar(
								comp_Value, "in")) {
					return isUserOwned(t1, t_parent);
				}

				else if (WordNetSimilarity.getInstance().isSimilar(comp_Value,
						"owned")
						|| WordNetSimilarity.getInstance().isSimilar(
								comp_Value, "is")) {
					if (t_Sibling.isTerminal()) {
						if (WordNetSimilarity.getInstance().isSimilar(
								t_Sibling.getEntity().getName(), "user"))
							return true;
						else if (WordNetSimilarity.getInstance().isSimilar(
								t_Sibling.getEntity().getName(), "you"))
							return true;
						else if (WordNetSimilarity.getInstance().isSimilar(
								t_Sibling.getEntity().getName(), "their"))
							return true;
						else if (WordNetSimilarity.getInstance().isSimilar(
								t_Sibling.getEntity().getName(), "her"))
							return true;
						else if (WordNetSimilarity.getInstance().isSimilar(
								t_Sibling.getEntity().getName(), "his"))
							return true;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	private static boolean isAction(ArrayList<String> actions, ITuple t1,
			ITuple t2) {
		ITuple t_parent = t1.findParent(t2);
		ITuple t_Sibling = t1.findSibling(t2);
		String comp_Value = "";
		if ((t_parent != null) && !t_parent.same(t2)) {
			comp_Value = t_parent.getRelation().getName();
			for (String action : actions) {
				try {
					if (WordNetSimilarity.getInstance().isSimilar(comp_Value,
							action)) {
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (t_Sibling != null) {
				if (t_Sibling.isTerminal())
					comp_Value = t_Sibling.toString();
				else
					comp_Value = t_Sibling.getRelation().getName();
				for (String action : actions) {
					try {
						if (WordNetSimilarity.getInstance().isSimilar(
								comp_Value, action)) {
							return true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			return isAction(actions, t1, t_parent);
		} else if (t_parent.same(t2)) {
			if (t_parent.isTerminal())
				comp_Value = t_parent.toString();
			else
				comp_Value = t_parent.getRelation().getName();
			for (String action : actions) {
				try {
					if (WordNetSimilarity.getInstance().isSimilar(comp_Value,
							action)) {
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static ArrayList<ITuple> searchAtom(ITuple t,
			ArrayList<String> atom, ArrayList<ITuple> tupleList) {

		if (t == null)
			return tupleList;
		if (t.isTerminal()) {
			for (String res : atom) {
				if (t.getEntity().getName().toString().toLowerCase()
						.contains(res)) {
					if (!tupleList.contains(t))
						tupleList.add(t);

				}
			}

		} else {

			for (String res : atom) {
				if (t.getRelation().getName().toLowerCase().contains(res)) {
					if (!tupleList.contains(t))
						tupleList.add(t);
				}

			}

			tupleList = searchAtom(t.getLeft(), atom, tupleList);

			tupleList = searchAtom(t.getRight(), atom, tupleList);
		}
		return tupleList;
	}
}
