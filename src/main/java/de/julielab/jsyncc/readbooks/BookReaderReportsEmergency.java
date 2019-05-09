package de.julielab.jsyncc.readbooks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import de.julielab.jsyncc.tools.FileTools;
import de.julielab.jsyncc.tools.LanguageTools;

public class BookReaderReportsEmergency {
	public static String BOOK_5 = FileTools.getSinglePDFFileName("src/main/resources/books/05-Fallbeispiele-Notfallmedizin");
	public static String source = "Wenzel, V. (2015). Fallbeispiele Notfallmedizin: Einprägsam-spannend-mit Lerneffekt. Springer.";
	public static String sourceShort = "Wenzel2015Emergency";

	public static int indexLocal = 1;

	public static ArrayList<TextDocument> listDocuments = new ArrayList<TextDocument>();
	public static ArrayList<CheckSum> listCheckSum = new ArrayList<CheckSum>();

	public static ArrayList<String> tableOfContents = new ArrayList<String>();
	public static ArrayList<String> tableOfAuthors = new ArrayList<String>();

	private static int elementIndex = 0;

	public static ArrayList<TextDocument> extractContent() throws IOException, InterruptedException {
		if (BOOK_5 == null) {
			return listDocuments;
		}
		ProcessBuilder pb = new ProcessBuilder("pdftotext", BOOK_5);
		Process p = pb.start();
		p.waitFor();

		List<String> lines = Files.readAllLines(Paths.get(BOOK_5.replaceAll(".pdf", ".txt")));

		boolean readElements = false;
		boolean readTableOfContents = false;

		String actElement = "";

		for (int i = 0; i < lines.size(); i++) {
			// get Table of Contents

			if (lines.get(i).matches("\\u000CInhaltsverzeichnis")) {
				readTableOfContents = true;
			}

			if ((readTableOfContents) && (lines.get(i).matches("\\d+\\u2002?\\u2003?.*"))) {
				String contentEntry = lines.get(i);

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

				tableOfAuthors.add(lines.get(i + 1));
			}

			// get Content of Single Texts
			if ((lines.get(i).matches("Literatur")) || (lines.get(i).matches("Fazit"))
					|| (lines.get(i).matches("Letzte Worte"))) {
				readElements = false;
			}

			if (readElements) {
				actElement = actElement + "\n" + lines.get(i);
			}

			if (lines.get(i).startsWith("77 ")) {
				if (!(actElement.equals(""))) {
					normalizeElement(actElement);
					actElement = "";
				}

				actElement = actElement + "\n" + lines.get(i);

				readElements = true;
			}

			// Last element
			if (lines.get(i).endsWith("Sachverzeichnis")) {
				if (!(actElement.equals(""))) {
					normalizeElement(actElement);
					actElement = "";
				}

				readElements = false;
			}
		}

		return listDocuments;
	}

