package de.julielab.jsyncc.readbooks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Stream;

import de.julielab.jsyncc.tools.LanguageTools;

public class BookReaderCasesSurgery {
	public static String BOOK_6 = "src/main/resources/books/06-Fallbuch-Chirurgie";
	public static String source = "Eisoldt, S. (2017). Fallbuch Chirurgie: 140 Fälle aktiv bearbeiten. Georg Thieme Verlag, 5. unveränderte edition.";
	public static String sourceShort = "Eisoldt2017CasesSurgery";

	public static int indexLocal = 1;

	public static String tableOfContentsFile = "src/main/resources/books/06-Fallbuch-Chirurgie/b-0036-141230.pdf";

	public static ArrayList<TextDocument> listDocuments = new ArrayList<TextDocument>();
	public static ArrayList<String> tableOfContents = new ArrayList<String>();

	public static HashMap<Integer, String> tableOfTopics = new HashMap<Integer, String>();
	public static HashMap<Integer, String> tableOfAnswers = new HashMap<Integer, String>();

	public static ArrayList<TextDocument> extractContent() throws IOException, InterruptedException {
		if (!new File(tableOfContentsFile).exists()) {
			System.err.println(tableOfContentsFile + " does not exist");
			return listDocuments;
		}
		readTableOfContents(tableOfContentsFile);
		readTableOfTopics(tableOfContentsFile);
		readTableOfAnswers(tableOfContentsFile);

		Stream<Path> list = Files.walk(Paths.get(BOOK_6)).filter(Files::isRegularFile).sorted()
				.filter(f -> f.toString().endsWith(".pdf"));

		for (Iterator<Path> iterator = list.iterator(); iterator.hasNext();) {
			File element = iterator.next().toFile();

			// exclude the file with different tables of content
			if (!(element.toString().endsWith("b-0036-141230.pdf"))) {
				getOneElement(element.toString());
			}
		}

		return listDocuments;
	}

	public static void readTableOfContents(String file) throws InterruptedException, IOException {
		String element = LanguageTools.getContentByTika(file.toString());
		String[] lines = element.split("\n");
		boolean tabContents = false;
		String temp = "";
		int index = 0;

		for (int i = 0; i < lines.length; i++) {
			if ((lines[i].equals("Inhaltsverzeichnis")) && (lines[i + 2].equals("Chirurgische Infektionen"))) {
				tabContents = false;

				// last element
				temp = temp.replaceAll(" Fall \\d+ ", "");
				temp = temp.replaceAll("\\.", "");
				temp = temp.replaceAll("\\s\\d+", "");
				temp = LanguageTools.removeHyphenNew(temp);
				tableOfContents.add(temp);
				temp = "";
				index = index + 1;
			}

			if ((tabContents) && (3 < lines[i].length()) && (!(lines[i].contains("Inhaltsverzeichnis nach Fällen")))
					&& (index <= 140)) {
				if (lines[i].startsWith("Fall")) {
					temp = temp.replaceAll(" Fall \\d+ ", "");
					temp = temp.replaceAll("\\.", "");
					temp = temp.replaceAll("\\s\\d+", "");
					temp = LanguageTools.removeHyphenNew(temp);
					tableOfContents.add(temp);
					temp = "";
					index = index + 1;
				}

				temp = temp + " " + lines[i];
			}

			if ((lines[i].equals("Inhaltsverzeichnis")) && (lines[i + 1].startsWith("Fall 1 20"))) {
				tabContents = true;
			}
		}
	}

	public static void readTableOfTopics(String file) throws InterruptedException, IOException {
		String element = LanguageTools.getContentByTika(file.toString());
		String[] lines = element.split("\n");
		boolean tabContents = false;
		String temp = "";
		String actTopic = "";
		int index = 0;

		for (int i = 0; i < lines.length; i++) {
			if ((lines[i].startsWith("Inhaltsverzeichnis nach Themen"))
					&& (lines[i + 1].equals("Inhaltsverzeichnis"))) {
				tabContents = false;
			}

			if ((tabContents) && (3 < lines[i].length()) && (index < 140)) {
				if (lines[i].startsWith("Fall")) {
					index = index + 1;

					String actCase = lines[i].replaceAll("\\.", "");
					actCase = actCase.replaceAll("Fall ", "");
					actCase = actCase.replaceAll("\\s\\s+\\d+", "");

					tableOfTopics.put(Integer.parseInt(actCase), actTopic);
				} else {
					actTopic = LanguageTools.removeHyphenNew(lines[i]).replaceAll("\\n", "");
				}

				temp = temp + " " + lines[i];
			}

			if ((lines[i].equals("Inhaltsverzeichnis")) && (lines[i + 2].startsWith("Chirurgische Infektionen"))) {
				tabContents = true;
			}
		}
	}

