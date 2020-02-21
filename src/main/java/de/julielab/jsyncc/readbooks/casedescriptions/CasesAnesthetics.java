package de.julielab.jsyncc.readbooks.casedescriptions;

import de.julielab.jsyncc.readbooks.BookExtractor;
import de.julielab.jsyncc.readbooks.BookReader;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.ExtractionUtils;
import de.julielab.jsyncc.tools.LanguageTools;

import org.apache.commons.lang3.exception.ContextedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CasesAnesthetics implements BookExtractor {
	private final int ID = 7;
	private final String SOURCE = BookReader.yaml.getSourceById(ID);
	private final String SOURCE_SHORT = BookReader.yaml.getSourceShortById(ID);

	public static final String BOOK = "books/07-Komplikationen-in-der-Anaesthesie/978-3-662-43440-6.pdf";
	public static final String TYPE = "CaseDescriptionLong";
	public static final String TOPIC = "Anästhesie";

	@Override
	public List<TextDocument> extractContent(String plainText) {
		List<TextDocument> listDocuments = new ArrayList<>();

		List<String> tableOfContents = new ArrayList<>();
		List<ArrayList<String>> tableOfTopics = new ArrayList<ArrayList<String>>();

		int index = 0;

		plainText = plainText.replaceAll("\ufffd", " ");
		String[] lines = plainText.split("\n");

		boolean readTableOfContents = false;
		boolean readFirstPart = false;
		boolean readSecondStep = false;
		boolean readLastPart = false;

		String actTopics = "";
		String text = "";
		String textsecondPart = "";
		String textlastPart = "";

		for (int i = 0; i < lines.length; i++) {
			if (readTableOfContents) {
				if (lines[i].startsWith(" z Fall")) {
					String actHeading = lines[i].replaceAll(" z Fall\\u00A0?\\s?\\d+\\s\\u2013?\\s", "");
					tableOfContents.add(actHeading);

					if (!(actTopics.equals(""))) {
						String[] temp = actTopics.split("–");
						ArrayList<String> tempList = new ArrayList<>();

						for (int j = 0; j < temp.length; j++) {
							temp[j] = temp[j].replaceAll("\\s+", " ");
							temp[j] = temp[j].replaceAll("-\\s", "");
							temp[j] = temp[j].replaceFirst("\\s", "");
							temp[j] = temp[j].substring(0, temp[j].length() - 1);
							tempList.add(temp[j]);
						}

						tableOfTopics.add(tempList);
					}

					actTopics = "";

				} else {
					if ((!(lines[i].equals(""))) && (!(lines[i].contains("Übersicht der Fallbeispiele")))
							&& (!(lines[i].matches("XV"))) && (!(lines[i].contains("Inhalt der Fallbeispiele")))) {
						actTopics = actTopics + " " + lines[i];
					}
				}
			}

			if (lines[i].startsWith("Übersicht der Fallbeispiele")) {
				readTableOfContents = true;
			}

			if ((lines[i].startsWith("Inhalt der Fallbeispiele")) && readTableOfContents) {
				readTableOfContents = false;

				String[] temp = actTopics.split(" – ");
				ArrayList<String> tempList = new ArrayList<>();

				for (int j = 0; j < temp.length; j++) {
					temp[j] = temp[j].replaceAll("\\s+", " ");
					temp[j] = temp[j].replaceAll("-\\s", "");
					temp[j] = temp[j].replaceFirst("\\s", "");
					temp[j] = temp[j].substring(0, temp[j].length() - 1);
					tempList.add(temp[j]);
				}

				tableOfTopics.add(tempList);
			}

			if (lines[i].matches("\\d+\\.1\\.1.*")) {
				readFirstPart = false;
			}

			if (readFirstPart) {
				if ((!(lines[i].matches("\\d+"))) && (!(lines[i].matches("\\d+\\.\\d\\. .*")))
						&& (!(lines[i].matches("\\d+ \\d")))
						&& (!(lines[i].matches("\\d+.\\d+( |\u00A0)\u2022\u00A0 .*")))
						&& (!(lines[i].matches("\\d+ Kapitel.*")))) {
					text = text + "\n" + lines[i];
				}
			}

			if ((lines[i].startsWith("Was geschah")) || (lines[i].startsWith("…Was geschah"))) {
				readFirstPart = true;
			}

			if ((readSecondStep)
					&& ((lines[i].matches("\\d+\\.\\d\\. .*")) || (lines[i].matches("\\d+\\.\\d+\\.\\d+ .*")))) {
				readSecondStep = false;
			}

			if (readSecondStep) {

				if (lines[i].startsWith(". Tab. 2.1 ")) {
					i = i + 47;
				} else if (lines[i].startsWith(" 5 Intrathorakale Ursachen:")) {
					i = i + 38;
				} else if (lines[i].startsWith(
						". Tab. 7.2 Indikationen für einen intraossären Zugang in der Kinderanästhesie. (Mod. nach [10,11])")) {
					i = i + 42;
				} else if (lines[i].startsWith(". Tab. 9.1 Einteilung der Schweregrade einer ")) {
					i = i + 66;
				} else if (lines[i].startsWith(". Tab. 15.1  Einfluss der Sauerstoffflussrate bei ")) {
					i = i + 49;
				} else if (lines[i].startsWith("10 II")) {
					i = i + 26;
				} else if (lines[i].startsWith(". Tab. 16.1 Modifizierter Mallampati-Score")) {
					i = i + 102;
				} else if (textsecondPart.endsWith("wort an Frau Scholz.")) {
					i = i + 38;
				} else if (lines[i].startsWith("% 10 mg")) {
					i = i + 11;
				} else if (lines[i].startsWith(". Tab. 20.1 Höchstdosen einzelner Lokalanäs-")) {
					i = i + 47;
				} else if (lines[i].startsWith("Empfehlungen zur Anwendung von Hyd-")) {
					i = i + 36;
				} else if (lines[i].equals("OI")) {
					i = i + 16;
				} else if (lines[i].equals("II")) {
					i = i + 20;
				} else if (lines[i].startsWith("fohlenen und markierten Insertionstiefe)")) {
					i = i + 24;
				} else if (lines[i].startsWith("Halbwertszeit Vor Punktion/Ka-")) {
					i = i + 38;
				} else if (lines[i].startsWith("Anforderungen der DIVI an den begleiten-")) {
					i = i + 27;
				} else if (lines[i].equals("0")) {
					i = i + 47;
				} else if (lines[i].equals("zu geringe Dämpfung")) {
					i = i + 21;
				} else if (lines[i].equals("lebensbedrohliche")) {
					i = i + 64;
				} else if (lines[i].equals(". Tab. 32.1 Einteilung der Niereninsuffizienz")) {
					i = i + 32;
				} else if (lines[i].equals("Trigger")) {
					i = i + 43;
				} else if (lines[i].equals(". Tab. 35.2  APGAR-Score nach Virginia Apgar [4]")) {
					i = i + 35;
				} else if (lines[i].equals(". Tab. 35.3 Differenzialdiagnosen für Bewusstlosigkeit")) {
					i = i + 29;
				} else {
					textsecondPart = textsecondPart + "\n" + lines[i];
				}
			}

			if (lines[i].contains("so geht es weiter")) {
				readSecondStep = true;
				textsecondPart = textsecondPart + "\nBREAKPARAGRAPH\n";
			}

			if ((readLastPart)
					&& ((lines[i].matches("\\d+\\.\\d\\.? .*")) || (lines[i].matches("\\d+\\.\\d+\\.\\d+ .*")))) {
				readLastPart = false;

				String finalText = cleanText(text, plainText) + "\n" + cleanText(textsecondPart, plainText) + "\n"
						+ cleanText(textlastPart, plainText);

				TextDocument textDocument = new TextDocument();
				textDocument.setText(finalText);
				textDocument.getTopic().add(TOPIC);
				textDocument.setType(TYPE);

				index++;
				textDocument.setIdLong(SOURCE_SHORT + "-" + index);
				textDocument.setSource(SOURCE);

				listDocuments.add(textDocument);

				text = "";
				textsecondPart = "";
				textlastPart = "";

			}

			if (readLastPart) {
				if (lines[i].equals("II")) {
					i = i + 19;
				} else if (lines[i].startsWith(". Abb. 15.2  Seitliche Röntgenaufnahmen")) {
					i = i + 10;
				} else if (lines[i].startsWith("Glukose")) {
					i = i + 25;
				} else if (lines[i].startsWith(". Tab. 35.4 Inzidenzen von intrazerebralen")) {
					i = i + 59;
				} else {
					textlastPart = textlastPart + "\n" + lines[i];
				}
			}

			if (lines[i].contains("das Ende des Falls")) {
				readLastPart = true;
			}
		}

		return listDocuments;
	}

	public static String cleanText(String text, String plainText) {
		if (text.startsWith("\nEs war 19.15")) {
			text = text.replaceAll("Mögliche Ursachen präoperativer.*\\n(.*\\n)*", "\n");
			int begin = plainText.indexOf("Schwellung hatte im");
			int end = plainText.indexOf("25.1.1 Was");
			text = text + "\n" + plainText.substring(begin, end);
		}

		text = text.replaceAll("\u00A0", "\u0020");
		text = text.replaceAll(" \\n", "\n");
		text = text.replaceAll("\n.*•.*\n", "\n");
		text = text.replaceAll("\n\\d+( \\d+)?\n", "\n");
		text = text.replaceAll("\u0020+", "\u0020");
		text = text.replaceAll(" ?\\(\\. Abb\\. \\d+\\.\\d+\\)", "");
		text = text.replaceAll("\n\\.", ". ");

		text = text.replaceAll("\u2013", "-"); // En dash −
		text = text.replaceAll("\u2212", "-"); // Minus −

		if (!(text.contains("\n 5 "))) {
			text = text.replaceAll("\n+", " ");
		} else {
			String[] lines = text.split("\n");
			text = "";

			for (int i = 0; i < lines.length; i++) {
				if (lines[i].startsWith(" 5 ")) // Absatz mit Bullet, Codierung
												// == "5"
				{
					lines[i] = lines[i].replaceFirst(" 5 ", "- ");

					if (text.endsWith("\n")) {
						text = text + lines[i];
					} else {
						text = text + "\n" + lines[i];
					}
				} else {
					if (text.equals("")) {
						text = lines[i];
					} else {
						if (text.endsWith("\n")) {
							text = text.substring(0, text.length() - 1);
							text = text + "" + lines[i];
						} else {
							text = text + " " + lines[i];
						}
					}
				}
			}

			text = text.replaceAll("bei Raumluft: 97%\\.", "bei Raumluft: 97%.\n");
			text = text.replaceAll("4 l Sauerstoff.", "4 l Sauerstoff.\n");
			text = text.replaceAll("70-120 mg/dl\\)\\.", "70-120 mg/dl).\n");
			text = text.replaceAll("Norm 37-47%\\.", "Norm 37-47%.\n");
			text = text.replaceAll("\\(Norm < 10 mg/l\\)\\.", "(Norm < 10 mg/l).\n");
			text = text.replaceAll("\\(Norm 95-98 %\\).", "(Norm 95-98 %).\n");
			text = text.replaceAll("Es wurden 2,2 g", "\nEs wurden 2,2 g");
			text = text.replaceAll("\\(Norm 3,5-5,0 mmol/l\\)\\.", "(Norm 3,5-5,0 mmol/l).\n");
			text = text.replaceAll("Herzfrequenz: 120/min\\.", "Herzfrequenz: 120/min.\n");
			text = text.replaceAll("Ramipril\\.", "Ramipril.\n");
			text = text.replaceAll("Weiter waren auf dem", "\nWeiter waren auf dem");
			text = text.replaceAll("HWS-Schleudertrauma\\.", "HWS-Schleudertrauma.\n");
			text = text.replaceAll("unter Raumluft\\.", "unter Raumluft.\n");
			text = text.replaceAll("\\(Norm 3,6-9,8 Gpt/l\\)\\.", "(Norm 3,6-9,8 Gpt/l).\n");
			text = text.replaceAll("- Entzugssymptomatik", "- Entzugssymptomatik\n");
			text = text.replaceAll("20 mg: 0-0-1.", "20 mg: 0-0-1.\n");
			text = text.replaceAll("Es gab keinen Grund", "\nEs gab keinen Grund");
			text = text.replaceAll("Volumentherapie zum Declamping", "\nVolumentherapie zum Declamping");
			text = text.replaceAll("\\(Norm 95-98%\\)\\.", "(Norm 95-98%).\n");
			text = text.replaceAll("Zugang links \\(18 G\\)\\.", "Zugang links (18 G).\n");
			text = text.replaceAll("PEEP: 10 cmH2O\\.", "PEEP: 10 cmH2O\\.\n");
			text = text.replaceAll("Insulin i\\.v\\. erhalten\\.", "Insulin i.v. erhalten.\n");
			text = text.replaceAll("\\(Norm 70-99 mg/dl\\)\\.", "(Norm 70-99 mg/dl).\n");
			text = text.replaceAll(" Sinusrhythmus.", " Sinusrhythmus.\n");
			text = text.replaceAll("fiebrigen Harnwegsinfekts\\.", "fiebrigen Harnwegsinfekts.\n");

		}

		text = text.replaceAll("in \\. Abb\\. 10\\.3 ", "");
		text = text.replace("in \\. Abb\\. 24\\.1 ", "");
		text = text.replaceAll(" und auf \\. Abb\\. 31\\.4", "");

		text = text.replaceAll("BREAKPARAGRAPH", "\n");
		text = text.replaceAll(" +", " ");
		text = text.replaceAll(" \n", "\n");
		text = text.replaceAll("\n ", "\n");
		text = text.replaceAll("\n+", "\n");

		text = text.replaceAll("HCO3 -", "HCO3-");
		text = text.replaceAll("Na + ", "Na+");
		text = text.replaceAll("K + ", "K+");

		if (text.startsWith(" ")) {
			text = text.replaceFirst(" ", "");
		}
		if (text.startsWith("\n")) {
			text = text.replaceFirst("\n", "");
		}
		if (text.endsWith(" ")) {
			text = text.substring(0, text.length() - 1);
		}

		text = LanguageTools.removeHyphenNew(text);
		return text;
	}

	@Override
	public String parseBook(Path pdfPath) throws ContextedException {
		// PDFtoTXTbyTika
		String plainText = ExtractionUtils.getContentByTika(pdfPath.toString());

		try {
			Files.write(Paths.get(pdfPath.toString().replaceAll("pdf", "txt")), plainText.getBytes());
		} catch (IOException e) {
			throw new ContextedException(e);
		}
		return plainText;
	}

	@Override
	public boolean validateText(String plainText) {
		return plainText.contains("ISBN 978-3-662-43440-6");
	}

}
