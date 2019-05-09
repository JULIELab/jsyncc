package de.julielab.jsyncc.readbooks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.apache.commons.codec.digest.DigestUtils;

import de.julielab.jsyncc.tools.LanguageTools;

public class BookReaderCasesInternalMedicine {
	public static String BOOK_10 = "src/main/resources/books/10-Fallbuch-Innere-Medizin";
	public static String source = "Hellmich, B. (2017). Fallbuch Innere Medizin. Georg Thieme Verlag, 5., vollständig überarbeitete Auflage.";
	public static String sourceShort = "Hellmich2017CasesInternalMedicine";

	public static int indexLocal = 1;

	public static ArrayList<String> listCaseFiles = new ArrayList<String>();

	public static ArrayList<TextDocument> listDocuments = new ArrayList<TextDocument>();
	public static ArrayList<CheckSum> listCheckSum = new ArrayList<CheckSum>();

	public static TreeMap<Integer, String> tableOfDiagnois = new TreeMap<Integer, String>();
	public static TreeMap<Integer, ArrayList<String>> tableOfThemes = new TreeMap<Integer, ArrayList<String>>();

	public static String docPageOne = "b-0037-142745.pdf";
	public static String docContent = "b-0037-142744.pdf";

	public static void readTableOfContents() {
		String contentFile = BOOK_10 + "/" + docContent;
		if (!new File(contentFile).exists()) {
			System.err.println(contentFile + " does not exist.");
			return;
		}
		String element = LanguageTools.getContentByTika(contentFile);
		String[] lines = element.split("\\n");

		boolean diag = false;
		boolean them = false;
		String actTheme = "";

		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("Verzeichnis nach Diagnosen")) {
				diag = true;
			}

			if (lines[i].startsWith("Inhaltsverzeichnis nach Themen")) {
				them = true;
				diag = false;
			}

			if ((diag) && (lines[i].length() > 4) && (!(lines[i].matches(" U = Schwieriger Fall")))
					&& (!(lines[i].startsWith("Verzeichnis")))) {
				lines[i] = lines[i].replaceFirst("Fall ", "");
				String[] f = lines[i].split(" ");
				lines[i] = lines[i].replaceAll("\\.", "");
				lines[i] = lines[i].replaceAll("\\d+", "");
				lines[i] = lines[i].replaceAll("\\s\\s+", "");

				if (lines[i].startsWith(" ")) {
					lines[i] = lines[i].replaceFirst(" ", "");
				}

				tableOfDiagnois.put(Integer.parseInt(f[0]), lines[i]);
			}

			if ((them) && (lines[i].length() > 4) && (!(lines[i].matches(" U = Schwieriger Fall")))) {
				if (!(lines[i].startsWith("Fall"))) {
					actTheme = lines[i];
				} else {
					lines[i] = lines[i].replaceFirst("Fall ", "");

					String[] f = lines[i].split(" ");
					String[] a = actTheme.split("/");
					ArrayList<String> aT = new ArrayList<String>();

					for (int j = 0; j < a.length; j++) {
						aT.add(a[j]);
					}
					tableOfThemes.put(Integer.parseInt(f[0]), aT);
				}
			}
		}
	}

	public static ArrayList<TextDocument> extractContent() throws IOException, InterruptedException {
		readTableOfContents();

		Stream<Path> list = Files.walk(Paths.get(BOOK_10)).filter(Files::isRegularFile)
				.filter(f -> f.toString().endsWith(".pdf")).sorted();

		int i = 0;
		boolean read = false;

		for (Iterator<Path> iterator = list.iterator(); iterator.hasNext();) {
			String element = iterator.next().toString();

			if ((read) && (i < 150)) {
				i++;
				getOneElement(element);
			}

			if (element.toString().endsWith(docPageOne)) {
				i++;
				getOneElement(element);
				read = true;
			}
		}

		return listDocuments;
	}

	public static void getOneElement(String file) throws IOException, InterruptedException {
		String element = LanguageTools.getContentByTika(file);

		String[] lines = element.split("\\n");
		boolean readText = false;

		String text = "";

		for (int i = 0; i < lines.length; i++) {
			if ((readText) && ((lines[i].matches("\\s?\\d+\\.1  ? .*")) || (lines[i].startsWith("Abb. ")))) {
				readText = false;
			}

			if ((readText)
					&& (!((lines[i].matches("Fr")) || (lines[i].matches("ag")) || (lines[i].matches("en"))
							|| (lines[i].matches("a b")) || (lines[i].startsWith("Antworten und Kommentar auf Seite"))))
					&& (lines[i].length() > 3)) {
				text = text + lines[i] + "\n";
			}

			if (lines[i].matches("Fall \\d+")) {
				readText = true;
			}
		}

		text.replaceFirst("\n", "");
		String[] t = text.split("\n");

		String heading = "";
		String act = "";

		for (int j = 0; j < t.length; j++) {
			if (j == 0) {
				heading = t[0];
			} else if ((j == 1) && (t[1].startsWith(" "))) {
				heading = heading + t[1];
			} else {
				act = act + " " + t[j];
			}
		}

		act = act.replaceAll(heading, "");

		if (act.startsWith(" ")) {
			act = act.replaceFirst(" ", "");
		}

		act = act.replaceAll(" +", " ");

		act = LanguageTools.removeHyphenNew(act);

		// this step is for the jtbd-tokenizer
		act = act.replaceAll("»", "\"");
		act = act.replaceAll("«", "\"");
		act = act.replaceAll("„", "\"");
		act = act.replaceAll("“", "\"");
		act = act.replaceAll("\u2013", "-"); // En dash −
		act = act.replaceAll("\u2212", "-"); // Minus −

		if (heading.endsWith(" ")) {
			heading = heading.substring(0, heading.length() - 1);
		}

		TextDocument document = new TextDocument();
		document.heading = heading;
		document.text = act;
		document.topic.add(tableOfDiagnois.get(indexLocal));
		document.topic.addAll(tableOfThemes.get(indexLocal));
		document.type = "case description";
		document.source = source;

		BookReader.index++;
		document.id = Integer.toString(BookReader.index);

		document.idLong = sourceShort + "-" + indexLocal;
		indexLocal++;

		listDocuments.add(document);

		CheckSum checkSum = new CheckSum();
		checkSum.checkSumText = DigestUtils.md5Hex(text);
		checkSum.id = Integer.toString(BookReader.index);
		BookReader.listCheckSum.add(checkSum);
	}
}
