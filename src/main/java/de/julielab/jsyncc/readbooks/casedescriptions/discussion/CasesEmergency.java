package de.julielab.jsyncc.readbooks.casedescriptions.discussion;

import de.julielab.jsyncc.readbooks.BookExtractor;
import de.julielab.jsyncc.readbooks.BookReader;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.LanguageTools;

import org.apache.commons.lang3.exception.ContextedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CasesEmergency implements BookExtractor {

	private static final int ID = 5;
	private static final String SOURCE = BookReader.yaml.getSourceById(ID);
	private static final String SOURCE_SHORT = BookReader.yaml.getSourceShortById(ID);
	public static final String BOOK = "books/05-Fallbeispiele-Notfallmedizin/978-3-662-47232-3.pdf";

	public static final String TOPIC = "Notfallmedizin";

	public static final String TYPE_1 = "CaseDescription";
	public static final String TYPE_2 = "Discussion";

	@Override
	public String parseBook(Path path) throws ContextedException {
		String plainText = "";
		ProcessBuilder pb = new ProcessBuilder("pdftotext", BOOK);

		try {
			Process p;
			p = pb.start();
			p.waitFor();

			List<String> lines = Files.readAllLines(Paths.get(path.toString().replaceAll(".pdf", ".txt")));
			for (int i = 0; i < lines.size(); i++) {
				plainText = plainText + "\n" + lines.get(i);
			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return plainText;
	}

	@Override
	public boolean validateText(String plainText) {
		return (plainText.contains("978-3-662-47231-6"));
	}

	@Override
	public List<TextDocument> extractContent(String plainText) {
		boolean readElements = false;
		boolean readTableOfContents = false;
		int index = 1;
		String text = "";
		ArrayList<TextDocument> listDocuments = new ArrayList<>();
		ArrayList<String> tableOfAuthors = new ArrayList<>();
		ArrayList<String> tableOfContents = new ArrayList<>();

		String[] lines = plainText.split("\n");

		for (int i = 0; i < lines.length; i++) {
			// get Table of Contents

			if (lines[i].matches("\\u000CInhaltsverzeichnis")) {
				readTableOfContents = true;
			}

			if ((readTableOfContents) && (lines[i].matches("\\d+\\u2002?\\u2003?.*"))) {
				String contentEntry = lines[i];

				contentEntry = contentEntry.replaceAll("\\d+[\\u2002\\u2003]+", "");
				contentEntry = contentEntry.replaceAll("\\uFFFD", "");

				if (contentEntry.startsWith(" ")) {
					contentEntry = contentEntry.replaceFirst(" ", "");
				}

				contentEntry = contentEntry.replaceAll("\\u2002", " ");
				contentEntry = contentEntry.replaceAll("\\u2003", " ");
				contentEntry = contentEntry.replaceAll("\\u0008", " ");
				contentEntry = contentEntry.replaceAll(" +\\d+", "");

				tableOfContents.add(contentEntry);

				if (tableOfContents.size() == 48) {
					readTableOfContents = false;
				}

				tableOfAuthors.add(lines[i + 1]);
			}

			// get Content of Single Texts
			if ((lines[i].matches("Literatur")) || (lines[i].matches("Fazit")) || (lines[i].matches("Letzte Worte"))) {
				readElements = false;
			}

			if (readElements) {
				text = text + "\n" + lines[i];
			}

			if (lines[i].startsWith("77 ")) {
				if (!(text.equals(""))) {
					listDocuments.addAll(normalizeElement(text, index, tableOfContents));
					index = index + 1;
					text = "";
				}

				text = text + "\n" + lines[i];

				readElements = true;
			}

			// last element
			if (lines[i].endsWith("Sachverzeichnis")) {
				if (!(text.equals(""))) {

					listDocuments.addAll(normalizeElement(text, index, tableOfContents));
					index = index + 1;
					text = "";
				}

				readElements = false;
			}
		}

		return listDocuments;
	}

	private static ArrayList<TextDocument> normalizeElement(String text, int index, ArrayList<String> tableOfContents) {
		text = text.replaceFirst("77 ", "");
		text = text.replaceAll("\\u0003", "");
		text = text.replaceAll("\\u00A0", " "); // No-Break Space
		text = text.replaceAll("\\u2013", "-"); // – / En dash

		String[] lines = text.split("\n");
		ArrayList<TextDocument> listDocuments = new ArrayList<TextDocument>();
		;

		text = "";
		boolean partCopyRight = false;
		String tempAuthor = "";

		for (int i = 0; i < lines.length; i++) {
			if ((!(lines[i].matches("\\u000C\\d+\\u2003[\\p{Alnum}öäüÖÄÜß\\s\\p{Punct}]+")))
					&& (!(lines[i].matches("\\u000C\\d+"))) && (!(lines[i].matches("\\d+")))
					&& (!(lines[i].equals("")))) {
				if (lines[i].matches(".*\\uF02A.*")) {
					partCopyRight = true;
					tempAuthor = lines[i].substring(0, lines[i].length() - 4);

					if (index == 9) {
						tempAuthor = tempAuthor + " und " + lines[i + 4];
					}

					if (index == 43) {
						tempAuthor = tempAuthor + " und " + lines[i + 3];
					}
				}
				if ((!partCopyRight) && (!(lines[i].equals(tempAuthor)))) {
					// marking of paragraph of enumerations
					if (lines[i].matches("\\u2002? ?\\d+\\.\\u2002 ?\\u0007?.*")) {
						lines[i] = lines[i].replaceAll("\\u2002", "");
						lines[i] = lines[i].replaceAll("\\u0007", " ");

						if (lines[i].startsWith(" ")) {
							lines[i] = lines[i].replaceFirst(" ", "");
						}

						lines[i] = "___" + lines[i];
					}

					text = text + lines[i] + "\n";
				}

				if (lines[i].equals(tempAuthor)) {
					partCopyRight = false;
				}
			}
		}

		text = text.replaceAll("\\n+", "\n");

		if (text.startsWith("\n")) {
			text = text.replaceFirst("\\n", "");
		}
		if (text.endsWith("\n")) {
			text = text.substring(0, text.length() - 1);
		}

		// normalize enumerations
		text = text.replaceAll("\\u0007", ""); // BEL
		text = text.replaceAll("\\u2002", ""); // En space
		text = text.replaceAll("\\u000C", ""); // Form Feed

		// remove numbers in [] - bibliographical references
		text = text.replaceAll(" ?\\[\\d+[\\, \\d]*\\]", "");
		text = text.replaceAll(" ?\\[\\d+\\-\\d+\\]", "");

		// correct mismatched parsing with bullet points
		text = text.replaceAll("\\u2022\t\n", "\n");
		text = text.replaceAll("\\u2022\t ", "\u2022 ");

		text = text.replaceAll("\\nHypoxie\\?", "\n\u2022 Hypoxie?");
		text = text.replaceAll("\\nHypovolämie\\?", "\n\u2022 Hypovolämie?");
		text = text.replaceAll("\\nHypothermie\\?", "\n\u2022 Hypothermie?");
		text = text.replaceAll("\\nHypo\\-\\, Hyperkaliämie", "\n\u2022 Hypo-, Hyperkaliämie");
		text = text.replaceAll("\\nHerzbeuteltamponade\\?", "\n\u2022 Herzbeuteltamponade?");
		text = text.replaceAll("\\nIntoxikation\\?", "\n\u2022 Intoxikation?");
		text = text.replaceAll("\\nThromboembolie\\?", "\n\u2022 Thromboembolie?");
		text = text.replaceAll("\\nSpannungspneumothorax\\?", "\n\u2022 Spannungspneumothorax?");

		text = text.replaceAll("\\nA \\(Airway\\)", "\n\u2022 A (Airway)");
		text = text.replaceAll("\\nB \\(Breathing\\)", "\n\u2022 B (Breathing)");
		text = text.replaceAll("\\nC \\(Circulation\\)", "\n\u2022 C (Circulation)");
		text = text.replaceAll("\\nD \\(Disability\\)", "\n\u2022 D (Disability)");
		text = text.replaceAll("\\nE \\(Exposure/Environment\\)", "\n\u2022 E (Exposure/Environment)");

		// space error in pdf
		text = text.replaceAll("-Kreislauf stabil", "- Kreislauf stabil");

		// This is an error by authors of the book.
		text = text.replaceAll("eines sog- QuickTrach", "eines sog. QuickTrach");

		// remove all newlines and normalize the newlines
		// but save the part with the discussion "Diskussion"
		text = text.replaceAll("\nDiskussion\n", "\n__Diskussion__");
		text = text.replaceAll("\n", "\u0020");
		text = text.replaceAll(" \\u2022", "\n\u2022");
		text = text.replaceAll(" __Diskussion__", "\nDiskussion\n");

		text = text.replaceAll("\u2022", "-"); // bullet •

		text = text.replaceAll("\u2009", "\u0020"); // "Thin space" -> to
													// "normal spache
		text = text.replaceAll("\u00AD", ""); // soft hyphen

		// remove the internal marking of enumerations
		text = text.replaceAll("___", "");

		text = text.replaceAll("\u2013", "-"); // En dash −
		text = text.replaceAll("\u2212", "-"); // Minus −

		// split the text into the parts of the description and discussion
		// and make the entries
		lines = text.split("\nDiskussion\n");

		String caseText = LanguageTools.removeHyphenNew(lines[0]);
		String discText = LanguageTools.removeHyphenNew(lines[1]);

		TextDocument documentCase = new TextDocument();
		documentCase.setText(caseText);
		documentCase.setType(TYPE_1);
		documentCase.topic.add(TOPIC);
		documentCase.setHeading(tableOfContents.get(index - 1));
		documentCase.setSource(SOURCE);
		documentCase.setIdLong(SOURCE_SHORT + "-" + ((index * 2) - 1));

		// in relation of ...
		ArrayList<String> cRelList = new ArrayList<String>();
		cRelList.add(SOURCE_SHORT + "-" + (index + 1));
		documentCase.inRelationOf = cRelList;

		listDocuments.add(documentCase);

		TextDocument documentDiscuss = new TextDocument();
		documentDiscuss.setText(discText);
		documentDiscuss.setType(TYPE_2);
		documentDiscuss.topic.add(TOPIC);
		documentDiscuss.setHeading(tableOfContents.get(index - 1));
		documentDiscuss.source = SOURCE;
		documentDiscuss.setIdLong(SOURCE_SHORT + "-" + (index * 2));

		// in relation of ...
		ArrayList<String> dRelList = new ArrayList<String>();
		dRelList.add(SOURCE_SHORT + "-" + (index));
		documentDiscuss.inRelationOf = dRelList;

		listDocuments.add(documentDiscuss);

		return listDocuments;
	}
}
