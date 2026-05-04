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

public class CasesHumanGenetics {

	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{
		String plainText = ExtractionUtils.getContentByTika(bookProperties.bookPath);
		try {
			Files.write(Paths.get(bookProperties.bookPath.toString().replaceAll("pdf", "txt")), plainText.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList<TextDocument> textDocuments = new ArrayList<>();

		Pattern start = Pattern.compile("([^(\\p{Z}7\\p{Z}](Praxisfall)((.?)+))", Pattern.MULTILINE);
		Pattern end = Pattern.compile("(\\p{Z}+(k(Epidemiologie|Klinische Merkmale|Genetik und Ätiologie|α-Thalassämie))|(Verursacht wird die Osteogenesis imperfecta durch ))", Pattern.MULTILINE);
		Matcher startMatcher = start.matcher(plainText);
		Matcher endMatcher = end.matcher(plainText);

		int index = -1;

		while (startMatcher.find())
		{
			int startOffset = startMatcher.end();
			endMatcher.find(startOffset);
			int endOffset = endMatcher.start();

			String text = plainText.substring(startOffset, endOffset).trim();
			String title = startMatcher.group(3);
			text = postProcessDescription(index, text);
			index++;

			TextDocument textDocument = createTextDocument((index+1), title, text, bookProperties);
			textDocuments.add(textDocument);
		}

		return textDocuments;

	}

	private static String postProcessDescription(int caseNumber, String text)
	{
		int caseNum = caseNumber + 1;
		text = text.replaceAll("6\\p{Z}6", "");
		text = text.replaceAll("(\\d+\\p{Z})?\\d+\\.\\d\\p{Z}\\u00B7\\p{Z}(.?)+", "");
		text = text.replaceAll("\\d+\\p{Z}(Kapitel)\\p{Z}\\d+\\p{Z}\\u00B7\\p{Z}(.?)+", "");
		text = text.replaceAll("\\d+\\p{Z}?\n", "");
		text = text.replaceAll("(.?)+\\p{Z}\\u003D\\p{Z}(.?)+", "");
		text = NormalizationUtils.normalizeHyphensAndNewlines(text);
		text = text.replaceAll("(\\p{Z}\\.\\p{Z}(Abb|Tab)(\\p{Z})?\\.\\p{Z}\\d+\\.\\d+(.?)+)", "");

		if (caseNum == 10) {
			text = text.replaceAll("20", "");
		} else if (caseNum == 30) {
			String first = "im Bauch- und Hüftbereich. Wie er berichtet, setzte die ";
			int endFirst = text.indexOf(first) + first.length() - 1;
			int startSecond = text.indexOf("ubertät bei ihm relativ spät ein und muss er sich nur ", endFirst);
			text = text.substring(0, endFirst) + text.substring(startSecond);
		}

		text = NormalizationUtils.normalizeNewlines(text);
		text = text.replaceAll("\n", " ");
		text = LanguageTools.removeHyphenNew(text);

		return text;
	}

	private static TextDocument createTextDocument(int caseNumber, String title, String text, BookProperties bookProperties) {
		TextDocument textDocument = new TextDocument();
		textDocument.setHeading(title);
		textDocument.setText(text);

		textDocument.setSource(
			bookProperties.getTitle() + " " +
			bookProperties.getEditorAuthor() + " " +
			bookProperties.getYear() + " " +
			bookProperties.getPublisher() + " " +
			bookProperties.getDoi()
		);
		textDocument.setDocumentType(bookProperties.documentType.get(0));
		textDocument.setIdLong(bookProperties.getSourceShort() + "-" + caseNumber);
		textDocument.setTopic(bookProperties.topics);

		textDocument.setSourcShort(bookProperties.sourceShort);
		textDocument.setBookId(bookProperties.bookId);

		return textDocument;
	}

	public boolean validateText(String plainText) {
		return  (plainText.contains("978-3-642-28907-1"));
	}
}