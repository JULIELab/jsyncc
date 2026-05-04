package de.julielab.jsyncc.readbooks.clindoc;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * @author Christina Lohr
 *
 * The transformation into HTML code is not ready!
 * Only tables, new lines are transformed into HTML. 
 *
 */

public class ReadSingleMSDoc {

	public static String INPUT_FILE = "";
	
	public static String CONTENT_HTML				= "";
	public static String CONTENT_NORMAL				= "";
	public static String CONTENT_TAB_MARKED			= "";
	
	public static String LAB_PARAMS_HTML			= "";
	public static String LAB_PARAMS_NORMAL			= "";
	public static String LAB_PARAMS_TAB_MARKED		= "";
	
	// WOLP... without lab params
	public static String CONTENT_WOLP_HTML			= "";
	public static String CONTENT_WOLP_NORMAL		= "";
	public static String CONTENT_WOLP_TAB_MARKED	= "";
	
	public static boolean LAB_TABLE = false;
	public static boolean PARAGRAPH = false;
	public static boolean REMOVE_WITHE_SPACE_LINE_END = false;
	
	public static void main(String[] args) {

		//INPUT_FILE = "C:/Users/C01638/Arztbrief/10712391/130045486/2013-03-08-ENT.doc";
		
		INPUT_FILE = "grascco_test-data/test-dok_1.doc";
		doc2Text();
		
//		System.out.println(CONTENT_HTML);
//		System.out.println(CONTENT_WOLP_NORMAL);
//		System.out.println(LAB_PARAMS_NORMAL);
		System.out.println(CONTENT_NORMAL);
		
	}

