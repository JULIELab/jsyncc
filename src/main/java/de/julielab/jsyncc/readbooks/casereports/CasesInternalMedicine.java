package de.julielab.jsyncc.readbooks.casereports;

import java.io.File;
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

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.ExtractionUtils;
import de.julielab.jsyncc.tools.LanguageTools;

public class CasesInternalMedicine
{
	public static Map<Integer, String> getDiagnoisInformation(BookProperties bookProperties)
	{
		Map<Integer, String> diagnosis = new TreeMap<>();

		String element = "";
		try {
			element = ExtractionUtils.getContentByTika(bookProperties.bookPath + File.separator + "b-0037-142744.pdf");
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

	public static Map<Integer, ArrayList<String>> getThemesInformation(BookProperties bookProperties) {
		Map<Integer, ArrayList<String>> themes = new TreeMap<>();

		String element = "";
		try {
			element = ExtractionUtils.getContentByTika(bookProperties.bookPath + "/" + "b-0037-142744.pdf");
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

	public static List<TextDocument> extractContent(BookProperties bookProperties) throws IOException, InterruptedException
	{
		List<TextDocument> textDocuments = new ArrayList<>();

		Map<Integer, String> diagnosis = getDiagnoisInformation(bookProperties);
		Map<Integer, ArrayList<String>> themes = getThemesInformation(bookProperties);

		Stream<Path> list = Files.walk(Paths.get(bookProperties.bookPath)).filter(Files::isRegularFile).filter(f -> f.toString().endsWith(".pdf")).sorted();

		int index = 0;
		boolean read = false;

		for (Iterator<Path> iterator = list.iterator(); iterator.hasNext();)
		{
			String element = iterator.next().toString();

			if ((read) && (index < 150))
			{
				index++;
				textDocuments.add(createTextDocument(element, index, diagnosis, themes, bookProperties));
			}

			if (element.toString().endsWith("b-0037-142745.pdf")) {
				index++;
				textDocuments.add(createTextDocument(element, index, diagnosis, themes, bookProperties));
				read = true;
			}
		}

		return textDocuments;
	}

	public static TextDocument createTextDocument
	(
			String file,
			int index,
			Map<Integer, String> diagnosis,
			Map<Integer, ArrayList<String>> themes,
			BookProperties bookProperties
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

		TextDocument textDocument = new TextDocument();
		textDocument.setHeading(heading);
		textDocument.setText(currentText);
		textDocument.setDocumentType(bookProperties.documentType.get(0));
		textDocument.setSource(
				bookProperties.getTitle() + " " +
				bookProperties.getEditorAuthor() + " " +
				bookProperties.getYear() + " " +
				bookProperties.getPublisher() + " " +
				bookProperties.getDoi()
		);

		textDocument.setTopic(bookProperties.topics);
		textDocument.setIdLong(bookProperties.sourceShort + "-" + index);
		textDocument.setSourcShort(bookProperties.sourceShort);
		textDocument.setBookId(bookProperties.bookId);

		return textDocument;
	}
}