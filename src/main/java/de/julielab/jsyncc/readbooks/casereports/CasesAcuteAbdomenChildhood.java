package de.julielab.jsyncc.readbooks.casereports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.exception.ContextedException;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.ExtractionUtils;
import de.julielab.jsyncc.tools.LanguageTools;
import de.julielab.jsyncc.tools.NormalizationUtils;

public class CasesAcuteAbdomenChildhood {

	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{
		String plainText = ExtractionUtils.getContentByTika(bookProperties.bookPath);
		try
		{
			Files.write(Paths.get(bookProperties.bookPath.toString().replaceAll("pdf", "txt")), plainText.getBytes());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		ArrayList<TextDocument> textDocuments = new ArrayList<>();

		Pattern start = Pattern.compile("(Praxisbeispiel[^e])|((Fallbeispiel\\p{Z}\\d\\u003A)((.?)+))", Pattern.MULTILINE);
		Pattern end = Pattern.compile("(Klinische Überlegung)|^(\\d+\\p{Z}\\d)|((Fallbeispiel\\p{Z}2\\u003A)((.?)+))", Pattern.MULTILINE);

		Matcher startMatcher = start.matcher(plainText);
		Matcher endMatcher = end.matcher(plainText);

		int index = 0;

		while (startMatcher.find())
		{
			int startOffset = startMatcher.end();
			endMatcher.find(startOffset);
			int endOffset = endMatcher.start();

			String text;

			if (startMatcher.group().contains("Fallbeispiel"))
			{
				text = startMatcher.group(4).trim() + "" + plainText.substring(startOffset, endOffset).trim();
			}
			else
			{
				text = plainText.substring(startOffset, endOffset).trim();
			}

			text = cleanText(index, text, plainText);
			index++;

			TextDocument textDocument = createTextDocument((index), text, bookProperties);
			textDocuments.add(textDocument);
		}

		return textDocuments;
	}

	private static String cleanText(int index, String text, String element)
	{
		if (index == 1)
		{
			String first = "verabreichten. Die Therapie soll je nach klini-";
			String second = "schem Verlauf für 10–14 Tage fortgesetzt werden.";

			int startSecond = element.indexOf(first);
			int endSecond = element.indexOf(second) + second.length() + 1;

			text = text + " " + element.substring(startSecond, endSecond);
		}

		text = text.replaceAll("\\(", " (");
		text = text.replaceAll("Mäd-chen", "Mädchen");
		text = text.replaceAll("» ", "»");
		text = text.replaceAll(" «", "«");
		
		text = text.replaceAll("(\\(7\\p{Z}Abschn\\.(.?)+Abb(.?)+\\))","");
		text = text.replaceAll("\u00AD", ""); //soft hyphen
		text = NormalizationUtils.normalizeNewlines(text);
		text = LanguageTools.removeHyphenNew(text);

		return text;
	}

	private static TextDocument createTextDocument(int index, String text, BookProperties bookProperties)
	{
		TextDocument textDocument = new TextDocument();
		textDocument.setText(text);

		textDocument.setSource(
				bookProperties.getTitle() + " " +
				bookProperties.getEditorAuthor() + " " +
				bookProperties.getYear() + " " +
				bookProperties.getPublisher() + " " +
				bookProperties.getDoi()
		);
		textDocument.setDocumentType(bookProperties.documentType.get(0));
		textDocument.setIdLong(bookProperties.getSourceShort() + "-" + index);
		textDocument.setTopic(bookProperties.topics);
		textDocument.setSourcShort(bookProperties.sourceShort);
		textDocument.setBookId(bookProperties.bookId);

		return textDocument;
	}

	public boolean validateText(String plainText) {
		return (plainText.contains("978-3-662-55995-6"));
	}
}
