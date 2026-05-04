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

public class CasesPediatrics
{
	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{
		String plainText = ExtractionUtils.getContentByTika(bookProperties.bookPath.toString());

		try
		{
			Files.write(Paths.get(bookProperties.bookPath.toString().replaceAll("pdf", "txt")), plainText.getBytes());
		}
		catch (IOException e)
		{
			throw new ContextedException(e);
		}

		ArrayList<TextDocument> textDocuments = new ArrayList<>();
		ArrayList<Integer> exceptionSimple = new ArrayList<>(Arrays.asList(8, 12, 21, 27, 32));
		ArrayList<Integer> exceptionComplex = new ArrayList<>(Arrays.asList(1, 10, 16, 26));
		ArrayList<Integer> exceptionSpecial = new ArrayList<>(Arrays.asList(3, 19, 30));
		ArrayList<Integer> noSections = new ArrayList<>(Arrays.asList(4, 5, 29, 30, 31, 32, 33));

		String sectionText = "";

		if (validateText(plainText))
		{
			Pattern start = Pattern.compile("(Der besondere Fall)\n", Pattern.MULTILINE);
			Pattern end = Pattern.compile("\n\n", Pattern.MULTILINE);
			Pattern endComplex = Pattern.compile("(\\d+\\u002E\\d(\\u002E\\d+)?\\p{Z}+\\p{Alpha}+.+)", Pattern.MULTILINE);

			Matcher startMatcher = start.matcher(plainText);
			Matcher endMatcher = end.matcher(plainText);
			Matcher complexMatcher = endComplex.matcher(plainText);

			int index = -1;

			while (startMatcher.find())
			{
				int startOffset = startMatcher.end();
				endMatcher.find(startOffset);

				int endOffset = endMatcher.start();
				String text;

				if (exceptionSimple.contains(index + 1))
				{
					endMatcher.find(endOffset + 1);
					int secondEndOffset = endMatcher.start();

					text = plainText.substring(startOffset, secondEndOffset).trim();
				}
				else if (exceptionComplex.contains(index + 1))
				{
					complexMatcher.find(endOffset + 1);
					int secondEndOffset = complexMatcher.start();

					text = plainText.substring(startOffset, secondEndOffset).trim();
				}
				else if (exceptionSpecial.contains(index + 1))
				{
					String[] secondList = findSecondPart(index + 1);
					int secondStart = plainText.indexOf(secondList[0]);
					int secondEnd = plainText.indexOf(secondList[1]) + secondList[1].length();

					text = plainText.substring(startOffset, endOffset).trim() + " " + plainText.substring(secondStart, secondEnd).trim();
				}
				else
				{
					text = plainText.substring(startOffset, endOffset).trim();
				}

				index++;
				text = cleanTextFirstStep(index, text);

				if (!noSections.contains(index))
				{
					sectionText = getSectionText(text, index);
					text = cleanTextSecondStep(sectionText);
				}
				else
				{
					text = cleanNormalTextSecondStep(text);
					sectionText = "";
				}

				textDocuments.add(createTextDocument(index, text, sectionText, bookProperties));
			}
		}

		return textDocuments;
	}

	private static String cleanTextFirstStep(int index, String text)
	{
		text = text.replaceAll("(Kapitel\\p{Z}\\d|\\d+\\u002E\\d+)\\p{Z}\\u00B7\\p{Z}.+", "");
		text = text.replaceAll("\\d+\n\n", "");
		text = text.replaceAll("a b\n", "");
		text = text.replaceAll("\\p{Z}\\u0028\\u002E\\p{Z}+Abb\\u002E\\p{Z}+\\d+\\u002E\\d+\\u0029", "");
		text = text.replaceAll("\\p{Z}+\\u002E\\p{Z}+Abb\\u002E.*", "");

		if (index == 10)
		{
			text = text.replaceAll("zie \\(Schädelsonographie, Längsschnitt\\)", "");
		}
		else if (index == 23)
		{
			text = text.replaceAll("106", "10^6");
		}

		return text;
	}

	private static String cleanTextSecondStep(String text)
	{
		text = text.replaceAll("</.*>\n", "");
		text = text.replaceAll("<.*>\n?", "");
		text = text.replaceAll("\\n+", "\n");

		return text;
	}

