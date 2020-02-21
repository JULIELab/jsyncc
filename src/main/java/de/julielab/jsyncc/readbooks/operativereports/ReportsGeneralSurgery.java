package de.julielab.jsyncc.readbooks.operativereports;

import de.julielab.jsyncc.readbooks.BookExtractor;
import de.julielab.jsyncc.readbooks.BookReader;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.LanguageTools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ContextedException;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// VERSION 2

public class ReportsGeneralSurgery implements BookExtractor {
	private static final int ID = 4;
	private final static String SOURCE = BookReader.yaml.getSourceById(ID);
	private final static String SOURCE_SHORT = BookReader.yaml.getSourceShortById(ID);
	public static final String BOOK = "books/04-Operationsberichte-fuer-Einsteiger";

	public static final String TOPIC = "Chirurgie";
	public static final String TYPE = "OperativeReport";

	@Override
	public String parseBook(Path pdfPath) throws ContextedException {
		File file1 = new File(BOOK + "/" + "b-0034-39761.pdf");
		File file2 = new File(BOOK + "/" + "b-0034-39762.pdf");
		File file3 = new File(BOOK + "/" + "b-0034-39763.pdf");
		File file4 = new File(BOOK + "/" + "b-0034-39764.pdf");
		File file5 = new File(BOOK + "/" + "b-0034-39765.pdf");
		File file6 = new File(BOOK + "/" + "b-0034-39766.pdf");
		File file7 = new File(BOOK + "/" + "b-0034-39767.pdf");
		File file8 = new File(BOOK + "/" + "b-0034-39768.pdf");
		File file9 = new File(BOOK + "/" + "b-0034-39769.pdf");
		File file10 = new File(BOOK + "/" + "b-0034-39770.pdf");
		File file11 = new File(BOOK + "/" + "b-0034-39771.pdf");

		PDFMergerUtility PDFmerger = new PDFMergerUtility();
		PDFmerger.setDestinationFileName(BOOK + "/" + "merged.pdf");

		try {
			PDFmerger.addSource(file1);
			PDFmerger.addSource(file2);
			PDFmerger.addSource(file3);
			PDFmerger.addSource(file4);
			PDFmerger.addSource(file5);
			PDFmerger.addSource(file6);
			PDFmerger.addSource(file7);
			PDFmerger.addSource(file8);
			PDFmerger.addSource(file9);
			PDFmerger.addSource(file10);
			PDFmerger.addSource(file11);
			PDFmerger.mergeDocuments();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			ProcessBuilder pb = new ProcessBuilder("pdftohtml", BOOK + "/" + "merged.pdf");
			Process p = pb.start();
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String plainText = "";
		List<String> allLines;

		File textFile = new File(pdfPath.toString() + "/mergeds.html");
		try {
			allLines = FileUtils.readLines(textFile, "UTF-8");
		} catch (IOException e) {
			throw new ContextedException(e);
		}
		for (int i = 0; i < allLines.size(); i++) {
			plainText = plainText + allLines.get(i) + "\n";
		}

		return plainText;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<TextDocument> extractContent(String plainText) {
		String[] lines = plainText.split("\n");

		boolean readElements = false;

		String text = "";
		String actHeading = "";
		String actTopic = "";
		int index = 1;

		ArrayList<TextDocument> listDocuments = new ArrayList<>();

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			if ((line.startsWith("<a name=90>")) // ... "Erfassung und
													// Verwaltung"
					|| (line.startsWith("<b>Schnittführung")) || (line.startsWith("<b>Debridement"))
					|| (line.startsWith("<b>Lage")) || (line.startsWith("<b>Punktionsstelle"))
					|| (line.startsWith("<b>Trokarplatzierung"))) {
				readElements = false;
			}

			if (line.matches("<a name=\\d+></a><i>\\d&#160;.*<\\/i><br\\/>")) {
				actTopic = line;
				actTopic = actTopic.replaceAll("<a name=\\d+></a><i>\\d&#160;", "");
				actTopic = actTopic.replaceAll("</i><br/>", "");
				actTopic = actTopic.replaceAll("<b>", "");
				actTopic = actTopic.replaceAll("</b>", "");
				actTopic = actTopic.replaceAll("&#160;", " ");
			}

			if (line.startsWith("<b>Indikation:")) {
				readElements = true;

				if (!(text.equals(""))) {
					listDocuments.add(reviseDocument(text, actHeading, actTopic, index));
					index++;
				}
				text = "";
			}

			if (readElements) {
				line = line.replaceAll("&#160;", " ");
				line = line.replaceAll("<br/>", " ");
				line = line.replaceAll("<br/>", " ");

				if ((!(line.matches("<b>\\d+</b> +"))) && (!(line.startsWith("Aus M.Hagen")))
						&& (!(line.startsWith("Dieses Dokument ist nur für den persönlichen")))
						&& (!(line.startsWith("<a name="))) && (!(line.startsWith("<i><b>")))
						&& (!(line.startsWith("</body>"))) && (!(line.startsWith("</html>")))
						&& (!(line.startsWith("Heruntergeladen von: Thieme E-Books")))

				) {
					line = line.replaceAll("<hr/>", "");
					text = text + "\n" + line;
				}
			}
		}

		listDocuments.add(reviseDocument(text, actHeading, actTopic, index));

		return listDocuments;
	}

	public static TextDocument reviseDocument(String element, String actHeading, String actTopic, int index) {
		String[] t = element.split("\n");

		String text = "";
		String textSection = "";

		for (int i = 0; i < t.length; i++) {
			text = text + t[i];

			if (t[i].startsWith("<b>Indikation:")) {
				textSection = textSection + "<Indikation>" + t[i];
			} else if (t[i].startsWith("<b>Operation:")) {
				textSection = textSection + "\n" + "</Indikation>\n<Operation>" + t[i];
			} else if (t[i].startsWith("<b>Procedere:")) {
				textSection = textSection + "\n" + "</Operation>\n<Procedere>" + t[i];
			} else {
				textSection = textSection + t[i];
			}
		}

		text = LanguageTools.removeHyphenNew(text);

		text = cleanText(text);
		textSection = cleanText(textSection);
		textSection = textSection + "\n</Procedere>";

		TextDocument doc = new TextDocument();
		doc.setText(text);
		doc.setTextSection(textSection);
		doc.setType(TYPE);

		ArrayList<String> topic = new ArrayList<>();
		topic.add(TOPIC);
		topic.add(actTopic);
		doc.setTopic(topic);

		doc.setHeading(actHeading);
		doc.setSource(SOURCE);

		doc.setIdLong(SOURCE_SHORT + "-" + index);

		return doc;
	}

	private static String cleanText(String text) {

		text = text.replaceAll("<i>", " ");
		text = text.replaceAll("</i>", " ");

		text = text.replaceAll("<b>", "\n<b>");
		text = text.replaceAll("</b>", "</b>\n");

		text = text.replaceAll("<b>", "");
		text = text.replaceAll("</b>", "");

		text = LanguageTools.removeHyphenNew(text);

		if (text.startsWith("\n")) {
			text = text.replaceFirst("\n", "");
		}

		return text;
	}

	@Override
	public boolean validateText(String plainText) {
		return plainText.contains("<br/>(ISBN 3-13-141401-4) © 2005");
	}
}