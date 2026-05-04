package de.julielab.jsyncc.readbooks.casedescriptions.discussion;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.ExtractionUtils;
import de.julielab.jsyncc.tools.LanguageTools;
import de.julielab.jsyncc.tools.NormalizationUtils;

import org.apache.commons.lang3.exception.ContextedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CasesPediatricEmergencies
{
	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{
		String plainText = ExtractionUtils.getContentByTika(bookProperties.bookPath);

		try
		{
			Files.write(Paths.get(bookProperties.bookPath.replaceAll("pdf", "txt")), plainText.getBytes());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		CasesPediatricEmergencies pediatricEmergencies = new CasesPediatricEmergencies();
		ArrayList<TextDocument> textDocuments = new ArrayList<>();

		if (pediatricEmergencies.validateText(plainText))
		{
			int i = 0;	//Modulo-Schalter
			ArrayList<String> texts = new ArrayList<>();
			String caseDescription = "";

			ArrayList<String> rawParagraphs = getRawParagraphs(plainText);

			for (String paragraph : rawParagraphs)
			{
				paragraph = cleanText(paragraph);
				if((i+1)%5 != 0)
				{
					caseDescription = caseDescription + paragraph;
				} else {
					texts.add(caseDescription);
					texts.add(paragraph);
					caseDescription = "";
				}
				i++;
			}
			ArrayList<String> headings = extractHeadings(plainText);

			//List<String> topic = bookProperties.getTextType();

			//ArrayList<String> topic = new ArrayList<String>();
			//topic.add(TOPIC_1);
			//topic.add(TOPIC_2);
			//topic.add(TOPIC_3);
	
			i = 0;	//reset i
			for (String text : texts)
			{
				text = NormalizationUtils.normalizeHyphensAndNewlines(text);
				text = NormalizationUtils.normalizeNewlines(text);
				text = LanguageTools.removeHyphenNew(text);
				
				if (text.startsWith("\n"))
				{
					text = text.replaceFirst("\n", "");
				}
				
				TextDocument textDocument = new TextDocument();
				textDocument.setText(text);
				textDocument.setIdLong(bookProperties.getSourceShort() + "-" + (i+1));
				textDocument.setSource(
						bookProperties.getTitle() + " " +
						bookProperties.getEditorAuthor() + " " +
						bookProperties.getYear() + " " +
						bookProperties.getPublisher() + " " +
						bookProperties.getDoi()
				);

				textDocument.setTopic(bookProperties.topics);
				textDocument.setHeading(headings.get(i/2));	//.1 - .4 ein Text .5 extra (type = comment)
				textDocument.setSourcShort(bookProperties.sourceShort);
				textDocument.setBookId(bookProperties.bookId);
				textDocuments.add(textDocument);

				ArrayList<String> inRelationOf = new ArrayList<String>();

				//TODO check
				//String TYPE = bookProperties.getTextType();
				String TYPE_1 = bookProperties.documentType.get(0); //TYPE.split("_")[0];
				String TYPE_2 = bookProperties.documentType.get(1); //TYPE.split("_")[1];
				
				if (i % 2 == 0)	//sets inRelationOf
				{
					textDocument.setDocumentType(TYPE_1);
					inRelationOf.add(Integer.toString(i + 1));
				}
				else
				{
					textDocument.setDocumentType(TYPE_2);
					inRelationOf.add(Integer.toString(i - 1));
				}
				i++;
			}
		}
		return textDocuments;
	}

	public boolean validateText(String plainText) {
		return (plainText.contains("978-3-662-49305-2"));
	}

	private static ArrayList<String> extractHeadings(String plainText)
	{
		ArrayList<String> headings = new ArrayList<String>();
		Pattern headingPattern = Pattern.compile("9\\.\\d.*?\\.\\s+\\.");
		Matcher headingMatcher = headingPattern.matcher(plainText);
		boolean found = headingMatcher.find();
		while (found)
		{
			String heading = headingMatcher.group().substring(4);
			Pattern p = Pattern.compile("\\s*\\.\\s+\\.");
			Matcher m = p.matcher(heading);
			heading = m.replaceAll("");
			headings.add(heading);
			found = headingMatcher.find();
		}
		return headings;
	}

	//Die 6 Bsp. sind in je 5 Unterabschnitte unterteilt, die einzeln bearbeitet werden
	private static ArrayList<String> getRawParagraphs(String plainText)
	{
		ArrayList<String> paragraphs = new ArrayList<>();
		String examplesExcerpt = plainText.substring(plainText.indexOf("9.1.1 Einsatzmeldung\n"), plainText.indexOf("252 10"));

		Pattern paragraphPattern = Pattern.compile("9\\.\\d\\.\\d.*?\\n");
		Matcher paragraphMatcher = paragraphPattern.matcher(examplesExcerpt);

		boolean found = paragraphMatcher.find();
		while (found)
		{
			int paragraphStart = paragraphMatcher.start();
			found = paragraphMatcher.find();
			if (found)
			{
				int paragraphEnd = paragraphMatcher.start();
				paragraphs.add(examplesExcerpt.substring(paragraphStart, paragraphEnd));
			} else {
				paragraphs.add(examplesExcerpt.substring(paragraphStart));
			}
			
		}
		return paragraphs;
	}

	private static String cleanText(String text)
	{
		ArrayList<String> hyphenLowerCaseExceptions = new ArrayList<>();
			text = deleteHeadlines(text);
			text = deleteReferences(text);
			text = cleanWhitespace(text);
			text = cleanHyphens(text, hyphenLowerCaseExceptions);

		return text;
	}

	private static String deleteHeadlines(String text)
	{
		Pattern paragraphHeadlinePattern = Pattern.compile("9\\.\\d.*");
		Matcher paragraphHeadlineMatcher = paragraphHeadlinePattern.matcher(text);
		if (paragraphHeadlineMatcher.find())
		{
			text = paragraphHeadlineMatcher.replaceAll("");
		}
		
		Pattern unevenPageNumberHeadlinePattern = Pattern.compile("\\d{3}\\s\\d");
		Matcher unevenPageNumberHeadlineMatcher = unevenPageNumberHeadlinePattern.matcher(text);
		if (unevenPageNumberHeadlineMatcher.find())
		{
			text = unevenPageNumberHeadlineMatcher.replaceAll("");
		}

		Pattern evenPageNumberHeadlinePattern = Pattern.compile("\\d{3}.*?\\n\\n9\\n");
		Matcher evenPageNumberHeadlineMatcher = evenPageNumberHeadlinePattern.matcher(text);
		if (evenPageNumberHeadlineMatcher.find())
		{
			text = evenPageNumberHeadlineMatcher.replaceFirst("");
		}

		text = text.replaceAll("RS/RA\n", "");
		text = text.replaceAll("\nNotarzt\n", "");

		return text;
	}

	private static String deleteReferences(String text)
	{
		Pattern referencePattern = Pattern.compile("(\\s\\(\\d.*?Abschn.*?\\))");
		Matcher referenceMatcher = referencePattern.matcher(text);
		if (referenceMatcher.find())
		{
			text = referenceMatcher.replaceAll("");
		}
		return text;
	}

	private static String cleanWhitespace(String text)
	{
		Pattern emptyLinePattern = Pattern.compile("\\n{2,}");
		Pattern multipleWhitespacePattern = Pattern.compile(" {2,}");
		Pattern dotPattern = Pattern.compile("\\s+\\.");
		Pattern closingBracketPattern = Pattern.compile("\\s+\\)");
		Pattern openingBracketPattern = Pattern.compile("\\(\\s+");
		Pattern commaPattern = Pattern.compile("\\s+,");

		Matcher emptyLineMatcher = emptyLinePattern.matcher(text);
		if (emptyLineMatcher.find())
		{
			text = emptyLineMatcher.replaceAll("\n");
		}

		Matcher multpileWhitespaceMatcher = multipleWhitespacePattern.matcher(text);
		if (multpileWhitespaceMatcher.find())
		{
			text = multpileWhitespaceMatcher.replaceAll(" ");
		}

		Matcher dotMatcher = dotPattern.matcher(text);
		if (dotMatcher.find())
		{
			text = dotMatcher.replaceAll(".");
		}

		Matcher closingBracketMatcher = closingBracketPattern.matcher(text);
		if (closingBracketMatcher.find())
		{
			text = closingBracketMatcher.replaceAll(")");
		}

		Matcher openingBracketMatcher = openingBracketPattern.matcher(text);
		if (openingBracketMatcher.find())
		{
			text = openingBracketMatcher.replaceAll("(");
		}

		Matcher commaMatcher = commaPattern.matcher(text);
		if (commaMatcher.find())
		{
			text = commaMatcher.replaceAll(",");
		}
		return text;
	}
	
	private static String cleanHyphens(String text, ArrayList<String> hyphenLowerCaseExceptions)	//keine Ausnahmen, alles nett
	{
		String[] lines = text.split("\\n");
		text = "";
		Pattern hyphenPattern = Pattern.compile("\\s*-\\s*$");

		for (int i = 0; i < lines.length; i++) 
		{
			Matcher hyphenMatcher = hyphenPattern.matcher(lines[i]);
			if (hyphenMatcher.find()) 
			{
				String[] words = lines[i+1].split("\\s");
				String wordRemainder = words[0];
				if (wordRemainder.endsWith("-"))
				{
					wordRemainder = wordRemainder + " " + words[1];
				}
				if (hyphenLowerCaseExceptions.contains(wordRemainder) || Character.isUpperCase(wordRemainder.charAt(0)))
				{
					lines[i] += wordRemainder;
					if (wordRemainder.length() + 1 < lines[i+1].length())
					{
						lines[i+1] = lines[i + 1].substring(wordRemainder.length() + 1, lines[i + 1].length());
					}
					else lines[i+1] = "";
				}
				else
				{
					lines[i] = lines[i].substring(0, lines[i].length() - 1) + wordRemainder;
					if (wordRemainder.length() + 1 < lines[i+1].length())
					{
						lines[i+1] = lines[i + 1].substring(wordRemainder.length() + 1, lines[i + 1].length());
					}
					else lines[i+1] = "";
				}
			}
			if (lines[i] != "")
			{
				text = text + lines[i] + "\n";
			}
		}
		return text;
	}
}