	private static String normalizeElement(String element) {
		element = element.replaceFirst("77 ", "");
		element = element.replaceAll("\\u0003", "");
		element = element.replaceAll("\\u00A0", " "); // No-Break Space
		element = element.replaceAll("\\u2013", "-"); // – / En dash

		String[] lines = element.split("\n");

		element = "";
		boolean partCopyRight = false;
		String tempAuthor = "";

		for (int i = 0; i < lines.length; i++) {
			if ((!(lines[i].matches("\\u000C\\d+\\u2003[\\p{Alnum}öäüÖÄÜß\\s\\p{Punct}]+")))
					&& (!(lines[i].matches("\\u000C\\d+"))) && (!(lines[i].matches("\\d+")))
					&& (!(lines[i].equals("")))) {
				if (lines[i].matches(".*\\uF02A.*")) {
					partCopyRight = true;
					tempAuthor = lines[i].substring(0, lines[i].length() - 4);

					if (elementIndex == 8) {
						tempAuthor = tempAuthor + " und " + lines[i + 4];
					}

					if (elementIndex == 42) {
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

					element = element + lines[i] + "\n";
				}

				if (lines[i].equals(tempAuthor)) {
					partCopyRight = false;
				}
			}
		}

		element = element.replaceAll("\\n+", "\n");

		if (element.startsWith("\n")) {
			element = element.replaceFirst("\\n", "");
		}
		if (element.endsWith("\n")) {
			element = element.substring(0, element.length() - 1);
		}

		// normalize enumerations
		element = element.replaceAll("\\u0007", ""); // BEL
		element = element.replaceAll("\\u2002", ""); // En space
		element = element.replaceAll("\\u000C", ""); // Form Feed

		// remove numbers in [] - bibliographical references
		element = element.replaceAll(" ?\\[\\d+[\\, \\d]*\\]", "");
		element = element.replaceAll(" ?\\[\\d+\\-\\d+\\]", "");

		// correct mismatched parsing with bullet points
		element = element.replaceAll("\\u2022\t\n", "\n");
		element = element.replaceAll("\\u2022\t ", "\u2022 ");

		element = element.replaceAll("\\nHypoxie\\?", "\n\u2022 Hypoxie?");
		element = element.replaceAll("\\nHypovolämie\\?", "\n\u2022 Hypovolämie?");
		element = element.replaceAll("\\nHypothermie\\?", "\n\u2022 Hypothermie?");
		element = element.replaceAll("\\nHypo\\-\\, Hyperkaliämie", "\n\u2022 Hypo-, Hyperkaliämie");
		element = element.replaceAll("\\nHerzbeuteltamponade\\?", "\n\u2022 Herzbeuteltamponade?");
		element = element.replaceAll("\\nIntoxikation\\?", "\n\u2022 Intoxikation?");
		element = element.replaceAll("\\nThromboembolie\\?", "\n\u2022 Thromboembolie?");
		element = element.replaceAll("\\nSpannungspneumothorax\\?", "\n\u2022 Spannungspneumothorax?");

		element = element.replaceAll("\\nA \\(Airway\\)", "\n\u2022 A (Airway)");
		element = element.replaceAll("\\nB \\(Breathing\\)", "\n\u2022 B (Breathing)");
		element = element.replaceAll("\\nC \\(Circulation\\)", "\n\u2022 C (Circulation)");
		element = element.replaceAll("\\nD \\(Disability\\)", "\n\u2022 D (Disability)");
		element = element.replaceAll("\\nE \\(Exposure/Environment\\)", "\n\u2022 E (Exposure/Environment)");

		element = element.replaceAll("-Kreislauf stabil", "- Kreislauf stabil"); // space
																					// error
																					// in
																					// pdf

		element = element.replaceAll("eines sog- QuickTrach", "eines sog. QuickTrach"); // This
																						// is
																						// an
																						// error
																						// by
																						// authors
																						// of
																						// the
																						// book.

		// remove all newlines and normalize the newlines
		// but save the part with the discussion "Diskussion"
		element = element.replaceAll("\nDiskussion\n", "\n__Diskussion__");
		element = element.replaceAll("\n", "\u0020");
		element = element.replaceAll(" \\u2022", "\n\u2022");
		element = element.replaceAll(" __Diskussion__", "\nDiskussion\n");

		element = element.replaceAll("„", "\"");
		element = element.replaceAll("“", "\"");
		element = element.replaceAll("\u2022", "-"); // bullet •
		element = element.replaceAll("\u2009", "\u0020"); // "Thin space" -> to
															// "normal spache

		// remove the internal marking of enumerations
		element = element.replaceAll("___", "");

		// this step is for the jtbd-tokenizer
		element = element.replaceAll("»", "\"");
		element = element.replaceAll("«", "\"");
		element = element.replaceAll("„", "\"");
		element = element.replaceAll("“", "\"");
		element = element.replaceAll("\u2013", "-"); // En dash −
		element = element.replaceAll("\u2212", "-"); // Minus −

		// split the text into the parts of the description and discussion
		// and make the entries
		lines = element.split("\nDiskussion\n");

		String caseText = LanguageTools.removeHyphenNew(lines[0]);
		String discText = LanguageTools.removeHyphenNew(lines[1]);

		TextDocument documentCase = new TextDocument();
		documentCase.text = caseText;
		documentCase.type = "case description";
		documentCase.topic.add("emergency");
		documentCase.heading = tableOfContents.get(elementIndex);
		documentCase.source = source;

		documentCase.idLong = sourceShort + "-" + indexLocal;
		indexLocal++;

		BookReader.index++;

		// in relation of ...
		ArrayList<String> cRelList = new ArrayList<String>();
		cRelList.add(Integer.toString((BookReader.index) + 1));
		documentCase.inRelationOf = cRelList;

		documentCase.id = Integer.toString(BookReader.index);

		listDocuments.add(documentCase);

		CheckSum checkSumC = new CheckSum();
		// checkSumC.checkSumText =
		// DigestUtils.md5Hex(LanguageTools.removeHyphen(lines[1])); // wrong
		// --> 0
		checkSumC.checkSumText = DigestUtils.md5Hex(caseText);

		checkSumC.id = Integer.toString(BookReader.index);
		BookReader.listCheckSum.add(checkSumC);

		TextDocument documentDiscuss = new TextDocument();
		documentDiscuss.text = discText;

		documentDiscuss.type = "discussion";
		documentDiscuss.topic.add("Notfallmedizin");
		documentDiscuss.heading = tableOfContents.get(elementIndex);
		documentDiscuss.source = source;

		documentDiscuss.idLong = sourceShort + "-" + indexLocal;
		indexLocal++;

		BookReader.index++;

		// in relation of ...
		ArrayList<String> dRelList = new ArrayList<String>();
		dRelList.add(Integer.toString((BookReader.index) - 1));
		documentDiscuss.inRelationOf = dRelList;

		documentDiscuss.id = Integer.toString(BookReader.index);

		listDocuments.add(documentDiscuss);

		CheckSum checkSumD = new CheckSum();
		checkSumD.checkSumText = DigestUtils.md5Hex(discText);

		checkSumD.id = Integer.toString(BookReader.index);
		BookReader.listCheckSum.add(checkSumD);

		elementIndex = elementIndex + 1;

		return element;
	}
}
