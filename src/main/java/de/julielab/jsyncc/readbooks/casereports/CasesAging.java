package de.julielab.jsyncc.readbooks.casereports;

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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CasesAging
{
	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{
		String plainText = ExtractionUtils.getContentByTika(bookProperties.bookPath);
		try {
			Files.write(Paths.get(bookProperties.bookPath.replaceAll("pdf", "txt")), plainText.getBytes());
		} catch (IOException e) {
			throw new ContextedException(e);
		}

		ArrayList<TextDocument> textDocuments = new ArrayList<>();

		if(validateText(plainText))
		{
			ArrayList<String> headings = extractHeadings(plainText);
			ArrayList<String> topics = extractTopics(plainText);
			ArrayList<String> textList = extractTextElements(plainText);

			for (int i = 0, indexTopic = 0; i < textList.size(); i++, indexTopic++ )
			{
				String text = textList.get(i);

				if (i == 9 || i == 10 || i == 15)
				{
					indexTopic--;
				}

				TextDocument document = createTextDocument(i, indexTopic, topics, headings.get(i), text, bookProperties);
				textDocuments.add(document);
			}
		}
		return textDocuments;
	}

	public static boolean validateText(String plainText)
	{
		return plainText.contains("978-3-642-28905-7");
	}
	
	private static ArrayList<String> extractTextElements(String plainText)
	{
		String[] lines = plainText.split("\\n");
		boolean readText = false;
		StringBuilder text = new StringBuilder();
		
		for (int i = 0; i < lines.length; i++)
		{
			// change of pages
			if (readText)
			{
				if (lines[i].equals("6"))
				{
					i = i + 7;
				}
				
				if (!(lines[i].matches("\\d+")))
				{
					text.append("\n" + lines[i]);
				}
			}

			if (
					lines[i].equals("Fallbeispiel")
				||
					lines[i].matches("Fallbeispiel [1-3]") 
				||
					((i > 0) && lines[i-1].equals("8.1 Fallbeispiel: Probleme beim Gehen"))
				)
			{
				readText = true;
			}

			else if (lines[i].equals("")) 
			{
				readText = false;
			}
		}

		String[] paragraphs = text.toString().split("\\n\\n");
		ArrayList<String> textList = new ArrayList<>(Arrays.asList(paragraphs));
		
		return textList;
	}

	private static String cleanText(String text)
	{
		text = text.replaceAll("\u00a0", " "); // No-Break Space
		text = text.replaceAll(" -jährige", "-jährige");
		text = text.replaceAll(", 7 Abschn. 1.7.3", "");
		text = text.replaceAll(" \\)", ")");
		text = text.replaceAll(" \\.", ".");

		text = NormalizationUtils.normalizeNewlines(text);
		text = LanguageTools.removeHyphenNew(text);

		text = text.replaceAll(" Sozialanamnese: ", "\nSozialanamnese:\n");
		text = text.replaceAll(" Befund: ", "\nBefund:\n");
		text = text.replaceAll(" Führende Diagnose: ", "\nFührende Diagnose:\n");
		text = text.replaceAll(" Bisherige Medikation: ", "\n Bisherige Medikation:\n");

		if (text.startsWith(" "))
		{
			text = text.replaceFirst(" ", "");
		}

		if (text.startsWith("\n"))
		{
			text = text.replaceFirst("\n", "");
		}

		return text;
	}

	private static ArrayList<String> extractTopics(String plainText)
	{
		String[] lines = plainText.split("\\n");
		ArrayList<String> topics = new ArrayList<>();

		Pattern p1 = Pattern.compile("^[0-9]{1,2}\\s");
		Pattern p2 = Pattern.compile("^[A-Z]\\.\\s");

		for (int i = 0; i < lines.length - 1; i++)
		{
			Matcher m1 = p1.matcher(lines[i]);
			Matcher m2 = p2.matcher(lines[i+1]);
			if (m1.find() && m2.find())
			{
				lines[i] = lines[i].replaceAll("\\d*", "");
				lines[i] = lines[i].replaceAll("\\.+", "");
				lines[i] = lines[i].replaceAll("\\s{2,}", "");
				lines[i] = lines[i].substring(1, lines[i].length());
				topics.add(lines[i]);
			}
		}
		return topics;
	}

	private static ArrayList<String> extractHeadings(String plainText)
	{
		String[] lines = plainText.split("\\n");
		ArrayList<String> headings = new ArrayList<String>();
		Pattern p = Pattern.compile("^\\d*\\.[0-9]");
		Pattern q = Pattern.compile("\\d+$");
		
		for (int i = 480; i < 13400; i++)
		{
			Matcher m = p.matcher(lines[i]);

			if (
					(m.find())
				&&
					(lines[i].contains("Fallbeispiel"))
				&&
					(!lines[i].contains("Fragen"))
				)
			{	
				if (lines[i + 1] != "")
				{
					lines[i] = lines[i] + lines[i + 1];
				}

				Matcher n = q.matcher(lines[i]);

				if (n.find())
				{
					String uncleaned = lines[i];
					String cleaned = "";
					String[] parts = uncleaned.split("\\s");
					Pattern r = Pattern.compile("[a-zA-z]");

					for (int j = 0; j < parts.length; j++)
					{
						Matcher o = r.matcher(parts[j]);

						if (
								(o.find())
							&&
								(!parts[j].startsWith("Fallbeispiel"))
							)
						{
							if (parts[j].contains("Krankenhaus"))
							{
								cleaned = cleaned + parts[j];
							}
							else
							{
								cleaned = cleaned + parts[j] + " ";
							}
						}
					}

					headings.add(cleaned);
				}
			}
		}
		headings.add(15, "Palliative Therapie");

		return headings;
	}

	private static TextDocument createTextDocument(
			int index,
			int indexTopic,
			ArrayList<String> topics,
			String title,
			String text,
			BookProperties bookProperties)
	{
		TextDocument textDocument = new TextDocument();

		textDocument.setText(cleanText(text));
		textDocument.setSource(
				bookProperties.getTitle() + " " +
				bookProperties.getEditorAuthor() + " " +
				bookProperties.getYear() + " " +
				bookProperties.getPublisher() + " " + 
				bookProperties.getDoi()
				);
		textDocument.setDocumentType(bookProperties.documentType.get(0));
		textDocument.setHeading(title);
		textDocument.setIdLong(bookProperties.getSourceShort() + "-" + (index+1));
		textDocument.setSourcShort(bookProperties.sourceShort);
		textDocument.setBookId(bookProperties.bookId);

		textDocument.setTopic(bookProperties.topics);
		//ArrayList<String> topicDoc = new ArrayList<>();
		//topicDoc.add(bookProperties.topics.get(0));
		//topicDoc.add(topics.get(indexTopic));
		//textDocument.setTopic(topicDoc);
		return textDocument;
	}
}
