package de.julielab.jsyncc.readbooks.casedescriptions;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.ExtractionUtils;
import de.julielab.jsyncc.tools.LanguageTools;

import org.apache.commons.lang3.exception.ContextedException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CasesPolyneuropathy
{

	public static final String BOOK = "books/18-Polyneuropathie/978-3-662-53871-5.pdf";
	public static final String TYPE = "CaseDescription";
	public static final String TOPIC = "Polyneuropathie";

	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{
		String plainText = ExtractionUtils.getContentByPdftoHTML(Paths.get(bookProperties.bookPath), "-i");

		List<TextDocument> textDocuments = new ArrayList<>();

		if (validateText(plainText))
		{
			ArrayList<String> rawTextDocuments = getRawTexts(plainText);
			ArrayList<String> topic = new ArrayList<>();
			ArrayList<String> headings = getHeadings(plainText);
			topic.add(TOPIC);

			int index = 1;

			for (String text : rawTextDocuments)
			{
				text = cleanText(text, index);
				TextDocument textDocument = new TextDocument();
				textDocument.setText(text);
				textDocument.setSource(
						bookProperties.getTitle() + " " +
						bookProperties.getEditorAuthor() + " " +
						bookProperties.getYear() + " " +
						bookProperties.getPublisher() + " " +
						bookProperties.getDoi()
				);
				textDocument.setIdLong(bookProperties.getSourceShort() + "-" + index);
				textDocument.setDocumentType(TYPE);
				textDocument.setTopic(topic);
				textDocument.setHeading(headings.get(index-1));

				textDocument.setSourcShort(bookProperties.sourceShort);
				textDocument.setBookId(bookProperties.bookId);

				textDocuments.add(textDocument);
				index++;
			}
		}
		return textDocuments;
	}

	public static boolean validateText(String plainText) { return  (plainText.contains("978-3-662-53871-5")); }
	
	private static ArrayList<String> getRawTexts(String plainText)
	{
		ArrayList<String> rawTexts = new ArrayList<String>();
		Pattern pattern = Pattern.compile("j<b>Patientenbeispiel (.*\\n)+?\\w+ ");
		Matcher matcher = pattern.matcher(plainText);

		while (matcher.find())
		{
			if (matcher.group().startsWith("j<b>Patientenbeispiel 12"))
			{
				Pattern restPattern = Pattern.compile("<i>(.+\\n)+?[\\wäöüß]+");
				Matcher restMatcher = restPattern.matcher(plainText);

				if(restMatcher.find(matcher.end()))
				{
					String rawText = matcher.group()+"\n"+restMatcher.group();
					rawTexts.add(rawText);
				}
			}
			else
			{
				rawTexts.add(matcher.group());
			}
		}
		return rawTexts;
	}

	private static String cleanText(String text, int index)
	{
		int italicEndIndex = text.lastIndexOf("</i>");
		text = text.substring(1, italicEndIndex);	//removes starting j and trailing lines
		text = text.replaceAll("<b>.*</b>", "");	//removes everything bold

		//replaces "l&#160;" with "ll" or "l "
		text = text.replaceAll("al&#160;fäl&#160;ig", "allfällig");
		text = text.replace("Al&#160;-", "All-");		//fragment in text 17

		String[] doubleL_Exemptions =
			{
				"kel&#160;hat",
				"Wadenmuskel&#160;und",
				"Schienbeinmuskel&#160;ab",
				"viel&#160;zu",
				"Kältegefühl&#160;leidet",
				"Folsäuremangel&#160;leidet"
			};
		
		for(int i = 0; i < doubleL_Exemptions.length; i++)
		{
			text = text.replaceAll(doubleL_Exemptions[i], doubleL_Exemptions[i].replace("&#160;", " "));
		}

		Pattern doubleLPattern = Pattern.compile("[\\wäöüß]+?l&#160;[\\wäöüß]+");
		Matcher doubleLMatcher = doubleLPattern.matcher(text);

		while (doubleLMatcher.find())
		{
			String replacement = doubleLMatcher.group().replace("l&#160;", "ll");
			text = text.replaceAll(doubleLMatcher.group(), replacement);			
		}

		text = text.replaceAll("&#160;", " ");	//replaces unbreakable whitespace -> problem : "ll" -> "l&#160;"
		text = text.replaceAll("\\s*</i><br/>\\n<([^i].*\\n)*?<i>", "\n");	//replaces page-turns
		text = text.replaceAll("<.*?> *", "");		//removes format-markers

		//clean fragments
		text = text.replaceAll("57.*?Jährige", "57-jährige");	//fragment in text 16
		text = text.replace("Poly neuropathie", "Polyneuropathie");//text 5
		text = text.replace("Polyneuro pathie", "Polyneuropathie");//text 12
		text = text.replace("\"-Zeichen\"", "\"-Zeichen");//text 4
		text = text.replace("7Kap. 5", "");//text 3

		text = text.replaceAll("\\n", " ");
		text = text.replaceAll("\u00A0", " ");
		text = text.replaceAll("\\s+", " ");

		if (text.endsWith(" "))
		{
			text = text.substring(0, text.length() - 1);
		}

		text = text.replaceAll("\u00AD", ""); //soft hyphen
		text = LanguageTools.removeHyphenNew(text);

		if (index == 14)
		{
			text = text.replaceAll("Poly neuropathie", "Polyneuropathie");
			text = text.replaceAll("Polyneuro pathie", "Polyneuropathie");
			text = text.replaceAll("Vita min", "Vitamin");
			text = text.replaceAll("Schmer zen", "Schmerzen");
			text = text.replaceAll("Naturheilmit teln", "Naturheilmitteln");
			text = text.replaceAll("Blutuntersu chung", "Blutuntersuchung");
			text = text.replaceAll("VitaminB12", "VitaminB12-");
			text = text.replaceAll("Präpa rate", "Präparate");
			text = text.replaceAll("Be handlungen", "Behandlungen");
			text = text.replaceAll("Gang bildes", "Gangbildes");
			text = text.replaceAll("Krib beln", "Kribbeln");
			text = text.replaceAll("verord neten", "verordneten");
			text = text.replaceAll("idiopa thische", "idiopathische");
			text = text.replaceAll("neurologi schen", "neurologischen");
		}

		if (index == 15)
		{
			text = text.replaceAll("Autofah ren", "Autofahren");
			text = text.replaceAll("Überan strengung", "Überanstrengung");
			text = text.replaceAll("neurologi sche", "neurologische");
			text = text.replaceAll("Neuropa thie", "Neuropathie");
			text = text.replaceAll("ge nommen", "genommen");
			text = text.replaceAll("gewohn tes", "gewohntes");
			text = text.replaceAll("Ganzkör pertraining", "Ganzkörpertraining");
			text = text.replaceAll("Zellregene ration", "Zellregeneration");
			text = text.replaceAll("betrei ben", "betreiben");
			text = text.replaceAll("Kraft ", "Kraft- ");
		}

		if (index == 16)
		{
			text = text.replaceAll("be ginnen", "beginnen");
			text = text.replaceAll("gene tisch", "genetisch");
			text = text.replaceAll("Gleich zeitig", "Gleichzeitig");
			text = text.replaceAll("Pa tient", "Patient");
			text = text.replaceAll("unter bricht", "unterbricht");
			text = text.replaceAll("Antide pressiva", "Antidepressiva");
			text = text.replaceAll("Physio therapie", "Physiotherapie");
			text = text.replaceAll("Spa ziergänge", "Spaziergänge");
			text = text.replaceAll("Leidens gefährte", "Leidensgefährte");
		}

		return text;
	}

	private static ArrayList<String> getHeadings(String plainText)
	{
		ArrayList<String> headings = new ArrayList<String>();
		Pattern headingPattern = Pattern.compile("\\n<b>\\d\\.\\d(\\.\\d)?.+?</b><br/>(\\n<b>[\\D(&#160;)]+?</b><br/>)*");
		Matcher headingMatcher = headingPattern.matcher(plainText);
		
		Pattern stopPattern = Pattern.compile("Patientenbeispiel \\d\\d?");
		Matcher stopMatcher = stopPattern.matcher(plainText);
		
		String heading = "";
		int matchFrom = 0;
		
		while (stopMatcher.find())
		{
			
			int matchUpTo = stopMatcher.start();
			
			while (headingMatcher.find(matchFrom) && headingMatcher.start() < matchUpTo)
			{
				heading = headingMatcher.group();
				matchFrom = headingMatcher.end();
			}
			matchFrom = matchUpTo;
			
			heading = heading.replaceAll("&#160;", " ");
			heading = heading.replaceAll("\\d\\.\\d(\\.\\d)?","");
			heading = heading.replaceAll("<.+?>", "");
			heading = heading.replaceAll("-\\n", "");
			heading = heading.replaceAll("\\s+", " ").trim();
			headings.add(heading);
		}
		headings.set(3, "Vererbte Polyneuropathien");		//eigentlich "Auffallende Symptome" nach Match-Prinzip,
															//Text handelt aber von vererbter Polyneuropathie
		return headings;
	}
}
