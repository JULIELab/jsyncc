package de.julielab.jsyncc.readbooks.casedescriptions;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.ExtractionUtils;
import de.julielab.jsyncc.tools.LanguageTools;

import org.apache.commons.lang3.exception.ContextedException;
import java.util.ArrayList;
import java.util.List;

public class CasesCulture
{
	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{
		List<TextDocument> textDocuments = new ArrayList<>();
		ArrayList<String> tableOfContents = new ArrayList<>();

		boolean readTableOfContents = false;
		boolean readSituationDescription = false;
		String text = "";
		int index = 1;

		String plainText = ExtractionUtils.getContentByTika(bookProperties.getBookPath());
		String[] lines = plainText.split("\\n");

		for (int i = 0; i < lines.length; i++) {
			if (lines[i].equals("Inhaltsverzeichnis")) {
				readTableOfContents = true;
			} else if (lines[i].equals("Serviceteil")) {
				readTableOfContents = false;
			}

			if (readTableOfContents && (lines[i].matches("\\d+.*"))) {
				String content = lines[i];
				boolean firstCapital = false;

				if ((content.startsWith("1")) && (!content.startsWith("10")) && (!content.startsWith("11"))
						&& (!content.startsWith("12"))) {
					firstCapital = true;
				}

				if (!firstCapital) {
					content = content.replaceAll("\\d+\\s", "");
					content = content.replaceAll("\\d", "");
					content = content.replaceAll("\\.", "");
					content = content.replaceAll("\\A+(\u0020|\u0020\u0020)", "");
					content = content.replaceAll("(\u0020|\u3000)+\\z", "");

					tableOfContents.add(content);
				}
			}

			if (!readTableOfContents) {
				// readSituationDescription
				if (lines[i].endsWith(".1 Situationsbeschreibung")
						&& (!(lines[i + 2].endsWith("Fremdkulturelle und religiöse Beobachtungen"))
								&& !(lines[i + 2].endsWith("Hintergrundinformationen und Handlungsoptionen")))) {
					TextDocument textDocument = new TextDocument();

					if (!(text.equals(""))) {
						textDocument.setText(cleanText(text));
						textDocument.setHeading(tableOfContents.get(index));
						textDocument.setSource(
							bookProperties.getTitle() + " " +
							bookProperties.getEditorAuthor() + " " +
							bookProperties.getYear() + " " +
							bookProperties.getPublisher() + " " +
							bookProperties.getDoi()
						);
						textDocument.topics.add(bookProperties.topics.get(0));
						textDocument.setDocumentType(bookProperties.documentType.get(0));
						textDocument.setIdLong(bookProperties.getSourceShort() + "-" + index);
						textDocument.setSourcShort(bookProperties.sourceShort);
						textDocument.setBookId(bookProperties.bookId);

						index++;
						textDocuments.add(textDocument);
						text = "";
					}

					readSituationDescription = true;

				}

				else if (lines[i].startsWith("\u0020?"))
				{
					readSituationDescription = false;
				}
			}

			if (readSituationDescription) {
				String content = lines[i];

				if (content.startsWith("?") || content.startsWith("\u0020?")) {
					if (content.endsWith("?")) {
						content = "";
					}
				}

				if ((content.matches("\\A+\\d+\u0020Kapitel") == false)
						&& (content.matches("\\d+\\.1\\sSituationsbeschreibung") == false)
						&& (content.matches("\\d+\\.\\d+\\s\u00b7\\s[a-zA-Zöäüß]+(\\s[a-zA-Zöäüß]+)*") == false)
						&& (content.matches(
								"\\d+\\s[a-zA-Zöäüß]+\\s\\d+\\s+\u00b7\\s+[a-zA-Zöäüß]+(\\s[a-zA-Zöäüß]+)*") == false)
						&& (content.matches(
								"\\s\\.\\sAbb\\.\\s\\d+\\.\\d\\s[a-zA-Zöäüß().]+\\.*(\\s+[a-zA-Zöäüß().]+\\.*)*\\s*") == false)
						&& (content.matches("") == false)) {
					if (content.startsWith(" ")) {
						content = content.replaceFirst(" ", "");
					}

					text = text + content + "\n";
				}
			}
		}

		TextDocument textDocument = new TextDocument();

		textDocument.setText(cleanText(text));
		textDocument.setHeading(tableOfContents.get(index - 1));
		textDocument.setSource(
			bookProperties.getTitle() + " " +
			bookProperties.getEditorAuthor() + " " +
			bookProperties.getYear() + " " +
			bookProperties.getPublisher() + " " +
			bookProperties.getDoi()
		);
		textDocument.setDocumentType(bookProperties.documentType.get(0));
		textDocument.topics.add(bookProperties.topics.get(0));
		textDocument.setIdLong(bookProperties.getSourceShort() + "-" + index);

		textDocument.setSourcShort(bookProperties.sourceShort);
		textDocument.setBookId(bookProperties.bookId);

		index++;

		textDocuments.add(textDocument);
		text = "";

		return textDocuments;
	}

	public static String cleanText(String element) {
		element = element.replaceAll("\\(\\.\\sAbb\\.\\s\\d\\.\\d\\)", "");

		String[] e = element.split("\n");

		String text = "";

		for (int i = 0; i < e.length; i++) {
			if (e[i].startsWith("4\u0020")) {
				e[i] = e[i].replaceFirst("4", "-");

				if (text.endsWith("\n")) {
					text = text + e[i] + "\n";
				} else {
					text = text + "\n" + e[i] + "\n";
				}
			} else {
				if (text.endsWith("\n")) {
					text = text + e[i];
				} else {
					text = text + "\u0020" + e[i];
				}

			}
		}

		text = text.replaceAll("\u00A0", " ");
		text = text.replaceAll("\u0020\u0020", "\u0020");

		if (text.startsWith("\u0020")) {
			text = text.replaceFirst("\u0020", "");
		}

		text = text.replaceAll("Angehö -", "Angehö-");
		// mismatch in pdf of book

		text = LanguageTools.removeHyphenNew(text);

		return text;
	}

	/*
	@Override
	public String parseBook(Path pdfPath) throws ContextedException
	{
		String plainText = ExtractionUtils.getContentByTika(pdfPath);
		try {
			Files.write(Paths.get(pdfPath.toString().replaceAll("pdf", "txt")), plainText.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return plainText;
	}*/

	public boolean validateText(String plainText)
	{
		return plainText.contains("978-3-642-34869-3");
	}
}