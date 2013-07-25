package edu.ncsu.csc.ase.dristi.util;

/**
 * Class containing common String utility routines
 * @author rahul_pandita
 *
 */
public class StringUtil 
{
	/**
	 * Utility function to remove spaces
	 * @param str the String in which spaces are to be removed
	 * @return String with all spaces stripped
	 */
	public static String removeSpaces(String str)
	{
		return str.replaceAll("\\s","");
	}
}
