package de.julielab.jsyncc.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.exception.ContextedException;
import org.yaml.snakeyaml.Yaml;

import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.readbooks.casereports.CasesAging;

public class HyphenYamlTestingClass {	
	
	//Differenzen zwischen Wiki und Klassen
	
	public static Map<String, String> bookPaths;
	public static Map<String, String> sources;
	public static Map<String, String> sourcesShort;
	
	 public static void readYaml() throws ContextedException
	 {
		 Yaml yaml = new Yaml();
			File yamlFile = new File("yamlFile.yaml");
			try {
			InputStream inputStream = new FileInputStream(yamlFile);
			Map<String, Map<String, String>> yamlMap = yaml.load(inputStream);
			bookPaths = yamlMap.get("bookPaths");
			sources = yamlMap.get("sources");
			sourcesShort = yamlMap.get("sources-short");
			} catch (FileNotFoundException fnf) {throw new ContextedException (fnf);}		 
	 }
	
	/*
	 *	Ideen zu und Probleme bei der Zeichen-Normalisierung:
	 *		Erwartungen an den Input (Liste aller TextDocuments oder ähnliches):
	 *			- Inhaltlich geputzte Texte -> keine 'Abb. 3.1'
	 *			- Codiert geparsedte Satz- und Sonderzeichen ersetzt
	 *			- Stichpunkt hat Form :"\n-"
	 *			(- potentiell : Bulletpoints abhandeln)
	 *
	 *		Bearbeitungsschritte:
	 *			- Alle Dashs "–" durch "-" ersetzen
	 *			- Sonderzeichen ersetzen "<<" -> """
	 *			- Whitespace putzen --> " {2,}" -> " ", "\n +" -> "\n", " ." -> ".", "-\n{2,}" -> "-\n", etc.
	 *			- Bulletpoints putzen, da durch sie die meisten Kontrollfälle zu erwarten sind -> Probleme bei Multiline-Stichpunkten und Suffixen (Haarfarbe und \n-länge)
	 *				- prüfe ob Wort zusammengezogen werden kann:
	 *					> ja 	: Kontroll
	 *					> nein	:
	 *						> erster Teil ist "und", "oder", etc.: Suffix
	 *						> sonst: Stichpunkt (Einrückung -> multiline-Problem)
	 *				- Multiline-Problem nicht gelöst (weiß nicht, wann Stichpunkt endet)
	 *			- Hyphen putzen	:
	 *				- suche nächstes " \S+\s*-\s*\S ":
	 *					> wenn in Spezialfall-Liste oder Schreibungs-Liste enthalten 
	 *							: ersetze
	 *					> sonst	:	suche nach allen Vorkommen des Wortes und lege ihre Häufigkeit in Array ab
	 *						 > werte das Array aus
	 *							> Array eindeutig:
	 *								> ja	: ersetze alle Vorkommen dieser Trennung und füge Schreibung einer Liste hinzu
	 *								> nein	: Ablage in Kontroll-Liste
	 *		- Probleme:
	 *			> Bulletpoints
	 *			> bei Wörtern, die mehr als ein "-" enthalten ("Apfel-Bir\n-nen-Ding")
	 *			> bei buch-abhängigen Schreibweisen
	 *			> bei Worten die mit Klammern beginnen oder enden -> Fehler im Pattern
	 *			> getConcatinatability ignoriert Case-Fehler (und geht davon aus, dass Abkürzungen nicht getrennte werden)
	 *
	 *	Bulletpoints können auch beim Hyphen-putzen mitgenommen werden und landen dann vermutlich in der Kontroll-Liste
	 *		-> vertretbar, wenn nicht viele Stichpunkte auftreten, aber Formatierung von multiline-Punkten noch unklar
	 *
	 *	Mithilfe der Kontroll-Liste soll eine Spezialfall-Liste erstellt werden, die der Putz-Funtkion mit übergeben wird
	 */
	
		
	public static void main(String args[]) throws ContextedException
	{
		
		//		CasesAging ca = new CasesAging();
//		String casesAgingPlaintext = ca.parseBook(Paths.get(CasesAging.BOOK));
//		List<TextDocument> casesAgingDocuments = new ArrayList<TextDocument>();
//		casesAgingDocuments = ca.extractContent(casesAgingPlaintext);
//		cleanHyphens((ArrayList<TextDocument>) casesAgingDocuments);
	}
	

	
	public static void cleanWhitespaceAndSpecialCharacters(ArrayList<TextDocument> documents)
	{
		for (int i = 0; i < documents.size(); i++)
		{
			String text = documents.get(i).getText();
			text = text.replaceAll(" {2,}", " ");
			text = text.replaceAll("\\n{2,}", "\n");
			text = text.replaceAll("\\n +", "\n");
			text = text.replaceAll("\\s+\\.", ".");
			text = text.replaceAll("\\s+,", ",");
			text = text.replaceAll("\\s+;", ";");
			text = text.replaceAll("\\(\\s+", "(");
			text = text.replaceAll("\\s+\\)", ")");
			//etc.
			documents.get(i).setText(text);
		}
	}
	
	public static void cleanBulletpoints(ArrayList<TextDocument> documents)
	{
		for (int i = 0; i < documents.size(); i++)
		{
			String text = documents.get(i).getText();
			Matcher bulletpointMatcher = Pattern.compile("\\S+ *\\n- *\\S+").matcher(text);
			while (bulletpointMatcher.find())
			{
				String word = bulletpointMatcher.group();
				String [] parts = word.split("-");
				
			}
		}
	}
	
