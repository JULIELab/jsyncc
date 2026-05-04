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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CasesBasicsRadiology{

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

		Pattern chapterHeading = Pattern.compile("((\\d+\\p{Z})?(Kapitel)\\p{Z}+(\\d+)\\p{Z}+·\\p{Z}+(.+)?)", Pattern.MULTILINE);
		Pattern start = Pattern.compile("Fallbeispiel$", Pattern.MULTILINE);
		Pattern end = Pattern.compile("(\\p{Z}Übungsfragen)|(\\d+\\p{Z}\\d+)|(\\p{Z}\\.\\p{Z}+T\\p{Z}ab\\.(.+?))", Pattern.MULTILINE);

		Matcher chapterMatcher = chapterHeading.matcher(plainText);
		Matcher startMatcher = start.matcher(plainText);
		Matcher endMatcher = end.matcher(plainText);
		ArrayList<Integer> cases = new ArrayList<>();

		int index = 1;

		while (chapterMatcher.find())
		{
			int chapterNumber = Integer.parseInt(chapterMatcher.group(4).trim());
			String title = chapterMatcher.group(5).trim();

			if (!cases.contains(chapterNumber) && chapterNumber > 13 && chapterNumber < 25)
			{

				// there are two case 15. first one has page number 152 and
				// these description should be ignored because it was an
				// explanation of this book
				if (!(chapterNumber == 15 && Integer.parseInt(chapterMatcher.group(2).trim()) == 152))
				{
					cases.add(chapterNumber);
					startMatcher.find(chapterMatcher.end());

					int startOffset = startMatcher.end();
					endMatcher.find(startOffset);

					int endOffset = endMatcher.start();
					String text = plainText.substring(startOffset, endOffset).trim();

					text = cleanText(text);
					TextDocument textDocument = createTextDocument(index, title, text, bookProperties);
					textDocuments.add(textDocument);
					index++;
				}
			}
		}

		return textDocuments;
	}

	private static String cleanText(String text)
	{
		text = text.replaceAll("\\d{5}", "");
		text = text.replaceAll("\\d+\\.\\d+\\p{Z}\\u00B7\\p{Z}(Therapie)", "");
		text = text.replaceAll("\\d+\\p{Z}(Kapitel)\\p{Z}\\d+\\p{Z}\\u00B7\\p{Z}(Urogenital)", "");
		text = text.replaceAll("\\d{2}$", "");

		text = NormalizationUtils.correctBullets(text);
		text = NormalizationUtils.normalizeHyphensAndNewlines(text);
		text = NormalizationUtils.normalizeNewlines(text);

		text = LanguageTools.removeHyphenNew(text);

		return text;
	}

	private static TextDocument createTextDocument(int index, String title, String description, BookProperties bookProperties)
	{
		TextDocument textDocument = new TextDocument();
		textDocument.setHeading(title);
		textDocument.setText(description);
		textDocument.setSource(
				bookProperties.getTitle() + " " +
						bookProperties.getEditorAuthor() + " " +
						bookProperties.getYear() + " " +
						bookProperties.getPublisher() + " " +
						bookProperties.getDoi()
		);
		textDocument.setDocumentType(bookProperties.documentType.get(0));
		textDocument.setHeading(title);
		textDocument.setIdLong(bookProperties.getSourceShort() + "-" + index);
		textDocument.setTopic(bookProperties.topics);
		textDocument.setSourcShort(bookProperties.sourceShort);
		textDocument.setBookId(bookProperties.bookId);

		return textDocument;
	}

	public boolean validateText(String plainText) { return (plainText.contains("978-3-662-54278-1")); }
}
