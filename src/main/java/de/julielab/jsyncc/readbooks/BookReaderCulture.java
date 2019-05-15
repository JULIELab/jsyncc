package de.julielab.jsyncc.readbooks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import de.julielab.jsyncc.tools.FileTools;
import de.julielab.jsyncc.tools.LanguageTools;

public class BookReaderCulture {
	public static String BOOK_08 = FileTools.getSinglePDFFileName("src/main/resources/books/08-Patienten-aus-fremden-Kulturen-im-Notarzt-und-Rettungsdienst-Fallbeispiele-und-Praxistipps");
	public static String source = "Machado, C. (2013). Patienten aus fremden Kulturen im Notarzt- und Rettungsdienst: Fallbeispiele und Praxistipps. Springer-Verlag.";
	public static String sourceShort = "Machado2013Culture";

	public static int indexLocal = 1;

	public static ArrayList<TextDocument> ListOfDocuments = new ArrayList<>();
	public static ArrayList<String> tableOfContents = new ArrayList<>();
	public static ArrayList<String> listOfDashs = new ArrayList<>();

	public static ArrayList<TextDocument> extractContent() throws IOException {
		if (BOOK_08 == null) {
			return ListOfDocuments;
		}
		
		boolean readTableOfContents = false;
		boolean readSituationDescription = false;
		int indexOfTableOfContents = 0;
		String actText = "";

		String element = LanguageTools.getContentByTika(BOOK_08);
		Files.write(Paths.get(BOOK_08.replaceAll("pdf", "txt")), element.getBytes());

		String[] lines = element.split("\\n");

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

					if (!(actText.equals(""))) {
						document.text = editOneReport(actText);
						document.heading = tableOfContents.get(indexOfTableOfContents);
						document.source = source;
						document.topic.add("emergency");
						document.id = Integer.toString(BookReader.index);
						BookReader.index++;

						document.idLong = sourceShort + "-" + indexLocal;
						indexLocal++;

						ListOfDocuments.add(document);
						actText = "";
						indexOfTableOfContents++;
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
						&& (content.matches("\\d+\\s[a-zA-Zöäüß]+\\s\\d+\\s+\u00b7\\s+[a-zA-Zöäüß]+(\\s[a-zA-Zöäüß]+)*") == false)
						&& (content.matches("\\s\\.\\sAbb\\.\\s\\d+\\.\\d\\s[a-zA-Zöäüß().]+\\.*(\\s+[a-zA-Zöäüß().]+\\.*)*\\s*") == false)
						&& (content.matches("") == false)) {
					if (content.startsWith(" ")) {
						content = content.replaceFirst(" ", "");
					}

					actText = actText + content + "\n";
				}
			}
		}

		TextDocument document = new TextDocument();

		document.text = editOneReport(actText);
		document.heading = tableOfContents.get(indexOfTableOfContents);
		document.source = source;
		document.type = "case description";
		document.topic.add("emergency");
		document.id = Integer.toString(BookReader.index);
		BookReader.index++;

		document.idLong = sourceShort + "-" + indexLocal;
		indexLocal++;

		ListOfDocuments.add(document);
		actText = "";
		indexOfTableOfContents++;

		return ListOfDocuments;
	}

	public static String editOneReport(String element) {
		element = element.replaceAll("\\(\\.\\sAbb\\.\\s\\d\\.\\d\\)", "");
		element = element.replaceAll("»", "\"");
		element = element.replaceAll("«", "\"");

		// this step is for the jtbd-tokenizer
		element = element.replaceAll("„", "\"");
		element = element.replaceAll("“", "\"");
		element = element.replaceAll("\u2013", "-"); // En dash −
		element = element.replaceAll("\u2212", "-"); // Minus −

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

		text = text.replaceAll("\u0020\u0020", "\u0020");

		if (text.startsWith("\u0020")) {
			text = text.replaceFirst("\u0020", "");
		}

		text = text.replaceAll("Angehö -", "Angehö-"); // mismatch in pdf of
														// book
		text = LanguageTools.removeHyphenNew(text);

		return text;
	}
}