	public static void cleanHyphens(ArrayList<TextDocument> documents)
	{
		for (int i = 0; i < documents.size(); i++)
		{
			Matcher hyphenMatcher = Pattern.compile("\\S+\\s*-\\s*\\S+").matcher(documents.get(i).getText());
			while (hyphenMatcher.find())
			{
//				System.out.println("\t\tfound");
				String word = hyphenMatcher.group();
				String[] parts = word.split("-");
//				System.out.println(word);

				// Wörter mit mehreren "-" müssen gesondert behandelt werden, 
				// da es sein kann, dass i-1 mit i und i mit i+1 verknüpfbar sind, aber nicht i-1 mit i mit i+1 
				// (zumindest Gegenteil unbewiesen)	
				if (parts.length == 2)
				{	
					parts[0] = parts[0].replaceAll("[^(\\wäöüÄÖÜ)]", "");
					parts[1] = parts[1].replaceAll("[^(\\wäöüÄÖÜ)]", "");
//					System.out.println(parts[0]);
					int[] occurenceVariants = getWordVariantOccurrences(parts, documents);
					System.out.println(word);
					interpretVariantOccurrences(occurenceVariants);
				}
			}
		}
	

	}
	
	public static int[] getWordVariantOccurrences(String[] parts, ArrayList<TextDocument> documents)
	{	
		//Varianten: "", " -", "\n-", " \n-", "\n- ", " \n- ", " - ", " - \n", " -\n", "-", "-\n", "- \n","- ", Fehler
		int[] variantOccurrences = new int[14];
		if (parts[0].startsWith("(")&&!parts[0].endsWith(")"))	//Klammern lösen Fehler aus
				parts[0] = parts[0].substring(1);
		if (parts[1].endsWith(")")&&!parts[1].startsWith("("))
			parts[1] = parts[1].substring(0, parts[1].length()-1);
		for (int i = 0; i < documents.size(); i++)
		{
			Matcher occurenceMatcher = Pattern.compile(parts[0]+ "\\s*-?\\s*" + parts[1]).matcher(documents.get(i).getText());	
			while (occurenceMatcher.find())
			{
				String wordOccurence = occurenceMatcher.group();
				String whitespaceVariant = getWhitespaceVariant(wordOccurence);
				switch (whitespaceVariant)		//potentiell anders, wenn vorher Bulletpoints rausgenommen werden
				{
				case " -"	:	variantOccurrences[0]++;	break;	//suffix	
				case "\n-"	:	variantOccurrences[1]++; 	break;	//bulletpoint, suffix
				case " \n-"	:	variantOccurrences[2]++;	break;	//bulletpoint, suffix
				case "\n- "	:	variantOccurrences[3]++;	break;	//bulletpoint
				case " \n- ":	variantOccurrences[4]++;	break;	//bulletpoint, dash
				case " - "	:	variantOccurrences[5]++;	break;	//dash
				case " - \n":	variantOccurrences[6]++;	break;	//dash
				case " -\n"	:	variantOccurrences[7]++;	break;	//dash?
				case "-"	:	variantOccurrences[8]++;	break;	//conjunction
				case ""		:	variantOccurrences[9]++;	break;	//concatenated
				case "-\n"	:	variantOccurrences[10]++;	break;	//concatenation
				case "- \n"	:	variantOccurrences[11]++;	break;	//concatenation, prefix
				case "- "	:	variantOccurrences[12]++;	break;	//prefix
				default		:	variantOccurrences[13]++;	break;	//error
				}
			}
		}
		return variantOccurrences;
	}
	
	public static String getWhitespaceVariant (String word)
	{
		String wsv = "";
		Matcher WSVMatcher = Pattern.compile("\\s*-\\s*").matcher(word);
		if (WSVMatcher.find())
			wsv = WSVMatcher.group();
		return wsv;
	}
	
	public static String interpretVariantOccurrences(int[] variantOccurrences)	//wenn Bulletpoints vorher rausgenommen werden einfacher und weniger Kontrollfälle
	{
		String replacement = null;
		if (variantOccurrences[13] > 0)
			return replacement;
		int suffixVariantCounter = variantOccurrences[0] + variantOccurrences[1] + variantOccurrences[2];
		
		for (int i = 0; i < variantOccurrences.length; i++)
		{System.out.print(variantOccurrences[i]);}
		System.out.println("\n");
		return replacement;
	}
	
//	public static boolean getConcatenatability(String[] parts)
//	{
//		boolean concatenatable = false;
//		parts[0] = parts[0].trim();
//		parts[1] = parts[1].trim();
//			//Bestandteile, die ausschließlich aus Zahlen bestehen werden nicht konkateniert
//			//beginnt der zweite Teil im Uppercase so wird nicht konkateniert
//			//Bestandteile, die aus einem oder weniger Zeichen bestehen werden nicht konkateniert
//			//Stehen Sonderzeichen/Interpunktionszeichen/Zahlen am Ende/am Anfang wird nicht konkateniert
//			//endet parts[i] mit einer Zahl wird nicht konkateniert
//			if (!(parts[0].matches("\\d+") || parts[1].matches("\\d+") 
//					|| Character.isUpperCase(parts[1].codePointAt(0))
//					|| parts[0].length() <= 1 || parts[1].length() <= 1
//					|| parts[0].matches("$[\\W0-9]") || parts[1].matches("^[\\W0-9]")))
//			{
//				
//				concatenatable = true;
//			}
//		return concatenatable;	
//	}

	
	
}