	public static void doc2Text() {
		
		CONTENT_HTML			= "";
		CONTENT_NORMAL			= "";
		CONTENT_TAB_MARKED		= "";
		
		LAB_PARAMS_HTML			= "";
		LAB_PARAMS_NORMAL		= "";
		LAB_PARAMS_TAB_MARKED	= "";
		
		// WOLP... without lab params
		CONTENT_WOLP_HTML		= "";
		CONTENT_WOLP_NORMAL		= "";
		CONTENT_WOLP_TAB_MARKED	= "";
		
		try {
			InputStream fis = new FileInputStream(INPUT_FILE);
			POIFSFileSystem fs = new POIFSFileSystem(fis);

			@SuppressWarnings("resource")
			HWPFDocument doc = new HWPFDocument(fs);

			Range docRange = doc.getRange();
			
			boolean inTable = false;
			boolean inRow = false;

			boolean tbOpen = false;
			boolean tdOpen = false;
			
			for (int i = 0; i < docRange.numParagraphs(); i++)
			{
				Paragraph par = docRange.getParagraph(i);

				if (par.isInTable())
				{
					if (!inTable)
					{
						CONTENT_HTML += "\n<table>\n";
						inTable = true;
					}
					if (!inRow)
					{
						if (tbOpen == true)
						{
							CONTENT_HTML += "</tr>\n";
						}
						
						CONTENT_HTML += "<tr>";
						inRow = true;
						tbOpen = true;
					}
					if (par.isTableRowEnd())
					{
						inRow = false;
					}
					else
					{
						if ( (!tdOpen) && (par.text().endsWith("\u0007")))
						{
							CONTENT_HTML += "<td>";
							CONTENT_HTML += par.text().replaceAll("\\s", " " );
							CONTENT_HTML += "</td>";
						}
						else if ( (!tdOpen) && (!(par.text().endsWith("\u0007"))))
						{
							CONTENT_HTML += "<td>";
							CONTENT_HTML += par.text().replaceAll("\\s", " " );
							tdOpen = true;
						}
						
						else if (tdOpen && (par.text().endsWith("\u0007")))
						{
							CONTENT_HTML += par.text().replaceAll("\\s", " " );
							CONTENT_HTML += "</td>";
							tdOpen = false;
						}
						else if (tdOpen && (!(par.text().endsWith("\u0007"))))
						{
							CONTENT_HTML += par.text().replaceAll("\\s", " " );
						}
					}
				}
				else
				{
					if (inTable)
					{
						CONTENT_HTML += "</tr>\n</table>\n";
						inTable = false;
					}
					CONTENT_HTML += par.text() + "<br/>";
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception: " + e);
		}
		
		CONTENT_HTML = characterChecker(CONTENT_HTML);
		
//		CONTENT_HTML = CONTENT_HTML.replaceAll("\\s+\\n", "\\n");
		
		CONTENT_NORMAL = makeNormalText(CONTENT_HTML);
		CONTENT_TAB_MARKED = makeTabMarked(CONTENT_HTML);
		
		// TODO hier schauen! -- Umbau der Laborparameter
		
//		CONTENT_TEXT_ONLY = characterChecker(CONTENT_TEXT_ONLY);

		String[] contentSplitMT = characterChecker(CONTENT_HTML).split("\n");
//		String[] contentSplitNMT = characterChecker(CONTENT_TEXT_ONLY).split("\n");
		
		boolean foundLabParams = false;

		for (int i = 0; i < contentSplitMT.length; i++)
		{
				// TODO mehr Abfragekriterien hin!
				if (
						(
							(contentSplitMT[i].contains("<br/>Laborwerte:"))
						||
							(contentSplitMT[i].contains("<br/>Laborparameter"))
						)
					
					&&
						(
							(contentSplitMT[i].matches(".*siehe An(hang|lage).*") == false)
						&&
							(contentSplitMT[i+1].matches(".*siehe An(hang|lage).*") == false)
						)
					
					)
					
					
				{
					for (int j = i; j < contentSplitMT.length; j++)
					{
						if (
								(contentSplitMT[j].contains("Normwert"))
							||
								(contentSplitMT[j].contains("Normalbereich"))
							)
						{
							foundLabParams = true;
							break;
						}
					}
				}

			if (!foundLabParams) {
				CONTENT_WOLP_HTML = CONTENT_WOLP_HTML + contentSplitMT[i] + "\n";
			} else {
				LAB_PARAMS_HTML		= LAB_PARAMS_HTML + contentSplitMT[i] + "\n";
			}
		}
		
		CONTENT_WOLP_NORMAL = makeNormalText(CONTENT_WOLP_HTML);
		CONTENT_WOLP_TAB_MARKED = makeTabMarked(CONTENT_WOLP_HTML);
		
		LAB_PARAMS_NORMAL = makeNormalText(LAB_PARAMS_HTML);
		LAB_PARAMS_TAB_MARKED = makeTabMarked(LAB_PARAMS_HTML);
		
		if (
				(LAB_PARAMS_HTML.contains("<table>"))
			&&
				(LAB_PARAMS_HTML.contains("</table>"))
			&&
				(LAB_PARAMS_HTML.contains("<tr>"))
			&&
				(LAB_PARAMS_HTML.contains("</tr>"))
			)
		{
			LAB_TABLE = true;
		}
		else
		{
			LAB_TABLE = false;
		}
	}
	
	
	/**
	 * The Reader doesn't use all characters.
	 * 
	 * Private Use Area from U+E000 until U+F8FF
	 * https://unicode-table.com/en/blocks/private-use-area/
	 * 
	 * Basic Latin from 0020 until 007F, 007F is DEL-character, not used, used
	 * characters into 0020-007E
	 * https://unicode-table.com/en/blocks/basic-latin/
	 * 
	 * Latin-1 Supplement from 0080 until 00FF used characters into 00A1-00FF
	 * https://unicode-table.com/en/blocks/latin-1-supplement/
	 * 
	 */

	private static String characterChecker(String content) {
		String output = "";

		for (int i = 0; i < content.length(); i++) {
			int charvalue = ReadIntegerValueOf1Character(content.charAt(i));

			if ((!((57343 <= charvalue) && (charvalue <= 63742)))
					&& (((3 <= charvalue) && (charvalue <= 125)) || ((160 <= charvalue) && (charvalue <= 254)))) {
				output = output + content.charAt(i);
			}
		}

		output = output.replaceAll("", "");

		output = output.replaceAll("\u0007", "");
		output = output.replaceAll("\u0008", "");
		output = output.replaceAll("\u000B", "");
		output = output.replaceAll("\u000C", "");
		output = output.replaceAll("\\r", "\n");

		return output;
	}

	private static int ReadIntegerValueOf1Character(char c) {
		String y = c + Character.digit(c, 10) + " ";
		y = y.substring(0, y.length() - 1);
		return Integer.parseInt(y);
	}
	
	private static String makeNormalText(String input)
	{
		String text = input;
		text = text.replaceAll("<table>",	"");
		text = text.replaceAll("</table>",	"");
		text = text.replaceAll("<tr>",		"");
		text = text.replaceAll("</tr>",		"\n");
		text = text.replaceAll("<td>",		"");
		text = text.replaceAll("</td>",		"\t");
		text = text.replaceAll("<br/>",		"");
		
		if (PARAGRAPH == false)
		{
			text = text.replaceAll("\n+",	"\n");
		}
		
		if (REMOVE_WITHE_SPACE_LINE_END == true)
		{
//			text = text.replaceAll("\\n\\s+\\n", "EMPTYLINE");
//			text = text.replaceAll("\\s+\\n", "\n");
//			text = text.replaceAll("EMPTYLINE", "\n\n");
		}
		
		if (text.startsWith("\n"))
		{
			text = text.replaceFirst("\n", "");
		}
		
		return text;
	}
	
	private static String makeTabMarked(String input)
	{
		String text = input;
		text = text.replaceAll("<table>",	"");
		text = text.replaceAll("</table>",	"");
		text = text.replaceAll("<tr>",		"");
		text = text.replaceAll("</tr>",		"");
		text = text.replaceAll("<td>",		"|    ");
		text = text.replaceAll("</td>",		"    |");
		text = text.replaceAll("<br/>",		"");
		
		if (REMOVE_WITHE_SPACE_LINE_END == true)
		{
			text = text.replaceAll("\\n( |\\t)+\\n", "EMPTYLINE");
			text = text.replaceAll("( |\\t)+\\n", "\n");
			text = text.replaceAll("EMPTYLINE", "\n\n");
		}
		
//		text = text.replaceAll("(\n)+",	"\n");
		
		if (PARAGRAPH == false)
		{
			text = text.replaceAll("\n+",	"");
		}
		
		if (text.startsWith("\n"))
		{
			text = text.replaceFirst("\n", "");
		}
		
		return text;
	}
}
