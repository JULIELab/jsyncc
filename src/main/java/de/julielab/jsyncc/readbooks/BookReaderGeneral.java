package de.julielab.jsyncc.readbooks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import de.julielab.jsyncc.tools.LanguageTools;

public class BookReaderGeneral {
	public static String BOOK_4 = "src/main/resources/books/04-Operationsberichte-fuer-Einsteiger";
	public static String source = "Hagen, Monika. (2005). Operationsberichte für Einsteiger-Chirurgie: Operation vorbereiten—Bericht diktieren. Georg Thieme Verlag";
	public static String sourceShort = "Hagen2005General";

	public static int indexLocal = 1;

	public static ArrayList<TextDocument> ListOfDocuments = new ArrayList<TextDocument>();
	public static ArrayList<String> ListOfTopics = new ArrayList<String>();

	static int index = 0;

	public static ArrayList<TextDocument> extractContent() throws IOException, InterruptedException {
		List<File> files = Arrays.asList(
			new File(BOOK_4 + "/" + "b-0034-39761.pdf"),
			new File(BOOK_4 + "/" + "b-0034-39762.pdf"),
			new File(BOOK_4 + "/" + "b-0034-39763.pdf"),
			new File(BOOK_4 + "/" + "b-0034-39764.pdf"),
			new File(BOOK_4 + "/" + "b-0034-39765.pdf"),
			new File(BOOK_4 + "/" + "b-0034-39766.pdf"),
			new File(BOOK_4 + "/" + "b-0034-39767.pdf"),
			new File(BOOK_4 + "/" + "b-0034-39768.pdf"),
			new File(BOOK_4 + "/" + "b-0034-39769.pdf"),
			new File(BOOK_4 + "/" + "b-0034-39770.pdf"),
			new File(BOOK_4 + "/" + "b-0034-39771.pdf"));

		for (File f : files) {
			if (!f.exists()) {
				System.err.println(f.getPath() + " does not exist, skipping documents");
				return ListOfDocuments;
			}
		}

		PDFMergerUtility PDFmerger = new PDFMergerUtility();
		PDFmerger.setDestinationFileName(BOOK_4 + "/" + "merged.pdf");

		for (File file : files) {
			PDFmerger.addSource(file);
		}

		PDFmerger.mergeDocuments();

		ProcessBuilder pb = new ProcessBuilder("pdftohtml", BOOK_4 + "/" + "merged.pdf");
		Process p = pb.start();
		p.waitFor();

		@SuppressWarnings("resource")
		Stream<String> lines = Files.lines(Paths.get(BOOK_4 + "/" + "mergeds.html"));

		boolean readElements = false;

		String text = "";
		String actHeading = "";
		String actTopic = "";
		String linebefore = "";

		for (Iterator<String> iterator = lines.iterator(); iterator.hasNext();) {
			String element = iterator.next();

			if (element.startsWith("<a name=90>"))
			// ... "Erfassung und Verwaltung"
			{
				readElements = false;
			}

			if (element.matches("<a name=\\d+></a><i>\\d&#160;.*<\\/i><br\\/>")) {
				actTopic = element;
				actTopic = actTopic.replaceAll("<a name=\\d+></a><i>\\d&#160;", "");
				actTopic = actTopic.replaceAll("</i><br/>", "");
				actTopic = actTopic.replaceAll("<b>", "");
				actTopic = actTopic.replaceAll("</b>", "");
				actTopic = actTopic.replaceAll("&#160;", " ");
			}

			if (element.startsWith("<b>Indikation:")) {

				actHeading = linebefore;
				actHeading = actHeading.replaceAll("<i><b>", "");
				actHeading = actHeading.replaceAll("</b></i>", "");
				actHeading = actHeading.replaceAll("<br/>", "");

				actHeading = actHeading.replaceAll("&#160;", " ");

				if (actHeading.endsWith("")) {
					actHeading = actHeading.substring(0, actHeading.length() - 1);
				}

				readElements = true;

				if (!(text.equals(""))) {
					editOneReport(text, actHeading, actTopic);
				}
				text = "";
			}

			if (readElements) {
				element = element.replaceAll("&#160;", " ");
				element = element.replaceAll("<br/>", " ");
				element = element.replaceAll("<br/>", " ");

				if ((!(element.matches("<b>\\d+</b> +"))) && (!(element.startsWith("Aus M.Hagen")))
						&& (!(element.startsWith("Dieses Dokument ist nur für den persönlichen")))
						&& (!(element.startsWith("<a name="))) && (!(element.startsWith("<i><b>")))
						&& (!(element.startsWith("</body>"))) && (!(element.startsWith("</html>")))
						&& (!(element.startsWith("Heruntergeladen von: Thieme E-Books")))

				) {

					element = element.replaceAll("<hr/>", "");
					text = text + "\n" + element;
				}
			}

			linebefore = element;
		}

		editOneReport(text, actHeading, actTopic);

		return ListOfDocuments;
	}

	public static void editOneReport(String element, String actHeading, String actTopic) {
		String[] t = element.split("\n");

		String text = "";

		for (int i = 0; i < t.length; i++) {
			text = text + t[i];
		}

		text = text.replaceAll("<i>", " ");
		text = text.replaceAll("</i>", " ");

		text = text.replaceAll("<b>", "\n<b>");
		text = text.replaceAll("</b>", "</b>\n");

		text = text.replaceAll("<b>", "");
		text = text.replaceAll("</b>", "");

		text = LanguageTools.removeHyphenNew(text);

		// this step is for the jtbd-tokenizer
		text = text.replaceAll("»", "\"");
		text = text.replaceAll("«", "\"");
		text = text.replaceAll("„", "\"");
		text = text.replaceAll("“", "\"");
		text = text.replaceAll("\u2013", "-"); // En dash −
		text = text.replaceAll("\u2212", "-"); // Minus −

		TextDocument doc = new TextDocument();

		BookReader.index++;
		doc.id = Integer.toString(BookReader.index);
		doc.text = text;
		doc.type = "Operationsbericht";

		ArrayList<String> topic = new ArrayList<>();
		topic.add(actTopic);

		doc.topic = topic;
		doc.heading = actHeading;
		doc.source = source;
		doc.idLong = sourceShort + "-" + indexLocal;
		indexLocal++;

		ListOfDocuments.add(doc);
	}
}