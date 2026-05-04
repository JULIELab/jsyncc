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

public class CasesRheumatologyPractice
{
	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{
		String plainText = ExtractionUtils.getContentByTika(bookProperties.bookPath);
		try {
			Files.write(Paths.get(bookProperties.bookPath.replaceAll("pdf", "txt")), plainText.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList<TextDocument> textDocuments = new ArrayList<>();

		if (validateText(plainText)) {
			Pattern start = Pattern.compile(
					"(((\\(Historisches\\)\\p{Z})?(Fallbeispiel:((.?)+\n((\\p{Lu}\\p{Alpha}+[^\\p{Z}]\n)|(Rückenschmerzen\\u003F)|(\\p{Lower}+\\p{Z}(.?)+))?)))|(Prominentes kurzes Fallbeispiel)|(Fallbeispiel\n))",
					Pattern.MULTILINE);

			Pattern end = Pattern.compile(
					"((Fallbeispiel:(.?)+)|(Daniela Loisl)|(Literatur\n+Literatur)|(Literatur\n1\\..*)|^((Indikation|Kontrollen|Fallbeispiel|Therapie|Sulfasalazin|Rheumatoide Arthritis und Verlauf: psychosomatische Aspekte)\n)|(^([^�])\\d\\.\\d(\\.\\d)?\\p{Z}.*))",
					Pattern.MULTILINE);

			Matcher startMatcher = start.matcher(plainText);
			Matcher endMatcher = end.matcher(plainText);
			int index = -1;
			
			int snips = 0;

			while (startMatcher.find())
			{
				snips = snips + 1;

				int startOffset = startMatcher.end();
				endMatcher.find(startOffset);
				int endOffset = endMatcher.start();

				String title = startMatcher.group(5);

				if (title != null) {
					title = title.replaceAll("\n", " ");
					title = title.replaceAll(" \\. \\. \\.", "");

					if (title.startsWith(" ")) {
						title = title.replaceFirst(" ", "");
					}
					if (title.endsWith(" ")) {
						title = title.substring(0, title.length() - 1);
					}
				}

				String text = plainText.substring(startOffset, endOffset).trim();
				index++;

				if (text.contains("Kommentar")) {
					String first = "Kommentar";
					int commentFirst = text.indexOf(first) + first.length() + 1;

					TextDocument textDocument = createTextDocument(index, title, cleanText(text.substring(0, text.indexOf(first)).trim()), bookProperties.documentType.get(0), bookProperties);
					textDocuments.add(textDocument);

					index++;
					TextDocument textDocumentComment = createTextDocument(index, title, cleanText(text.substring(commentFirst).trim()), bookProperties.documentType.get(1), bookProperties);
					textDocuments.add(textDocumentComment);
				} else {
					text = plainText.substring(startOffset, endOffset).trim();
					text = cleanText(text);

					if (index != 51) {
						TextDocument textDocument = createTextDocument(index, title, text, bookProperties.documentType.get(0), bookProperties);
						textDocuments.add(textDocument);
					}

					if (text.contains("Barbara")) // "Text 51"
					{
						index--;
					}
				}
			}
		}

		return textDocuments;
	}

	private static String cleanText(String text)
	{
		if (text.contains("Tabelle 3.6 ASAS-Klassiﬁkationskriterien für axiale SpA (bei Patienten mit ")) {

			String first = "Tabelle 3.6 ASAS-Klassiﬁkationskriterien für axiale SpA (bei Patienten mit ";
			int endFirst = text.indexOf(first);
			int startSecond = text.indexOf("„sogenannte gute und schlechte Zeiten“, einerseits über Phasen, wo er kaum aus", endFirst);
			text = text.substring(0, endFirst) + text.substring(startSecond);
		}
		if (text.contains("Tabelle 3.10 BASDAI (Bath Ankylosing Spondylitis Disease Activity Index) [11]")) {
			String first = "Tabelle 3.10 BASDAI (Bath Ankylosing Spondylitis Disease Activity Index) [11]";
			int endFirst = text.indexOf(first);
			int startSecond = text.indexOf("Wegen einer Hernien-Operation musste vorübergehend die Basistherapie über", endFirst);
			text = text.substring(0, endFirst) + text.substring(startSecond);
		}
		if (text.contains("Tabelle 7.5 Diﬀerentialdiagnose der LORA")) {
			String first = "Tabelle 7.5 Diﬀerentialdiagnose der LORA";
			int endFirst = text.indexOf(first);
			text = text.substring(0, endFirst);
		}
		if (text.contains("Tabelle 8.1 ACR-Kriterien zur Klassiﬁkation der Wegener-Granulomatose [9]")) {
			String first = "Tabelle 8.1 ACR-Kriterien zur Klassiﬁkation der Wegener-Granulomatose [9]";
			int endFirst = text.indexOf(first);
			int startSecond = text.indexOf("Besserung der klinischen Symptome bei täglicher Gabe von 25 mg Prednisolon", endFirst);
			text = text.substring(0, endFirst) + text.substring(startSecond);
		}
		if (text.contains("Sulfasalazin ist eine Substanz, die als Basistherapeutikum zur Behandlung leichterer")) {
			String first = "Sulfasalazin ist eine Substanz, die als Basistherapeutikum zur Behandlung leichterer";
			int endFirst = text.indexOf(first);
			text = text.substring(0, endFirst);
		}

		text = text.replaceAll("2 Die rheumatoide Arthritis", "");
		text = text.replaceAll("3 Spondyloarthritiden", "");
		text = text.replaceAll("4 Kollagenosen", "");
		text = text.replaceAll("15 Medikamentöse Therapie entzündlich-rheumatischer Erkrankungen", "");
		text = text.replaceAll("16 Erkennen und Umgang von/mit Medikamentennebenwirkungen", "");
		text = text.replaceAll("\uFFFD(.?)+", "");
		text = text.replaceAll("^\\d\\p{Z}.*", "");
		text = text.replaceAll("\uFFFD?\\d+\\p{Z}?\n", "");
		text = text.replaceAll("\\d+\\p{Z}?$", "");
		text = text.replaceAll("\nLiteratur\n", "\n");
		text = text.replaceAll(" \\. \\. \\.", ".");
		text = text.replaceAll(" \\[9\\]", "");
		text = text.replaceAll(" \\[81, 88\\]", "");

		text = NormalizationUtils.correctBullets(text);
		text = NormalizationUtils.normalizeNewlines(text);
		text = text.replaceAll("\n", " ");
		text = LanguageTools.removeHyphenNew(text);

		return text;
	}

	private static TextDocument createTextDocument(int index, String title, String text, String type, BookProperties bookProperties)
	{
		TextDocument textDocument = new TextDocument();

		if ((57 <= index) && (index <= 62)) {
			textDocument.setHeading("Sulfasalazin (SSZ)");
		} else {
			textDocument.setHeading(title);
		}

		textDocument.setSource(
			bookProperties.getTitle() + " " +
			bookProperties.getEditorAuthor() + " " +
			bookProperties.getYear() + " " +
			bookProperties.getPublisher() + " " +
			bookProperties.getDoi()
		);
		textDocument.setIdLong(bookProperties.getSourceShort() + "-" + (index + 1));
		textDocument.setText(text);
		textDocument.setDocumentType(type);
		textDocument.topics.addAll(bookProperties.topics);

		return textDocument;
	}

	public static boolean validateText(String plainText)
	{
		return plainText.contains("978-3-211-99712-3 1");
	}
}
