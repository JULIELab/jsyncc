package de.julielab.jsyncc.readbooks.casereports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.apache.commons.lang3.exception.ContextedException;

import de.julielab.jsyncc.readbooks.BookReader;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.ExtractionUtils;
import de.julielab.jsyncc.tools.LanguageTools;

public class CasesInternalMedicine {

	private static final int ID = 10;
	private static final String SOURCE = BookReader.yaml.getSourceById(ID);
	private static final String SOURCE_SHORT = BookReader.yaml.getSourceShortById(ID);

	public static final String BOOK = "books/10-Fallbuch-Innere-Medizin";
	public static final String TYPE = "CaseReport";
	public static final String TOPIC = "Innere Medizin";

	public static final String DOC_PAGE_ONE = "b-0037-142745.pdf";
	public static final String DOC_CONTENT = "b-0037-142744.pdf";

	public static Map<Integer, String> getDiagnoisInformation() {

		Map<Integer, String> diagnosis = new TreeMap<>();

		String element = "";
		try {
			element = ExtractionUtils.getContentByTika(BOOK + "/" + DOC_CONTENT);
		} catch (ContextedException e) {
			e.printStackTrace();
		}
		String[] lines = element.split("\\n");

		boolean diag = false;

		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("Verzeichnis nach Diagnosen")) {
				diag = true;
			}

			if (lines[i].startsWith("Inhaltsverzeichnis nach Themen")) {
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

				diagnosis.put(Integer.parseInt(f[0]), lines[i]);
			}
		}
		return diagnosis;
	}

	public static Map<Integer, ArrayList<String>> getThemesInformation() {
		Map<Integer, ArrayList<String>> themes = new TreeMap<>();

		String element = "";
		try {
			element = ExtractionUtils.getContentByTika(BOOK + "/" + DOC_CONTENT);
		} catch (ContextedException e) {
			e.printStackTrace();
		}
		String[] lines = element.split("\\n");

		boolean them = false;
		String curTheme = "";

		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("Inhaltsverzeichnis nach Themen")) {
				them = true;
			}

			if ((them) && (lines[i].length() > 4) && (!(lines[i].matches(" U = Schwieriger Fall")))) {
				if (!(lines[i].startsWith("Fall"))) {
					curTheme = lines[i];
				} else {
					lines[i] = lines[i].replaceFirst("Fall ", "");
					String[] f = lines[i].split(" ");
					String[] temoTheme = curTheme.split("/");
					ArrayList<String> currentTheme = new ArrayList<String>();

					for (int j = 0; j < temoTheme.length; j++) {
						currentTheme.add(temoTheme[j]);
					}
					themes.put(Integer.parseInt(f[0]), currentTheme);
				}
			}
		}
		return themes;
	}

	public static List<TextDocument> extractContent() throws IOException, InterruptedException {
		List<TextDocument> listDocuments = new ArrayList<>();

		Map<Integer, String> diagnosis = getDiagnoisInformation();
		Map<Integer, ArrayList<String>> themes = getThemesInformation();

		Stream<Path> list = Files.walk(Paths.get(BOOK)).filter(Files::isRegularFile)
				.filter(f -> f.toString().endsWith(".pdf")).sorted();

		int index = 0;
		boolean read = false;

		for (Iterator<Path> iterator = list.iterator(); iterator.hasNext();) {
			String element = iterator.next().toString();

			if ((read) && (index < 150)) {
				index++;
				listDocuments.add(createTextDocument(element, index, diagnosis, themes));
			}

			if (element.toString().endsWith(DOC_PAGE_ONE)) {
				index++;
				listDocuments.add(createTextDocument(element, index, diagnosis, themes));
				read = true;
			}
		}

		return listDocuments;
	}

	public static TextDocument createTextDocument
	(
			String file,
			int index,
			Map<Integer, String> diagnosis,
			Map<Integer, ArrayList<String>> themes
		) throws IOException, InterruptedException
	{
		String element = "";
		try {
			element = ExtractionUtils.getContentByTika(file);
		} catch (ContextedException e) {
			e.printStackTrace();
		}

		String[] lines = element.split("\\n");
		boolean readText = false;

		String text = "";

		for (int i = 0; i < lines.length; i++)
		{
			if ((readText) && ((lines[i].matches("\\s?\\d+\\.1  ? .*")) || (lines[i].startsWith("Abb. "))))
			{
				readText = false;
			}

			if ( (readText)
					&& (!((lines[i].matches("Fr")) || (lines[i].matches("ag")) || (lines[i].matches("en"))
							|| (lines[i].matches("a b")) || (lines[i].startsWith("Antworten und Kommentar auf Seite"))))
					&& (lines[i].length() > 3))
			{
				text = text + "\n"+ lines[i];
			}

			if (lines[i].matches("Fall \\d+"))
			{
				readText = true;
			}
		}

		text = text.replaceFirst("\n", "");
		String[] lin = text.split("\n");
		String heading = "";
		String currentText = "";

		for (int i = 0; i < lin.length; i++)
		{
			if (i == 0)
			{
				heading = lin[0];
			}
			if (i == 1)
			{
				if (	(index == 10) || (index == 30) || (index == 54) || (index == 66) || (index == 88) || (index == 104)
						|| (index == 108) || (index == 124) || (index == 148)
				)
				{
					heading = heading + " " + lin[1];
				}
				else
				{
					currentText = currentText + " " + lin[i];
				}
			}

			if (i > 1)
			{
				currentText = currentText + " " + lin[i];
			}
		}

		heading = LanguageTools.removeHyphenNew(heading);

		if (currentText.startsWith(" ")) {
			currentText = currentText.replaceFirst(" ", "");
		}

		currentText = currentText.replaceAll(" +", " ");
		currentText = LanguageTools.removeHyphenNew(currentText);

		if (heading.endsWith(" ")) {
			heading = heading.substring(0, heading.length() - 1);
		}

		TextDocument document = new TextDocument();
		document.setHeading(heading);
		document.setText(currentText);
		document.setType(TYPE);
		document.setSource(SOURCE);

		document.topic.add(TOPIC);
		document.topic.add(diagnosis.get(index));
		document.topic.addAll(themes.get(index));

		document.setIdLong(SOURCE_SHORT + "-" + index);

		return document;
	}
}