	private static String cleanNormalTextSecondStep(String text)
	{
		text = text.replaceAll("\n", " ");
		text = text.replaceAll(" +", " ");
		text = LanguageTools.removeHyphenNew(text);

		return text;
	}

	private static String[] findSecondPart(int index)
	{
		String[] list = new String[2];

		switch (index)
		{
			case 3:
				list[0] = "keine Hinweise auf irgendwelche Risikofaktoren oder Komplikationen. ";
				list[1] = "ärztin der Familie, sie blieben immer unauffällig.";
				break;
			case 19:
				list[0] = "Abduzensparese keine neurologischen Auffälligkeiten; geht jetzt mit ";
				list[1] = "verursacht hatte.";
				break;
			case 30:
				list[0] = "ist bis zu diesem Moment völlig in den Hintergrund gedrängt. Doch ";
				list[1] = "einem kurzen und dialektal verneinenden »Na, Danke«.";
				break;
		}
		return list;
	}

	private static String getSectionText(String text, int index)
	{
		text = text.replaceAll("\\nPendelfluss", " Pendelfluss");

		Pattern header = Pattern.compile("^((\\p{Lu}\\p{Lower}+(((\\p{Z}und|\\u002C)\\p{Z})?\\p{Lu}\\p{Lower}+)?)\\u002E\\p{Z}|(Klinischer Befund\\.)|(Technische Untersuchungen\\.))", Pattern.MULTILINE);
		Matcher headerMatcher = header.matcher(text);
		ArrayList<String> headers = new ArrayList<>();

		text = text.replaceAll("\n", " ");
		text = text.replaceAll(" +", " ");

		while (headerMatcher.find())
		{
			String sectionHeader = headerMatcher.group(0);
			headers.add(sectionHeader);
		}

		for (int i = 0; i < headers.size() + 1; i++)
		{
			if (i == 0)
			{
				text = text.replace(headers.get(i), "\n<" + headers.get(i).replaceAll("\\.\\p{Z}", "") + ">\n" + headers.get(i) + "\n");
			}
			else if (i == headers.size())
			{
				text = text + "\n</" + headers.get(i - 1).replaceAll("\\.\\p{Z}", "") + ">\n";
			}
			else
			{
				String headPrev = headers.get(i - 1).replaceAll("\\.\\p{Z}", "");
				text = text.replace(
						headers.get(i),
							"\n</" + headPrev + ">\n" + "<" + headers.get(i).replaceAll("\\.\\p{Z}", "")
							+ ">\n" + headers.get(i) + "\n");
			}
		}

		text = text.replaceAll("\\n ", "\n");
		text = text.replaceAll(" \\n", "\n");
		text = text.replaceAll("\\n+", "\n");

		if (index == 27)
		{
			text = text.replaceAll("<Diagnose>\\n</Therapie und Verlauf>\\n", ""); // Case 28
			text = text.replaceAll("30 g\\.\\n</Untersuchungsbefund>\\n<Diagnose>\\n", "30 g.\n</Therapie und Verlauf>\n<Diagnose>\n");
		}

		if (index == 28)
		{
			text = text.replaceAll("<Diagnose>\\n</Beurteilung>\\n", ""); // Case 29
			text = text.replaceAll("die\\n</Therapie und Verlauf>\\n<Diagnose>\\n", "die "); // Case 29
			text = text.replaceAll("Folgen\\.\\n</Diagnose>", "Folgen\\.\n</Beurteilung>");
		}

		if (text.startsWith("\n"))
		{
			text = text.replaceFirst("\n", "");
		}

		text = normalizeSections(text);

		text = LanguageTools.removeHyphenNew(text);
		return text;
	}

	private static TextDocument createTextDocument(int index, String text, String sectionText, BookProperties bookProperties)
	{
		TextDocument textDocument = new TextDocument();
		textDocument.setText(text);
		textDocument.setDocumentType(bookProperties.documentType.get(0));
		textDocument.setTopic(bookProperties.topics);
		textDocument.setSource(
				bookProperties.getTitle() + " " +
						bookProperties.getEditorAuthor() + " " +
						bookProperties.getYear() + " " +
						bookProperties.getPublisher() + " " +
						bookProperties.getDoi()
		);
		textDocument.setSourcShort(bookProperties.sourceShort);
		textDocument.setBookId(bookProperties.bookId);
		textDocument.setIdLong(bookProperties.getSourceShort() + "-" + (index+1));

		return textDocument;
	}

