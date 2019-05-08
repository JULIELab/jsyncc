package de.julielab.jsyncc.readbooks;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;

import de.julielab.jsyncc.tools.FileTools;
import de.julielab.jsyncc.tools.LanguageTools;

public class BookReaderCasesAnesthetics {
	public static String BOOK_7 = FileTools.getSinglePDFFileName("src/main/resources/books/07-Komplikationen-in-der-Anaesthesie");
	public static String source = "Hübler, M. and Koch, T. (2014). Komplikationen in der Anästhesie. Springer, 3., überarb. u. erw. aufl. edition.";
	public static String sourceShort = "Huebler2014ComplicationAnesthetics";

	public static int indexLocal = 1;

	public static ArrayList<TextDocument> ListOfDocuments = new ArrayList<TextDocument>();

	public static ArrayList<String> tableOfContents = new ArrayList<String>();
	public static ArrayList<ArrayList<String>> tableOfTopics = new ArrayList<ArrayList<String>>();
	public static ArrayList<String> tableOfAuthors = new ArrayList<String>();

	public static ArrayList<TextDocument> extractContent() throws IOException, InterruptedException {
		if (BOOK_7 == null) {
			return ListOfDocuments;
		}
		String content = LanguageTools.getContentByTika(BOOK_7);

		String[] lines = content.split("\n");

		boolean readTableOfContents = false;
		boolean readAuthors = false;

		boolean readFirstPart = false;

		String actTopics = "";
		String text = "";

		for (int i = 0; i < lines.length; i++) {
			if (readAuthors) {
				if (lines[i].matches("\\d+  Fall.*")) {
					tableOfAuthors.add(lines[i + 1]);
				}
			}

			if (lines[i].matches("Inhaltsverzeichnis")) {
				readAuthors = true;
			}

			if (tableOfAuthors.size() == 35) {
				readAuthors = false;
			}

			if (readTableOfContents) {
				if (lines[i].startsWith(" z Fall")) {
					String actHeading = lines[i].replaceAll(" z Fall\\u00A0?\\s?\\d+\\s\\u2013?\\s", "");

					tableOfContents.add(actHeading);

					if (!(actTopics.equals(""))) {
						String[] temp = actTopics.split("–");
						ArrayList<String> tempList = new ArrayList<String>();

						for (int j = 0; j < temp.length; j++) {
							temp[j] = temp[j].replaceAll("  ", " ");
							tempList.add(temp[j]);
						}

						tableOfTopics.add(tempList);
					}

					actTopics = "";
				} else {
					if ((!(lines[i].equals(""))) && (!(lines[i].contains("Übersicht der Fallbeispiele")))
							&& (!(lines[i].matches("XV"))) && (!(lines[i].contains("Inhalt der Fallbeispiele")))) {
						actTopics = actTopics + " " + lines[i];
					}
				}
			}

			if (lines[i].startsWith("Übersicht der Fallbeispiele")) {
				readTableOfContents = true;
			}

			if ((lines[i].startsWith("Inhalt der Fallbeispiele")) && readTableOfContents) {
				readTableOfContents = false;

				String[] temp = actTopics.split(" – ");
				ArrayList<String> tempList = new ArrayList<String>();

				for (int j = 0; j < temp.length; j++) {
					temp[j] = temp[j].replaceAll("  ", " ");
					tempList.add(temp[j]);
				}

				tableOfTopics.add(tempList);
			}

			if (lines[i].matches("\\d+\\.1\\.1.*")) {
				readFirstPart = false;

				if (!(text.equals(""))) {
					editOneReport(text);
					text = "";
				}
			}

			if (readFirstPart) {
				if ((!(lines[i].matches("\\d+"))) && (!(lines[i].matches("\\d+\\.\\d\\. .*")))
						&& (!(lines[i].matches("\\d+ \\d")))
						&& (!(lines[i].matches("\\d+.\\d+( |\u00A0)\u2022\u00A0 .*")))
						&& (!(lines[i].matches("\\d+ Kapitel.*")))) {
					text = text + "\n" + lines[i];
				}
			}

			if ((lines[i].startsWith("Was geschah")) || (lines[i].startsWith("…Was geschah"))) {
				readFirstPart = true;
			}
		}

		return ListOfDocuments;
	}

	public static String editOneReport(String element) {
		element = element.replaceAll("«", "\"");
		element = element.replaceAll("»", "\"");
		element = element.replaceAll("\u00A0", "\u0020");

		String[] e = element.split("\n");

		String text = "";

		for (int i = 0; i < e.length; i++) {
			if (e[i].startsWith(" 5 ")) // Paragraoh with Bullet, Codierung ==
										// "5"
			{
				e[i] = e[i].replaceFirst(" 5 ", "- ");

				if (text.endsWith("\n")) {
					text = text + e[i];
				} else {
					text = text + "\n" + e[i];
				}
			} else {
				if (text.equals("")) {
					text = e[i];
				} else {
					if (text.endsWith("\n")) {
						text = text.substring(0, text.length() - 1);
						text = text + "" + e[i];
					} else {
						text = text + " " + e[i];
					}
				}
			}
		}

		text = text.replaceAll("\u0020+", "\u0020");
		text = text.replaceAll("\u00BD", "1/2"); // ½ / 1/2

		text = text.replaceAll("\u2013", "-"); // En dash / –
		text = text.replaceAll("\uFFFD", " "); // Space

		text = text.replaceAll(" ?\\(\\. Abb\\. \\d+\\.\\d+\\)", "");

		// this step is for the jtbd-tokenizer
		text = text.replaceAll("„", "\"");
		text = text.replaceAll("“", "\"");
		text = text.replaceAll("\u2013", "-"); // En dash −
		text = text.replaceAll("\u2212", "-"); // Minus −
		text = LanguageTools.removeHyphenNew(text);

		// remove a special
		// (siehe zu dieser Thematik auch Fall 21, 7 Abschn. 21.1.6 und 7
		// Abschn. 21.1.7 sowie 7 Abb. 21.2)

		if (text.contains("siehe zu dieser Thematik")) {
			String[] t = text.split("siehe zu dieser Thematik");
			text = t[0].substring(0, t[0].length() - 2) + ".";
		}

		TextDocument document = new TextDocument();

		document.text = text;
		document.heading = tableOfContents.get(indexLocal - 1);
		document.topic.add("anaestetics");
		document.source = source;
		document.type = "case description";
		document.id = Integer.toString(BookReader.index);
		BookReader.index++;

		document.idLong = sourceShort + "-" + indexLocal;
		indexLocal++;

		ListOfDocuments.add(document);
		// indexOfTableOfContents++;

		CheckSum checkSum = new CheckSum();
		checkSum.checkSumText = DigestUtils.md5Hex(text);
		checkSum.id = Integer.toString(BookReader.index);
		BookReader.listCheckSum.add(checkSum);

		return text;
	}
}
