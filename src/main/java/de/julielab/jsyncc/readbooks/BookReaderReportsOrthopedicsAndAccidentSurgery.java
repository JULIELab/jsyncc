package de.julielab.jsyncc.readbooks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

import org.apache.commons.codec.digest.DigestUtils;

import de.julielab.jsyncc.tools.FileTools;
import de.julielab.jsyncc.tools.LanguageTools;

public class BookReaderReportsOrthopedicsAndAccidentSurgery {
	public static ArrayList<TextDocument> ListOfDocuments = new ArrayList<TextDocument>();
	public static ArrayList<CheckSum> listCheckSum = new ArrayList<CheckSum>();

	private static ArrayList<String> ListOfPatIds = new ArrayList<String>();

	public static String BOOK_1 = FileTools.getSinglePDFFileName("src/main/resources/books/01-Operationsberichte-Orthopaedie-und-Unfallchirurgie");
	public static String BOOK_2 = FileTools.getSinglePDFFileName("src/main/resources/books/02-Operationsberichte-Orthopaedie");
	public static String BOOK_3 = FileTools.getSinglePDFFileName("src/main/resources/books/03-Operationsberichte-Unfallchirurgie");

	public static String source_1 = "Siekmann, H., Irlenbusch, L., and Klima, S. (2016). Operationsberichte Orthopädie und Unfallchirurgie. Springer-Verlag.";
	public static String source_2 = "Siekmann, H. and Klima, S. (2013). Operationsberichte Orthopädie: mit speziellen unfallchirurgisch-orthopädischen Eingriffen. Springer-Verlag.";
	public static String source_3 = "Siekmann, H. and Irlenbusch, L. (2012). Operationsberichte Unfallchirurgie. Springer-Verlag.";

	public static String sourceShort_1 = "Siekmann2016OrthopedicsAccidentSurgery";
	public static String sourceShort_2 = "Siekmann2013Orthopedics";
	public static String sourceShort_3 = "Siekmann2012AccidentSurgery";

	public static int indexLocal = 1;
	
	public static ArrayList<TextDocument> extractContent() throws IOException, InterruptedException {
		if (BOOK_1 != null) {
			ProcessBuilder pb = new ProcessBuilder("pdftotext", BOOK_1);
			Process p = pb.start();
			p.waitFor();
	
			String contentFile1 = BOOK_1.replaceAll("pdf", "txt");
			
			extractContentBook_1(contentFile1, source_1, sourceShort_1);
		}
		
		if (BOOK_2 != null) {
			String content_2 = LanguageTools.getContentByTika(BOOK_2);
			extractContentBook_23(content_2, source_2, "Orthopädie", sourceShort_2);
		}		
		
		if (BOOK_3 != null) {
			String content_3 = LanguageTools.getContentByTika(BOOK_3);
			extractContentBook_23(content_3, source_3, "Unfallchirurgie", sourceShort_3);
		}
		
		return ListOfDocuments;
	}

	private static void extractContentBook_1(String contentFile, String source, String sourceShort) throws IOException
	{
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
		boolean readElemens = false;
		boolean readInElements = false;

		ArrayList<String> topics = new ArrayList<>();
		topics.add("Orthopädie");
		topics.add("Unfallchirurgie");

		String actHeading = "";
		
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

			if ((actHeading.equals(""))&&(lin.get(i).startsWith("OP-Bericht,")))
			{
				if (!(lin.get(i-1).equals("")))
				{
					actHeading = lin.get(i-1);
				}
				else if (!(lin.get(i-2).equals("")))
				{
					actHeading = lin.get(i-2);
				}
				if (actHeading.endsWith(" "))
				{
					actHeading = actHeading.substring(0, actHeading.length() - 1);
				}
			}
			
			if (lin.get(i).matches("\\s?Pat\\.-Nr\\..*"))
			{
				readElemens = true;
				readInElements = false;

				if (!(actText.equals("")))
				{
					editOneReportBook(actText, topics, source, sourceShort, actHeading);
					ListOfPatIds.add(normalizeActCase1(actCase + " " + actCase2 + actOPtime));

					actText = "";

					if (!(lin.get(i-2)).equals(""))
					{
						actHeading = lin.get(i-2);
						
						if (!(lin.get(i-3)).equals(""))
						{
							actHeading = lin.get(i-3)  + " " + actHeading;
						}
					}
					else if (!(lin.get(i-3)).equals(""))
					{
						actHeading = lin.get(i-3);
						
						if (!(lin.get(i-4)).equals(""))
						{
							actHeading = lin.get(i-4)  + " " + actHeading;
						}
					}

					if (actHeading.endsWith(" "))
					{
						actHeading = actHeading.substring(0, actHeading.length() - 1);
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
				readElemens = false;

				if (!(actText.equals("")))
				{
					editOneReportBook(actText, topics, source, sourceShort, actHeading);
					ListOfPatIds.add(normalizeActCase1(lin.get(i)));
					actHeading = "";
				}
				
				actText = "";
			}

			if (readElemens)
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
		
	}

	private static void extractContentBook_23(String content, String source, String topic, String sourceShort) {
		
		indexLocal = 1;
		
		String[] lines = content.split("\n");

		String actText = "";
		String actChapter = "";
		String actCase = "";
		String actOPtime = "";
		String actHeading = "";
		
		boolean readElemens = false;
		boolean readInElements = false;

		ArrayList<String> topics = new ArrayList<>();
		topics.add(topic);

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
				readElemens = true;
				readInElements = false;

				if (!(actText.equals(""))) {
					if (!(ListOfPatIds.contains(actCase + actOPtime))) {
						editOneReportBook(actText, topics, source, sourceShort, actHeading);
						ListOfPatIds.add(actCase + actOPtime);
						actHeading = "";
					}
					actText = "";
				}
				actCase = normalizeActCase23(lines[i]);
				
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
				readElemens = false;

				actCase = normalizeActCase23(actCase);

				if (!(ListOfPatIds.contains(actCase + actOPtime))) {
					editOneReportBook(actText, topics, source, sourceShort, actHeading);
					ListOfPatIds.add(actCase + actOPtime);
					actHeading = "";
				}
				actText = "";
			}

			if (readElemens) {
				if ((lines[i].startsWith("Vorgeschichte")) || (lines[i].startsWith("Indikation"))) {
					readInElements = true;
				}

				if ((!(lines[i].matches("\\d+\\s*I*"))) && (readInElements) && (!(lines[i].matches("\\d+\\.\\d+\\s.*"))) // 5.2
						&& (!(lines[i].matches("\\d+\\.\\d+\\.\\d+\\s.*")))
						&& (!(lines[i].matches("Kapitel\\s\\d+\\s.*")))
						&& (!(lines[i].matches("\\d+\\sKapitel\\s\\d+\\s.*"))) && (!(lines[i].matches("\\d+\\s*I*")))
				// pagenumber / chapter in text - if text page break
				) {
					actText = actText + lines[i] + "\n";
				}
			}
		}
	}

