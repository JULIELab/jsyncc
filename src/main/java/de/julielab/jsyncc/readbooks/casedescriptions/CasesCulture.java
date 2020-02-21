package de.julielab.jsyncc.readbooks.casedescriptions;

import de.julielab.jsyncc.readbooks.BookExtractor;
import de.julielab.jsyncc.readbooks.BookReader;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.ExtractionUtils;
import de.julielab.jsyncc.tools.LanguageTools;

import org.apache.commons.lang3.exception.ContextedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CasesCulture implements BookExtractor {

	private final int ID = 8;
	private final String SOURCE = BookReader.yaml.getSourceById(ID);
	private final String SOURCE_SHORT = BookReader.yaml.getSourceShortById(ID);

	public static final String BOOK = "books/08-Patienten-aus-fremden-Kulturen-im-Notarzt-und-Rettungsdienst-Fallbeispiele-und-Praxistipps/978-3-642-34869-3.pdf";
	public static final String TYPE = "CaseDescription";
	public static final String TOPIC = "Notfallmedizin";

	public static void main(String[] args) throws IOException, InterruptedException, ContextedException {
		CasesCulture culture = new CasesCulture();
		String plainText = culture.parseBook(Paths.get(BOOK));
		List<TextDocument> listDocuments = culture.extractContent(plainText);
		System.out.println(listDocuments.size());
	}

	@Override
	public List<TextDocument> extractContent(String plainText) {

		List<TextDocument> listDocuments = new ArrayList<>();
		ArrayList<String> tableOfContents = new ArrayList<>();

		boolean readTableOfContents = false;
		boolean readSituationDescription = false;
		String text = "";
		int index = 1;

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
					TextDocument document = new TextDocument();

					if (!(text.equals(""))) {
						document.setText(cleanText(text));
						document.setHeading(tableOfContents.get(index));
						document.setSource(SOURCE);
						document.topic.add(TOPIC);
						document.setType(TYPE);
						document.setIdLong(SOURCE_SHORT + "-" + index);

						index++;

						listDocuments.add(document);
						text = "";
					}

					readSituationDescription = true;

				}

				else if (lines[i].startsWith("\u0020?")) {
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

		TextDocument document = new TextDocument();

		document.setText(cleanText(text));
		document.setHeading(tableOfContents.get(index - 1));
		document.setSource(SOURCE);
		document.setType(TYPE);
		document.topic.add(TOPIC);

		document.setIdLong(SOURCE_SHORT + "-" + index);
		index++;

		listDocuments.add(document);
		text = "";

		return listDocuments;
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

	@Override
	public String parseBook(Path pdfPath) throws ContextedException {
		String plainText = ExtractionUtils.getContentByTika(BOOK);
		try {
			Files.write(Paths.get(BOOK.replaceAll("pdf", "txt")), plainText.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return plainText;
	}

	@Override
	public boolean validateText(String plainText) {
		return plainText.contains("978-3-642-34869-3");
	}
}