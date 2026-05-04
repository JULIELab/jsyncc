package de.julielab.jsyncc.readbooks.casereports;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.ExtractionUtils;
import de.julielab.jsyncc.tools.LanguageTools;
import de.julielab.jsyncc.tools.NormalizationUtils;

import org.apache.commons.lang3.exception.ContextedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CasesGeneralMedicine
{
	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{
		String plainText = ExtractionUtils.getContentByTika(Paths.get(bookProperties.bookPath));
		try {
			Files.write(Paths.get(bookProperties.bookPath.replaceAll("pdf", "txt")), plainText.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList<TextDocument> textDocuments = new ArrayList<>();

		ArrayList<Integer> exceptionSimple = new ArrayList<>(Arrays.asList(4, 6, 24, 28, 34, 38, 46, 50, 57, 66, 79, 81, 88, 98, 100, 108, 112));
		ArrayList<Integer> exceptionComplex = new ArrayList<>(Arrays.asList(19, 21, 39, 41, 43, 53, 68, 69, 84, 91, 93, 101, 109));

		Pattern start = Pattern.compile("((Fallbeispiel)(\\u0020)?\n)", Pattern.MULTILINE);
		Pattern end = Pattern.compile("\n\n", Pattern.MULTILINE);

		Matcher startMatcher = start.matcher(plainText);
		Matcher endMatcher = end.matcher(plainText);

		if (validateText(plainText))
		{
			int index = -1;

			while (startMatcher.find())
			{
				int startOffset = startMatcher.end();
				endMatcher.find(startOffset);
				int endOffset = endMatcher.start();

				String text;

				if (exceptionSimple.contains(index))
				{
					endMatcher.find(endOffset + 1);
					int secondEndOffset = endMatcher.start();

					text = plainText.substring(startOffset, secondEndOffset - 1).trim();
				}
				else if (exceptionComplex.contains(index))
				{
					String[] secondList = findSecondPart(index);
					int secondStart = plainText.indexOf(secondList[0]);
					int secondEnd = plainText.indexOf(secondList[1]) + secondList[1].length();

					text = plainText.substring(startOffset, endOffset).trim() + " " + plainText.substring(secondStart, secondEnd).trim();
				}
				else
				{
					text = plainText.substring(startOffset, endOffset - 1).trim();
				}

				text = cleanText(index, text);
				index++;

				if (index > 0) // (!(index < 0))
				{
					TextDocument textDocument = createTextDocument(index, text, bookProperties);
					textDocuments.add(textDocument);
				}
			}
		}

		return concatenateDocuments(textDocuments, bookProperties);
	}

	private static String[] findSecondPart(int caseNum)
	{
		String[] list = new String[2];
		switch (caseNum) {
		case 19:
			list[0] = "Darmgeräusche, Temperatur 37,2°, RR ";
			list[1] = "ursache nicht wieder in der Praxis erschienen. ";
			break;
		case 21:
			list[0] = "Pfefferminzöl  auf Stirn und Nacken, regelmäßi-";
			list[1] = "lernen. Weitere Kontrollen wurden vereinbart. ";
			break;
		case 39:
			list[0] = "  Beim Eintreffen in der Wohnung liegt der Mann ";
			list[1] = "zuerlangen. ";
			break;
		case 41:
			list[0] = "  Die körperliche Untersuchung zeigt einen ";
			list[1] = " Patient unauffällig. ";
			break;
		case 43:
			list[0] = "eine ebenso vorhandene diskrete motorische ";
			list[1] = "und geht seiner Arbeit i n vollem Umfang nach. ";
			break;
		case 53:
			list[0] = "suchung findet sich ein linksseitig mäßig ";
			list[1] = "wieder abgesetzt werden kann. ";
			break;
		case 68:
			list[0] = "  Jetziger Rehabilitationszustand:";
			list[1] = "und arbeitet derzeit 3x/Woche fü r 2 Stunden. ";
			break;
		case 69:
			list[0] = "von völliger Beschwerdefreiheit seit 4 Tagen, ";
			list[1] = " einen Gichtanfall auslösen kann. ";
			break;
		case 84:
			list[0] = " stationär behandelt. Es zeigten sich keine ";
			list[1] = "mende Angst vor einem erneuten Anfall. ";
			break;
		case 91:
			list[0] = "  Nach stationärer 7-tägiger Entgiftung und ";
			list[1] = "stellend behandelt. ";
			break;
		case 93:
			list[0] = " 5   Die COPD wird mit Formoterol 2 × 12 μg als ";
			list[1] = "umgehen und sei zufr ieden. ";
			break;
		case 101:
			list[0] = "überwiesen und bekommt dort aufgrund einer ";
			list[1] = "sowie GFR und Blutdruck sind stabil. ";
			break;
		case 109:
			list[0] = "es noch schlimmer geworden sei, ihr Mann ";
			list[1] = "weiteren Überwachung unauffällig. ";
			break;
		}
		return list;
	}

	private static String cleanText(int index, String text)
	{
		text = text.replaceAll("\\(\\.\\p{Z}(Abb)\\.\\p{Z}\\d\\.\\d+\\)", "");

		text = text.replaceAll("vorz ustellen", "vorzustellen");
		text = text.replaceAll("Mononukleose -Schnelltest", "Mononukleose-Schnelltest");
		text = text.replaceAll("Prednisolo n", "Prednisolon");
		text = text.replaceAll(" «", "«");

		text = text.replaceAll(" , ", ", ");

		text = text.replaceAll("attestie rt", "attestiert"); // Nr. 32
		text = text.replaceAll("zufrie den", "zufrieden"); // Nr. 58
		text = text.replaceAll("…", "");
		text = text.replaceAll(" i n ", " in ");
		text = text.replaceAll("sie- nachdem", "sie \u2013 nachdem");
		text = text.replaceAll("gelingt- zu", "gelingt \u2013 zu");
		text = text.replaceAll("fü r", "für"); // Nr. 69

		text = text.replaceAll("\\n+", "\n");
		text = NormalizationUtils.normalizeNewlines(text);
		text = LanguageTools.removeHyphenNew(text);

		text = text.replaceAll(" \\.", ".");

		if (index == 6)
		{
			text = text.replaceAll("möglich:", "möglich:\n");
			text = text.replaceAll("(,( \\d\\. ))", ",\n$2");
			text = text.replaceAll("\\n ", "\n");
			text = text.replaceAll(" Der Patientin wird", "\nDer Patientin wird");
		}
		else if ( (index == 68) || (index == 93) )
		{
			text = text.replaceAll("Jetzige", "\nJetzige");
			text = text.replaceAll(" Die heute", "\nDie heute");
			text = text.replaceAll(" 5 ", "\n- ");
			text = text.replaceAll(" reduziert Der ", " reduziert\nDer ");
		}

		return text;
	}

	private static TextDocument createTextDocument(int index, String text, BookProperties bookProperties)
	{
		TextDocument textDocument = new TextDocument();
		textDocument.setText(text);

		textDocument.setSource(
				bookProperties.getTitle() + " " +
				bookProperties.getEditorAuthor() + " " +
				bookProperties.getYear() + " " +
				bookProperties.getPublisher() + " " +
				bookProperties.getDoi()
		);
		textDocument.setDocumentType(bookProperties.documentType.get(0));

		textDocument.setIdLong(bookProperties.getSourceShort() + "-" + index);
		textDocument.setTopic(bookProperties.topics);

		textDocument.setSourcShort(bookProperties.sourceShort);
		textDocument.setBookId(bookProperties.bookId);

		return textDocument;
	}

	public static boolean validateText(String plainText) {
		return (plainText.contains("978-3-662-53479-3"));
	}

	private static List<TextDocument> concatenateDocuments(List<TextDocument> textDocuments, BookProperties bookProperties)
	{
		/** This book contains "Fallbeschreibungen" (case reports) in 2 parts: a short introducing part
		 *  and second detailed part.
		 *  Only the connection of the two parts of the reports is a real report. 
		 */

		ArrayList<TextDocument> listConDoc = new ArrayList<>();

		listConDoc.add(concatDoc(textDocuments,1,1,2, bookProperties));
		listConDoc.add(concatDoc(textDocuments,2,3,5, bookProperties));
		listConDoc.add(concatDoc(textDocuments,3,4,7, bookProperties));
		listConDoc.add(concatDoc(textDocuments,4,6,8, bookProperties));
		listConDoc.add(concatDoc(textDocuments,5,9,10, bookProperties));

		listConDoc.add(concatDoc(textDocuments,6,11,12, bookProperties));
		listConDoc.add(concatDoc(textDocuments,7,13,14, bookProperties));
		listConDoc.add(concatDoc(textDocuments,8,15,16, bookProperties));
		listConDoc.add(concatDoc(textDocuments,9,17,18, bookProperties));
		listConDoc.add(concatDoc(textDocuments,10,19,20, bookProperties));

		listConDoc.add(concatDoc(textDocuments,11,21,22, bookProperties));
		listConDoc.add(concatDoc(textDocuments,12,23,24, bookProperties));
		listConDoc.add(concatDoc(textDocuments,13,25,26, bookProperties));
		listConDoc.add(concatDoc(textDocuments,14,27,29, bookProperties));
		listConDoc.add(concatDoc(textDocuments,15,28,30, bookProperties));

		listConDoc.add(concatDoc(textDocuments,16,31,32, bookProperties));
		listConDoc.add(concatDoc(textDocuments,17,33,34, bookProperties));
		listConDoc.add(concatDoc(textDocuments,18,35,36, bookProperties));
		listConDoc.add(concatDoc(textDocuments,19,37,39, bookProperties));
		listConDoc.add(concatDoc(textDocuments,20,38,40, bookProperties));

		listConDoc.add(concatDoc(textDocuments,21,41,42, bookProperties));
		listConDoc.add(concatDoc(textDocuments,22,43,44, bookProperties));
		listConDoc.add(concatDoc(textDocuments,23,45,47, bookProperties));
		listConDoc.add(concatDoc(textDocuments,24,46,48, bookProperties));
		listConDoc.add(concatDoc(textDocuments,25,49,51, bookProperties));
		
		listConDoc.add(concatDoc(textDocuments,26,50,52, bookProperties));
		listConDoc.add(concatDoc(textDocuments,27,53,54, bookProperties));
		listConDoc.add(concatDoc(textDocuments,28,55,56, bookProperties));
		listConDoc.add(concatDoc(textDocuments,29,57,58, bookProperties));
		listConDoc.add(concatDoc(textDocuments,30,59,60, bookProperties));

		listConDoc.add(concatDoc(textDocuments,31,61,62, bookProperties));
		listConDoc.add(concatDoc(textDocuments,32,63,64, bookProperties));
		listConDoc.add(concatDoc(textDocuments,33,65,67, bookProperties));
		listConDoc.add(concatDoc(textDocuments,34,66,69, bookProperties));
		listConDoc.add(concatDoc(textDocuments,35,68,70, bookProperties));

		listConDoc.add(concatDoc(textDocuments,36,71,72, bookProperties));
		listConDoc.add(concatDoc(textDocuments,37,73,74, bookProperties));
		listConDoc.add(concatDoc(textDocuments,38,75,76, bookProperties));
		listConDoc.add(concatDoc(textDocuments,39,77,78, bookProperties));
		listConDoc.add(concatDoc(textDocuments,40,79,80, bookProperties));

		listConDoc.add(concatDoc(textDocuments,41,81,82, bookProperties));
		listConDoc.add(concatDoc(textDocuments,42,83,85, bookProperties));
		listConDoc.add(concatDoc(textDocuments,43,84,86, bookProperties));
		listConDoc.add(concatDoc(textDocuments,44,87,89, bookProperties));
		listConDoc.add(concatDoc(textDocuments,45,88,90, bookProperties));

		listConDoc.add(concatDoc(textDocuments,46,91,92, bookProperties));
		listConDoc.add(concatDoc(textDocuments,47,93,94, bookProperties));
		listConDoc.add(concatDoc(textDocuments,48,95,96, bookProperties));
		listConDoc.add(concatDoc(textDocuments,49,97,99, bookProperties));
		listConDoc.add(concatDoc(textDocuments,50,98,101, bookProperties));

		listConDoc.add(concatDoc(textDocuments,51,100,102, bookProperties));
		listConDoc.add(concatDoc(textDocuments,52,103,104, bookProperties));
		listConDoc.add(concatDoc(textDocuments,53,105,106, bookProperties));
		listConDoc.add(concatDoc(textDocuments,54,107,109, bookProperties));
		listConDoc.add(concatDoc(textDocuments,55,108,110, bookProperties));

		listConDoc.add(concatDoc(textDocuments,56,111,112, bookProperties));
		listConDoc.add(concatDoc(textDocuments,57,113,114, bookProperties));

		return listConDoc;
	}
	
	private static TextDocument concatDoc(List<TextDocument> textDocuments, int index, int first, int second, BookProperties bookProperties)
	{
		TextDocument textDocument = new TextDocument();

		String firstText = textDocuments.get(first - 1).getText();
		String secondText = textDocuments.get(second - 1).getText();

		textDocument.setText(firstText + "\n" + secondText);
		textDocument.setSource(
				bookProperties.getTitle() + " " +
				bookProperties.getEditorAuthor() + " " +
				bookProperties.getYear() + " " +
				bookProperties.getPublisher() + " " +
				bookProperties.getDoi()
		);
		textDocument.setDocumentType(bookProperties.documentType.get(0));
		textDocument.setIdLong(bookProperties.getSourceShort() + "-" + index);
		textDocument.setTopic(bookProperties.topics);

		return textDocument;
	}
}