	private static void editOneReportBook(String t, ArrayList<String> topics, String source, String sourceShort, String actHeading)
	{
		TextDocument document = new TextDocument();

		String[] split = t.split("\\n");
		String author = split[split.length - 1];
		String text = "";

		for (int i = 0; i < split.length - 1; i++)
		{
			if (split[i].startsWith("Vorgeschichte:"))
			{
				text = text + "\n" + split[i].replaceAll("Vorgeschichte: ?", "Vorgeschichte:\n");
			}
			else if (split[i].startsWith("Vorgeschichte/Indikation:"))
			{
				text = text + "\n" + split[i].replaceAll("Vorgeschichte\\/Indikation: ?", "Vorgeschichte/Indikation:\n");
			}
			else if (split[i].startsWith("Diagnose:"))
			{
				text = text + "\n" + split[i].replaceAll("Diagnose: ?", "Diagnose:\n");
			}
			else if (split[i].startsWith("Therapie:"))
			{
				text = text + "\n" + split[i].replaceAll("Therapie: ?", "Therapie:\n");
			}
			else if (split[i].startsWith("Operation:"))
			{
				text = text + "\n" + split[i].replaceAll("Operation: ?", "Operation:\n");
			}
			else if (split[i].startsWith("Bericht:"))
			{
				text = text + "\n" + split[i].replaceAll("Bericht: ?", "Bericht:\n");
			}
			else if (split[i].startsWith("Vorgehen:"))
			{
				text = text + "\n" + split[i].replaceAll("Vorgehen: ?", "Vorgehen:\n");
			}
			else if (split[i].startsWith("Procedere:"))
			{
				text = text + "\n" + split[i].replaceAll("Procedere: ?", "Procedere:\n");
			}
			else
			{
				if ((text.equals("")) )
				{
					text = text + split[i];
				}
				else
				{
					text = text + " " + split[i];
				}
			}
		}

		if (text.startsWith(" "))
		{
			text = text.replaceFirst(" ", "");
		}
		
		// this step is for the jtbd-tokenizer
		text = text.replaceAll("»", "\"");
		text = text.replaceAll("«", "\"");
		text = text.replaceAll("„", "\"");
		text = text.replaceAll("“", "\"");
		text = text.replaceAll("\u2013", "-"); // En dash −
		text = text.replaceAll("\u2212", "-"); // Minus −
		
		if (author.startsWith(" "))
		{
			author = author.replaceFirst(" ", "");
		}
		text = text + "\n" + author;
		
		if (text.startsWith("\n")) {
			text = text.replaceFirst("\n", "");
		}

		text = text.replaceAll(" +", " ");

		text = LanguageTools.removeHyphenNew(text);
		document.text = text;
		document.type = "operation report";
		document.topic = topics;
		document.source = source;
		document.heading = actHeading;
		document.idLong = sourceShort + "-" + indexLocal;
		indexLocal++;
		
		BookReader.index++;
		document.id = Integer.toString(BookReader.index);

		ListOfDocuments.add(document);

		CheckSum checkSum = new CheckSum();
		checkSum.checkSumText = DigestUtils.md5Hex(text);
		checkSum.id = Integer.toString(BookReader.index);
		BookReader.listCheckSum.add(checkSum);

	}

	private static String normalizeActCase1(String actCase) {
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
	
	private static String normalizeActCase23(String actCase) {
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

	private static String normalizeActOP(String opDate, String opDuration) {
		opDate = opDate.replaceAll("OP-Datum:", "");
//		opDate = opDate.replaceAll("\\s", "");
		opDate = opDate.replaceAll("Pat", "");

		opDuration = opDuration.replaceAll("OP-Dauer \\(Schnitt/Naht\\):", "");
//		opDuration = opDuration.replaceAll("\\s", "");
		
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