	public static boolean validateText(String plainText) {
		return (plainText.contains("978-3-642-34269-1"));
	}

	private static String normalizeSections(String text)
	{
		// Befunde
		text = text.replaceAll("<Befunde>", "<Befund>");
		text = text.replaceAll("</Befunde>", "</Befund>");

		text = text.replaceAll("<Diagnostik>", "<Befund>");
		text = text.replaceAll("</Diagnostik>", "</Befund>");

		text = text.replaceAll("<Laborbefunde>", "<Befund>");
		text = text.replaceAll("</Laborbefunde>", "</Befund>");

		text = text.replaceAll("<Laborwerte>", "<Befund>");
		text = text.replaceAll("</Laborwerte>", "</Befund>");

		text = text.replaceAll("<Ultraschalldiagnostik>", "<Befund>");
		text = text.replaceAll("</Ultraschalldiagnostik>", "</Befund>");

		text = text.replaceAll("<Untersuchungsbefund>", "<Befund>");
		text = text.replaceAll("</Untersuchungsbefund>", "</Befund>");

		text = text.replaceAll("<Ultraschalldiagnostik>", "<Befund>");
		text = text.replaceAll("</Ultraschalldiagnostik>", "</Befund>");

		text = text.replaceAll("<Technische Untersuchungen>", "<Befund>");
		text = text.replaceAll("</Technische Untersuchungen>", "</Befund>");

		text = text.replaceAll("<Klinischer Befund>", "<Befund>");
		text = text.replaceAll("</Klinischer Befund>", "</Befund>");

		text = text.replaceAll("<Befund und Erstversorgung>", "<Befund>");
		text = text.replaceAll("</Befund und Erstversorgung>", "</Befund>");

		// Verlauf / Epikrise
		text = text.replaceAll("<Prognose>", "<Epikrise>");
		text = text.replaceAll("</Prognose>", "</Epikrise>");

		text = text.replaceAll("<Verlauf und Therapie>", "<Epikrise>");
		text = text.replaceAll("</Verlauf und Therapie>", "</Epikrise>");

		text = text.replaceAll("<Therapie und Verlauf>", "<Epikrise>");
		text = text.replaceAll("</Therapie und Verlauf>", "</Epikrise>");

		text = text.replaceAll("<Beurteilung und Verlauf>", "<Epikrise>");
		text = text.replaceAll("</Beurteilung und Verlauf>", "</Epikrise>");

		text = text.replaceAll("<Diagnose, Verlauf>", "<Epikrise>");
		text = text.replaceAll("</Diagnose, Verlauf>", "</Epikrise>");

		text = text.replaceAll("<Therapie, Diagnose>", "<Epikrise>");
		text = text.replaceAll("</Therapie, Diagnose>", "</Epikrise>");

		// Anamnese
		text = text.replaceAll("<Anamnese und Befund>", "<Anamnese>");
		text = text.replaceAll("</Anamnese und Befund>", "</Anamnese>");

		// Prozedur und Maßnahmen
		text = text.replaceAll("<Diagnose und Therapie>", "<Prozedur und Maßnahmen>");
		text = text.replaceAll("</Diagnose und Therapie>", "</Prozedur und Maßnahmen>");

		String lines[] = text.split("\n");
		text = "";

		for (int i = 1; i < (lines.length); i++)
		{
			String last = lines[i-1].replaceAll("/", "");
			String current = lines[i];

			if ( ( !(last.equals(current)) ) && (!(lines[i].isEmpty())) )
			{
				text = text + "\n" + lines[i-1];
			}
			else
			{
				lines[i] = "";
			}
		}

		text = text + "\n" + lines[lines.length - 1];
		text = text.replaceAll("\\n\\n", "\n");

		if (text.startsWith("\n"))
		{
			text = text.replaceFirst("\n", "");
		}

		return text;
	}
}

