package de.julielab.jsyncc.readbooks.operativereports;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.ExtractionUtils;
import de.julielab.jsyncc.tools.LanguageTools;

import org.apache.commons.lang3.exception.ContextedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ReportsOrthopedicsAndAccidentSurgery {

	private static ArrayList<String> listPatIds = new ArrayList<>();

	public static List<TextDocument> extractContent_1(BookProperties bookProperties) throws IOException, InterruptedException, ContextedException {

		List<TextDocument> textDocuments = new ArrayList<TextDocument>();

		ProcessBuilder pb = new ProcessBuilder("pdftotext", bookProperties.bookPath);
		Process p = pb.start();
		p.waitFor();

		textDocuments.addAll(
				extractContentBook_1(
					bookProperties.bookPath.replaceAll("pdf", "txt"),
					bookProperties)
				);

		return textDocuments;
	}

	public static ArrayList<TextDocument> extractContent_2(BookProperties bookProperties) throws IOException, InterruptedException, ContextedException {

		ArrayList<TextDocument> textDocuments = new ArrayList<>();

		String content2 = ExtractionUtils.getContentByTika(bookProperties.bookPath);
		textDocuments.addAll(extractContentBook_23(content2, bookProperties));

		return textDocuments;
	}
	
	public static ArrayList<TextDocument> extractContent_3(BookProperties bookProperties) throws IOException, InterruptedException, ContextedException {

		ArrayList<TextDocument> textDocuments = new ArrayList<>();

		String content3 = ExtractionUtils.getContentByTika(bookProperties.bookPath);
		textDocuments.addAll(extractContentBook_23(content3, bookProperties));

		return textDocuments;
	}
	
	private static ArrayList<TextDocument> extractContentBook_1(String contentFile, BookProperties bookProperties) throws IOException
	{
		ArrayList<TextDocument> textDocuments = new ArrayList<>();

		@SuppressWarnings("resource")
		Stream<String> l = Files.lines(Paths.get(contentFile));
		ArrayList<String> lin = new ArrayList<>();

		for (Iterator<String> iterator = l.iterator(); iterator.hasNext();)
		{
			lin.add(iterator.next());
		}

		String actText = "";
		String actChapter = "";
		String actCase = "";
		String actCase2 = "";
		String actOPtime = "";
		boolean readElements = false;
		boolean readInElements = false;
		int index = 1;

		ArrayList<String> topics = new ArrayList<>();
		topics.add(bookProperties.topics.get(0));

		String currentHeading = "";

		for (int i = 0; i < lin.size(); i++)
		{
			if (lin.get(i).matches("\\d+\\.\\d+\\s.*"))
			{
				if (!(actChapter.equals("")))
				{
					if (!(actChapter.equals(lin.get(i).replaceAll("· ", ""))))
					{
						readInElements = false;
					}
				}
				actChapter = lin.get(i).replaceAll("· ", "");
			}

			if (
					(lin.get(i).matches("\\d+.\\d+\\.\\d+\\s*.*"))
				)
			{
				readInElements = false;
			}

			if ((currentHeading.equals(""))&&(lin.get(i).startsWith("OP-Bericht,")))
			{
				if (!(lin.get(i-1).equals("")))
				{
					currentHeading = lin.get(i-1);
				}
				else if (!(lin.get(i-2).equals("")))
				{
					currentHeading = lin.get(i-2);
				}
				if (currentHeading.endsWith(" "))
				{
					currentHeading = currentHeading.substring(0, currentHeading.length() - 1);
				}
			}

			if (lin.get(i).matches("\\s?Pat\\.-Nr\\..*"))
			{
				readElements = true;
				readInElements = false;

				if (!(actText.equals("")))
				{
					textDocuments.add(cleanText(actText, topics, bookProperties, currentHeading, index));

					listPatIds.add(normalizeTextBook1(actCase + " " + actCase2 + actOPtime));
					index++;
					actText = "";

					if (!(lin.get(i-2)).equals(""))
					{
						currentHeading = lin.get(i-2);

						if (!(lin.get(i-3)).equals(""))
						{
							currentHeading = lin.get(i-3)  + " " + currentHeading;
						}
					}
					else if (!(lin.get(i-3)).equals(""))
					{
						currentHeading = lin.get(i-3);

						if (!(lin.get(i-4)).equals(""))
						{
							currentHeading = lin.get(i-4)  + " " + currentHeading;
						}
					}

					if (currentHeading.endsWith(" "))
					{
						currentHeading = currentHeading.substring(0, currentHeading.length() - 1);
					}
				}

				actCase = lin.get(i);
			}

			if (lin.get(i).startsWith("Fall-Nr"))
			{
				actCase2 = lin.get(i);
			}

			if ((lin.get(i).contains("OP-Datum:")) && (lin.get(i+1).contains("OP-Dauer (Schnitt/Naht):")))
			{
				actOPtime = normalizeActOP(lin.get(i), lin.get(i+1));
			}

			if (
					((lin.get(i).matches("Nachbehandlungs-")) && (lin.get(i).matches("schemata")))
				||
					(lin.get(i).matches("Nachbehandlungsschemata"))
				)
			{
				// last element
				readElements = false;

				if (!(actText.equals("")))
				{
					textDocuments.add(cleanText(actText, topics, bookProperties, currentHeading, index));
					index++;
					listPatIds.add(normalizeTextBook1(lin.get(i)));
					currentHeading = "";
				}

				actText = "";
			}

			if (readElements)
			{
				if (
						(readInElements)
					&&
						(!(lin.get(i).matches("\\d+\\.\\d+\\s.*"))) // 5.2
					&&
						(!(lin.get(i).matches("\\d+\\.\\d+\\.\\d+\\s.*")))
					&&
						(!(lin.get(i).matches("Kapitel\\s\\d+\\s.*")))
					&&
						(!(lin.get(i).matches("\\d+\\s*I*")))
					&&
						(!lin.get(i).matches("\u000C\\d+"))
					&&
						(!lin.get(i).matches("\\d\\.\\d\\.\\d"))
					&&
						(!lin.get(i).matches("\\d+\\.\\d"))
					&&
						(!lin.get(i).matches("\\u2013 \\d+"))
					&&
						(!lin.get(i).startsWith("H. Siekmann"))
					&&
						(!lin.get(i).matches("Vorderer Beckenring"))
					&&
						(!lin.get(i).matches("Hinterer Beckenring"))
					&&
						(!lin.get(i).endsWith("Acetabulum"))
					&&
						(!lin.get(i).startsWith("DOI"))
					&&
						(!lin.get(i).matches("Obere Extremität"))
					&&
						(!lin.get(i).matches("Untere Extremität"))
					&&
						(!lin.get(i).matches("Schulter und Humerus"))
					&&
						(!lin.get(i).matches("Hand"))
					&&
						(!lin.get(i).matches("L\\. Jansch"))
					&&
						(!lin.get(i).matches("Femur und Patella"))
					&&
						(!lin.get(i).matches("Degenerative und posttraumatische Fußchirurgie"))
					&&
						(!lin.get(i).matches("Tibia und Fibula"))
					&&
						(!lin.get(i).matches("Oberes Sprunggelenk"))
					&&
						(!lin.get(i).matches("Arthroskopie"))
					&&
						(!lin.get(i).matches("S\\. Klima"))
					&&
						(!lin.get(i).startsWith("L. Irlenbusch"))
					&&
						(!lin.get(i).matches("Schultergelenk"))
					&&
						(!lin.get(i).matches("Ellenbogengelenk"))
					&&
						(!lin.get(i).matches("Kniegelenk"))
					&&
						(!lin.get(i).matches("Hüftgelenk"))
					&&
						(!lin.get(i).matches("Handgelenk"))
					&&
						(!lin.get(i).matches("Prothetik"))
					&&
						(!lin.get(i).startsWith("S. Rehart"))
					&&
						(!lin.get(i).startsWith("Periprothetische Frakturen"))
					&&
						(!lin.get(i).startsWith("Korrekturen, Amputationen"))
					&&
						(!lin.get(i).startsWith("und Defektdeckungen"))
					&&
						(!lin.get(i).matches("Kinderorthopädie"))
					&&
						(!lin.get(i).matches("M\\. Wojan"))
					&&
						(!lin.get(i).matches("Osteotomien"))
					&&
						(!lin.get(i).matches("Rheumachirurgie"))
					&&
						(!lin.get(i).matches("Lappenplastiken"))
					&&
						(!lin.get(i).matches("J\\. H\\. Völpel"))
					&&
						(!lin.get(i).matches("Radius und Ulna"))
					&&
						(!lin.get(i).matches(""))

				// pagenumber / chapter in text - if text page break
				)
				{
					actText = actText + "\n" + lin.get(i);
				}

				if (lin.get(i).matches("Bericht"))
				{
					readInElements = true;
				}
			}
		}
		return textDocuments;
	}

	private static ArrayList<TextDocument> extractContentBook_23(String content, BookProperties bookProperties)
	{
		ArrayList<TextDocument> textDocuments = new ArrayList<>();
		int index = 1;

		String[] lines = content.split("\n");

		String actText = "";
		String actChapter = "";
		String actCase = "";
		String actOPtime = "";
		String actHeading = "";

		boolean readElements = false;
		boolean readInElements = false;

		ArrayList<String> topics = new ArrayList<>();
		topics.add(bookProperties.topics.get(0));

		for (int i = 0; i < lines.length; i++) {
			if (lines[i].matches("\\d+\\.\\d+\\s.*")) {
				if (!(actChapter.equals(""))) {
					if (!(actChapter.equals(lines[i].replaceAll("· ", "")))) {
						readInElements = false;
					}
				}
				actChapter = lines[i].replaceAll("· ", "");
			}

			if (lines[i].matches("\\d+.\\d+\\.\\d+\\s.*")) {
				readInElements = false;
			}

			if ((actHeading.equals(""))&&(lines[i].startsWith("OP-Bericht,")))
			{
				if (!(lines[i].equals("")))
				{
					actHeading = lines[i-1];
				}
				else if (!(lines[i-2].equals("")))
				{
					actHeading = lines[i-2];
				}
				actHeading = actHeading.replaceAll("\\d+\\.\\d+\\.\\d+ ?", "");
				if (actHeading.endsWith(" "))
				{
					actHeading = actHeading.substring(0, actHeading.length() - 1);
				}
			}

			if (
					(lines[i].matches("\\s?Pat\\.-Nr\\..*"))
				&&
					(!(lines[i].contains("000000000")))
				)
			{
				readElements = true;
				readInElements = false;

				if (!(actText.equals(""))) {
					if (!(listPatIds.contains(actCase + actOPtime))) {
						textDocuments.add(cleanText(actText, topics, bookProperties, actHeading, index));
						index++;
						listPatIds.add(actCase + actOPtime);
						actHeading = "";
					}
					actText = "";
				}
				actCase = normalizeTextBook23(lines[i]);

				actHeading = lines[i-4];

				if (!(lines[i-5].equals("")))
				{
					actHeading = lines[i-5] + " " + actHeading;

					if (!(lines[i-6].equals("")))
					{
						actHeading = lines[i-6] + " " + actHeading;
					}
				}

				actHeading = actHeading.replaceAll("\\d+\\.\\d+\\.\\d+ ?", "");
				if (actHeading.endsWith(" "))
				{
					actHeading = actHeading.substring(0, actHeading.length() - 1);
				}
			}

			if ((lines[i].contains("OP-Datum:")) && (lines[i + 1].contains("OP-Dauer (Schnitt/Naht):"))) {
				actOPtime = normalizeActOP(lines[i], lines[i + 1]);
			}

			if (lines[i].matches("III")) {
				// last element
				readElements = false;

				actCase = normalizeTextBook23(actCase);

				if (!(listPatIds.contains(actCase + actOPtime))) {
					textDocuments.add(cleanText(actText, topics, bookProperties, actHeading, index));
					listPatIds.add(actCase + actOPtime);
					actHeading = "";
				}
				actText = "";
			}

			if (readElements) {
				if ((lines[i].startsWith("Vorgeschichte")) || (lines[i].startsWith("Indikation"))) {
					readInElements = true;
				}

				if ((!(lines[i].matches("\\d+\\s*I*"))) && (readInElements) && (!(lines[i].matches("\\d+\\.\\d+\\s.*"))) // 5.2
						&& (!(lines[i].matches("\\d+\\.\\d+\\.\\d+\\s.*")))
						&& (!(lines[i].matches("Kapitel\\s\\d+\\s.*")))
						&& (!(lines[i].matches("\\d+\\sKapitel\\s\\d+\\s.*"))) && (!(lines[i].matches("\\d+\\s*I*")))
				// pagenumber / chapter in text - if text page break
				)
				{
					actText = actText + lines[i] + "\n";
				}
			}
		}
		return textDocuments;
	}

	private static TextDocument cleanText(
			String t,
			ArrayList<String> topics,
			BookProperties bookProperties,
			String actHeading,
			int index
	)
	{
		TextDocument textDocument = new TextDocument();

		String[] split = t.split("\\n");
		String author = split[split.length - 1];
		String text = "";
		String textSection = "";

		for (int i = 0; i < split.length - 1; i++)
		{
			if (split[i].startsWith("Vorgeschichte:"))
			{
				textSection = textSection + "\n" + "<Indikation>\n" + split[i].replaceAll("Vorgeschichte: ?", "Vorgeschichte:\n");
				text = text + "\n" + split[i].replaceAll("Vorgeschichte: ?", "Vorgeschichte:\n");
			}
			else if (split[i].startsWith("Vorgeschichte/Indikation:"))
			{
				textSection = textSection + "\n" + "<Indikation>\n" + split[i].replaceAll("Vorgeschichte\\/Indikation: ?", "Vorgeschichte/Indikation:\n");
				text = text + "\n" + split[i].replaceAll("Vorgeschichte\\/Indikation: ?", "Vorgeschichte/Indikation:\n");
			}
			else if (split[i].startsWith("Vorgeschichte/ Indikation:"))
			{
				textSection = textSection + "\n" + "<Indikation>\n" + split[i].replaceAll("Vorgeschichte/ Indikation:", "Vorgeschichte/Indikation:\n");
				text = text + "\n" + split[i].replaceAll("Vorgeschichte/ Indikation:", "Vorgeschichte/Indikation:\n");
			}
			else if (split[i].startsWith("Diagnose:"))
			{
				textSection = textSection + "\n" + "</Indikation>\n<Diagnose>\n" + split[i].replaceAll("Diagnose: ?", "Diagnose:\n");
				text = text + "\n" + split[i].replaceAll("Diagnose: ?", "Diagnose:\n");
			}
			else if (split[i].startsWith("Therapie:"))
			{
				textSection = textSection + "\n" + "</Diagnose>\n<Operation-Therapie>\n" + split[i].replaceAll("Therapie: ?", "Therapie:\n");
				text = text + "\n" + split[i].replaceAll("Therapie: ?", "Therapie:\n");
			}
			else if (split[i].startsWith("Operation:"))
			{
				textSection = textSection + "\n" + "</Diagnose>\n<Operation-Therapie>\n" + split[i].replaceAll("Operation: ?", "Operation:\n");
				text = text + "\n" + split[i].replaceAll("Operation: ?", "Operation:\n");
			}
			else if (split[i].startsWith("Bericht:"))
			{
				textSection = textSection + "\n" + "</Operation-Therapie>\n<Vorgehen-Bericht>\n" + split[i].replaceAll("Bericht: ?", "Bericht:\n");
				text = text + "\n" + split[i].replaceAll("Bericht: ?", "Bericht:\n");
			}
			else if (split[i].startsWith("Vorgehen:"))
			{
				textSection = textSection + "\n" + "</Operation-Therapie>\n<Vorgehen-Bericht>\n" + split[i].replaceAll("Vorgehen: ?", "Vorgehen:\n");
				text = text + "\n" + split[i].replaceAll("Vorgehen: ?", "Vorgehen:\n");
			}
			else if (split[i].startsWith("Procedere:"))
			{
				textSection = textSection + "\n" + "</Vorgehen-Bericht>\n<Procedere>\n" + split[i].replaceAll("Procedere: ?", "Procedere:\n");
				text = text + "\n" + split[i].replaceAll("Procedere: ?", "Procedere:\n");
			}
			else if (split[i].startsWith("Dr.") || split[i].startsWith(" Dr.") || split[i].startsWith("Prof.") || split[i].startsWith(" Prof.") || split[i].startsWith("OA Dr.") || split[i].startsWith(" OA Dr.") || split[i].startsWith(" PD Dr.") || split[i].startsWith(" P. Derst") || split[i].startsWith(" N. N."))
				{
					if (!split[i].contains(author)) {
						if (author.startsWith(" Tumoren") || author.startsWith(" Kinderorthopädie") || author.startsWith("und Amputationen") || author.startsWith(" Rheumachirurgie") || author.startsWith(" Degenerative und posttraumatische Fußchirurgie") || author.startsWith("OP-Bericht,") || author.startsWith(" Untere Extremität") || author.startsWith(" Sonstiges")) {
							author = split[i];
						} else {
							author = split[i].replace(",\n", "") + ", " + author;
						}
						split[i] = "";
					}
			}
			else
			{
				if ((text.equals("")) )
				{
					text = text + split[i];

					if ((textSection.equals("")) )
					{
						textSection = textSection + split[i];
					}
					else
					{
						textSection = textSection + " " + split[i];
					}
				}
				else
				{
					text = text + " " + split[i];

					if ((textSection.equals("")) )
					{
						textSection = textSection + split[i];
					}
					else
					{
						textSection = textSection + " " + split[i];
					}
				}
			}
		}

		if (author.startsWith(" "))
		{
			author = author.replaceFirst(" ", "");
		}
		textSection = textSection + "\n"  + "</Procedere>\n<FinalRemarks>\n" + author;
		text = text + "\n" + author;

		text = cleanText(text);
		textSection = cleanText(textSection);
		textSection = textSection + "\n</FinalRemarks>\n";

		textDocument.setText(text);
		textDocument.setDocumentType(bookProperties.documentType.get(0));

		textDocument.setTopic(topics);
		textDocument.setSource(
			bookProperties.getTitle() + " " +
			bookProperties.getEditorAuthor() + " " +
			bookProperties.getYear() + " " +
			bookProperties.getPublisher() + " " +
			bookProperties.getDoi()
		);

		textDocument.setHeading(actHeading);
		textDocument.setIdLong(bookProperties.sourceShort + "-" + index);
		textDocument.setSourcShort(bookProperties.sourceShort);
		textDocument.setBookId(bookProperties.bookId);

		index++;

		return textDocument;
	}

	private static String cleanText(String text)
	{
		if (text.startsWith(" "))
		{
			text = text.replaceFirst(" ", "");
		}

		text = text.replaceAll("\u00A0", " ");

		if (text.startsWith("\n")) {
			text = text.replaceFirst("\n", "");
		}

		text = text.replaceAll(" +", " ");
		text = LanguageTools.removeHyphenNew(text);

		return text;
	}

	private static String normalizeTextBook1(String text)
	{
		text = text.replaceAll("\\s+", " ");
		text = text.replaceAll("Pat.-Nr.:", "");
		text = text.replaceAll("Fall-Nr.:", "");
		text = text.replaceAll(" +", " ");

		if (text.startsWith("\\s")) {
			text = text.replaceFirst(" ", "");
		}

		if (text.endsWith("\\s")) {
			text = text.substring(0, text.length() - 1);
		}

		text = text.replaceAll(" ", "");
		return text;
	}

	private static String normalizeTextBook23(String actCase)
	{
		actCase = actCase.replaceAll("\\s+", " ");
		actCase = actCase.replaceAll("Pat.-Nr.:", "");
		actCase = actCase.replaceAll("Fall-Nr.:", "");
		actCase = actCase.replaceAll(" +", " ");

		if (actCase.startsWith("\\s")) {
			actCase = actCase.replaceFirst(" ", "");
		}

		if (actCase.endsWith("\\s")) {
			actCase = actCase.substring(0, actCase.length() - 1);
		}

		actCase = actCase.replaceAll(" ", "");
		return actCase;
	}

	private static String normalizeActOP(String opDate, String opDuration)
	{
		opDate = opDate.replaceAll("OP-Datum:", "");
		opDate = opDate.replaceAll("Pat", "");

		opDuration = opDuration.replaceAll("OP-Dauer \\(Schnitt/Naht\\):", "");
		opDuration = opDuration.replaceAll("Uhr", "");
		opDuration = opDuration.replaceAll("\\. ", ".");

		if (opDuration.endsWith(" "))
		{
			opDuration = opDuration.substring(0, opDuration.length() - 1);
		}

		opDate = opDate.replaceAll(" ", "");
		opDuration = opDuration.replaceAll(" ", "");

		return opDate + opDuration;
	}
}