	public static void readTableOfAnswers(String file) throws InterruptedException, IOException {
		String element = LanguageTools.getContentByTika(file.toString());
		String[] lines = element.split("\n");
		boolean tabContents = false;
		String temp = "";
		int index = 0;

		for (int i = 0; i < lines.length; i++) {
			if ((lines[i].startsWith("Anhang"))) {
				tabContents = false;

			}

			if ((tabContents) && (3 < lines[i].length()) && (index < 140)) {
				if (lines[i].startsWith("Fall")) {
					index = index + 1;

					String actCase = lines[i];

					if (!(lines[i].contains("."))) {
						actCase = actCase + " " + lines[i + 2];
					}

					actCase = lines[i].replaceAll("\\.", "");
					actCase = actCase.replaceAll("Fall \\d+ ", "");
					actCase = actCase.replaceAll("\\s\\s+\\d+", "");

					actCase = LanguageTools.removeHyphenNew(actCase).replaceAll("\\n", "");

					// Normalize En dash – \\u2013
					actCase = actCase.replaceAll("\\s\\u2013", " \u2013");
					actCase = actCase.replaceAll("\\u2013\\s", "\u2013 ");
					actCase = actCase.replaceAll("\\u2013", " - ");

					tableOfAnswers.put(index, actCase);
				}

				temp = temp + " " + lines[i];
			}

			if ((lines[i].equals("Inhaltsverzeichnis")) && (lines[i + 1].startsWith("Fall 1 Beckenfraktur"))) {
				tabContents = true;
			}
		}
	}

	public static void getOneElement(String file) throws IOException, InterruptedException {
		String element = LanguageTools.getContentByTika(file.toString());
		String[] lines = element.split("\\n");
		boolean readText = true;
		String actText = "";
		int caseNumber = 0;

		for (int i = 0; i < lines.length; i++) {
			if (2 < lines[i].length()) {
				if (lines[i].matches("\\d+\\.\\d .*")) {
					readText = false;
				}

				if (readText) {
					if ((lines[i].contains("Abb.")) && (!(lines[i].contains("\u25B6")))) {
						String[] temp = lines[i].split("Abb\\.");
						actText = actText + " " + temp[0];
						readText = false;
					} else {
						actText = actText + " " + lines[i];
					}
				}

				if (lines[i].startsWith("Fall")) {
					caseNumber = Integer.parseInt(lines[i].replaceAll("Fall ", ""));
				}
			}
		}

		actText = LanguageTools.removeHyphenNew(actText);

		// remove expressions like "(s. ▶Abb. 132.1)" and "(siehe ▶Abb. 46.1)"
		actText = actText.replaceAll("\\s\\((s\\.|siehe)\\s\\u25B6Abb\\.\\s\\d+\\.\\d\\)", "");
		actText = actText.replaceAll("\\s\\(\\u25B6Abb\\.\\s\\d+\\.\\d\\)", "");
		actText = actText.replaceAll("▶", "");

		String head = tableOfContents.get(caseNumber);
		actText = actText.substring(head.length());

		if (actText.startsWith(" ")) {
			actText = actText.replaceFirst(" ", "");
		}

		// There is an error befor this part and the following line is a very
		// quick and dirty solution.
		// String[] temp = actText.split(" ");

		actText = actText.replaceAll("\\A[A-Za-zÖÄÜßöäü]* ", "");

		// this step is for the jtbd-tokenizer
		actText = actText.replaceAll("»", "\"");
		actText = actText.replaceAll("«", "\"");
		actText = actText.replaceAll("„", "\"");
		actText = actText.replaceAll("“", "\"");
		actText = actText.replaceAll("\u2013", "-"); // En dash −
		actText = actText.replaceAll("\u2212", "-"); // Minus −

		TextDocument document = new TextDocument();
		document.heading = head;
		document.text = actText;
		document.topic.add(tableOfTopics.get(caseNumber));
		document.topic.add(tableOfAnswers.get(caseNumber));
		document.type = "case example";
		document.source = source;

		BookReader.index++;
		document.id = Integer.toString(BookReader.index);

		document.idLong = sourceShort + "-" + indexLocal;
		indexLocal++;

		listDocuments.add(document);
	}
}
