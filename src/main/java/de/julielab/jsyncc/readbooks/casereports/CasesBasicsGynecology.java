package de.julielab.jsyncc.readbooks.casereports;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.ExtractionUtils;
import de.julielab.jsyncc.tools.LanguageTools;

import org.apache.commons.lang3.exception.ContextedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CasesBasicsGynecology
{
	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{
		ArrayList<TextDocument> textDocuments = new ArrayList<>();
		ArrayList<Integer> exceptions = new ArrayList<>(Arrays.asList(0, 1, 4, 7, 9, 13, 18, 23, 24, 25, 26));

		String plainText = ExtractionUtils.getContentByTika(bookProperties.bookPath);
		try
		{
			Files.write(Paths.get(bookProperties.bookPath.replaceAll("pdf", "txt")), plainText.getBytes());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if (validateText(plainText))
		{
			Pattern start = Pattern.compile("(Fallbeispiel(\\p{Z}\\u0028\\u002E\\p{Z}Abb\\u002E\\p{Z}\\d+\\u002E\\d+\\u0029)?|Fallbericht)\n", Pattern.MULTILINE);
			Pattern end = Pattern.compile("(\n\n\n|Übungsfragen|((\\p{Z}+\\u002E\\p{Z}+A(\\p{Z})?bb\\u002E\\p{Z}+)?\\d(\\u002E\\d)+\\p{Z}+(\\p{Alpha}|ä|ü|ö|ß|\\p{Z})+))", Pattern.MULTILINE);

			Matcher startMatcher = start.matcher(plainText);
			Matcher endMatcher = end.matcher(plainText);

			int index = -2;

			while (startMatcher.find())
			{
				int startOffset = startMatcher.end();
				endMatcher.find(startOffset);
				int endOffset = endMatcher.start();
				String text = plainText.substring(startOffset, endOffset).trim();

				if (exceptions.contains(index + 1))
				{
					String[] secondList = findSecondPart(index + 1);
					int secondStart = plainText.indexOf(secondList[0]);
					int secondEnd = plainText.indexOf(secondList[1]) + secondList[1].length();

					text = text + " " + plainText.substring(secondStart, secondEnd).trim();
				}

				index++;
				text = cleanText(index, text);

				if (0 <= index)
				{
					TextDocument textDocument = new TextDocument();
					
					if (text.startsWith("Normale Geburt, Teil 2"))
					{
						textDocument = textDocuments.get(textDocuments.size() - 1);
						textDocuments.remove(textDocuments.size() - 1);
						String newText = textDocument.getText() + "\n" + text;
						newText = newText.replaceAll("Normale Geburt\\, Teil \\d ", "");
						textDocument.setText(newText);
						index--;
					}
					else
					{
						textDocument = createTextDocument(index, text, bookProperties);
					}

					textDocuments.add(textDocument);
				}
			}
		}

		return textDocuments;
	}

	private static String cleanText(int index, String text)
	{
		text = text.replaceAll("(\\p{Z}?\\u0028(\\u002E\\p{Z}+Abb\\u002E\\p{Z}+\\d+\\u002E\\d+(\\u002C\\p{Z})?)+\\u0029)", "");

		if (index == 5)
		{
			int endFirst = text.indexOf(" Defekt handelt, ist keine Lappenplastik nötig und ");
			int startSecond = text.indexOf("G1, R0), VIN I und III und ein Lichen sclerosus. Die ");
			text = text.substring(0, endFirst) + text.substring(startSecond);
		}
		else if (index == 7)
		{
			text = text + "\nsprechen!";
		}
		else if (index == 22)
		{
			int endIndex = text.indexOf(" Im Ultraschall   kann die Berechnung des Entbindungs-");
			text = text.substring(0, endIndex);
		}
		else if (index == 25)
		{
			text = text.replaceAll("\\u0028vgl\\u002E.+", "");
			text = text.replaceAll("CTG.+\\u00AB\\u0029", "");
			text = text.replaceAll("\\p{Z}+\\.\\p{Z}+Abb\\.\\p{Z}15\\.21\\p{Z}.+", "");
			text = text.replaceAll(" sporaidischen Akzelationen \\(blaue Pfeile\\) ", "");
			text = text.replaceAll(" \\u0028weiter.+\\.4\\u0029", "");
		}
		else if (index == 26)
		{
			text = text.replaceAll("Teil 2", "Teil 2\n");
		}

		text = text.replaceAll("\\n 5 ", "- ");
		text = text.replaceAll("T amoxifen", "Tamoxifen");
		text = text.replaceAll("Fi gur", "Figur");
		text = text.replaceAll("Anti biose", "Antibiose");

		String[] lines = text.split("\n");
		text = "";

		for (int i = 0; i < lines.length; i++)
		{
			if (
					(!(lines[i].isEmpty()))
				&&
					(!(lines[i].startsWith("- ")))
				)
			{
				text = text + " " + lines[i];
			}
			else
			{
				text = text + "\n" + lines[i];
			}
		}

		text = text.replaceAll("\u00A0", " ");
		text = text.replaceAll("  +", " ");
		text = text.replaceAll("\\n ", "\n");
		text = text.replaceAll(" \\.", ".");

		if (index != 1)
		{
			text = text.replaceAll("\\n", " ");
		}
		
		if (text.startsWith(" "))
		{
			text = text.replaceFirst(" ", "");
		}

		text = LanguageTools.removeHyphenNew(text);

		return text;
	}

	private static String[] findSecondPart(int index)
	{
		String[] list = new String[2];

		switch (index)
		{
			case 0:
				list[0] = "  Sie werden die Patientin zur Besprechung der";
				list[1] = "psychologische Betreuung kann sinnvoll sein. ";
				break;
			case 1:
				list[0] = "knoten (pN0) wird keine adjuvante Chemo-";
				list[1] = "ein Rezidiv. ";
				break;
			case 4:
				list[0] = " Defekt handelt, ist keine Lappenplastik nötig und ";
				list[1] = "kein Rezidiv auf. ";
				break;
			case 7:
				list[0] = "der Ursprung des unangenehmen Geruchs! Bei ";
				list[1] = " Frauenarzt eine neue Verhütungsmethode zu be-";
				break;
			case 9:
				list[0] = "außerdem zur Kontrolle der Blutzuckerwerte bei ";
				list[1] = "reduktion, zu widmen. ";
				break;
			case 13:
				list[0] = "bei V. a. paralytischen Ileus vorsichtig mit Pas-";
				list[1] = "3-monatlicher unauffälliger Nachsorge. ";
				break;
			case 18:
				list[0] = "Langzyklus einnehmen, das bedeutet, nur ";
				list[1] = "scheiden. ";
				break;
			case 23:
				list[0] = " Eiweißausscheidung vorgenommen werden. ";
				list[1] = "Laborwerte hatten sie sich komplett normalisiert. ";
				break;
			case 24:
				list[0] = "(. Abb. 15.20). Umgehend wird der Patientin ";
				list[1] = "intensivstation im Wärmebett über wacht. ";
				break;
			case 25:
				list[0] = "Patientin ist ein normaler Schwangerschaftsver-";
				list[1] = "mittel bekommt (weiter 7 Abschn. 15.9.4). ";
				break;
			case 26:
				list[0] = "den APGAR 4/7/8, der Nabelschnur-pH beträgt ";
				list[1] = " Eiseninfusion. ";
				break;
		}
		return list;
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
		textDocument.setIdLong(bookProperties.getSourceShort() + "-" + (index+1));
		textDocument.setTopic(bookProperties.topics);
		textDocument.setSourcShort(bookProperties.sourceShort);
		textDocument.setBookId(bookProperties.bookId);

		return textDocument;
	}

	public static boolean validateText(String plainText)
	{
		return plainText.contains("ISBN 978-3-662-52808-2");
	}
}
