package de.julielab.jsyncc.readbooks.casereports;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.ExtractionUtils;
import de.julielab.jsyncc.tools.LanguageTools;

import org.apache.commons.lang3.exception.ContextedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CasesSportsCardiology
{
	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{

		String plainText = ExtractionUtils.getContentByTika(bookProperties.bookPath);
		try
		{
			Files.write(Paths.get(bookProperties.bookPath.toString().replaceAll("pdf", "txt")), plainText.getBytes());
		} catch (IOException e)
		{
			throw new ContextedException(e);
		}

		List<TextDocument> textDocuments = new ArrayList<>();

		if (validateText(plainText))
		{
			List<String> texts = new ArrayList<>();

			texts = getTexts(plainText);
			
			for (int i = 0; i < texts.size(); i++)
			{
				TextDocument textDocument = new TextDocument();
				textDocument.setText(cleanText(texts.get(i)));

				textDocument.setSource(
						bookProperties.getTitle() + " " +
						bookProperties.getEditorAuthor() + " " +
						bookProperties.getYear() + " " +
						bookProperties.getPublisher() + " " +
						bookProperties.getDoi()
				);
				textDocument.setDocumentType(bookProperties.documentType.get(0));
				textDocument.setIdLong(bookProperties.getSourceShort() + "-" + (i+1));
				textDocument.setTopic(bookProperties.topics);
				textDocument.setSourcShort(bookProperties.sourceShort);
				textDocument.setBookId(bookProperties.bookId);

				textDocuments.add(textDocument);
			}
		}
		return textDocuments;
	}

	public static boolean validateText(String plainText) { return (plainText.contains("978-3-662-43711-7"));}

	private static List<String> getTexts (String plainText)
	{
		List<String> texts = new ArrayList<>();

		Pattern excerptPattern = Pattern.compile("13\\.7\\.\\d Fallbeispiel \\d\\n(.*\\n)*?veniert wird\\.");
		Matcher excerptMatcher = excerptPattern.matcher(plainText);
		Pattern textBoundaryPattern = Pattern.compile("13\\.7\\.\\d");

		while (excerptMatcher.find())
		{
			String excerpt = excerptMatcher.group();
			Matcher textBoundaryMatcher = textBoundaryPattern.matcher(excerpt);
			int textStart = 0;

			while (textBoundaryMatcher.find())
			{
				texts.add(excerpt.substring(textStart, textBoundaryMatcher.start()));
				textStart = textBoundaryMatcher.start();
			}
			texts.add(excerpt.substring(textStart));
		}

		texts.remove(0);
		return texts;
	}

	private static String cleanText(String text)
	{
		text = text.replaceAll("\\s\\(\\. Abb\\..*?\\)", "");
		text = text.replaceAll(" \\. Abb.*\\n.*", "");
		text = text.replaceAll("13\\.7\\..*", "");
		text = text.replaceAll("\n.{1,3}\n", "");
		text = text.replaceAll("Kapitel.*", "");
		text = text.replaceAll("\n{2,}", "\n");
		text = text.replaceAll(" \\.", ".");
		text = text.replaceAll("\\n", " ");
		text = LanguageTools.removeHyphenNew(text);

		if (text.startsWith(" "))
		{
			text = text.replaceFirst(" ", "");
		}

		return text;
	}
}
