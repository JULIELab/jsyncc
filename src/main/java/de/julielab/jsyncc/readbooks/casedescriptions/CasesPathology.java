package de.julielab.jsyncc.readbooks.casedescriptions;

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

public class CasesPathology
{
	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{
		String plainText = ExtractionUtils.getContentByTika(bookProperties.bookPath);
		try
		{
			Files.write(Paths.get(bookProperties.bookPath.toString().replaceAll("pdf", "txt")), plainText.getBytes());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		ArrayList<TextDocument> texttDocuments = new ArrayList<>();

		if (validateText(plainText))
		{
			ArrayList<Integer> exceptionSimple = new ArrayList<>(Arrays.asList(2, 6, 8, 13, 14, 16, 19, 20, 23, 24, 29, 31));
			ArrayList<Integer> exceptionComplex = new ArrayList<>(Arrays.asList(0, 1, 7, 12, 15, 21, 22, 27, 28, 35, 39, 40, 41, 42, 44, 46, 50));

			Pattern start = Pattern.compile("((Fallbericht)\n)", Pattern.MULTILINE);
			Pattern end = Pattern.compile("\n\n", Pattern.MULTILINE);

			Matcher startMatcher = start.matcher(plainText);
			Matcher endMatcher = end.matcher(plainText);

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
					text = plainText.substring(startOffset, secondEndOffset - 1).trim();

				}
				else if (exceptionComplex.contains(index + 1))
				{

					if (index + 1 == 12 || index + 1 == 41)
					{
						endMatcher.find(endOffset + 1);
						int secondEndOffset = endMatcher.start();
						text = plainText.substring(startOffset, secondEndOffset - 1).trim();
					}
					else
					{
						text = plainText.substring(startOffset, endOffset).trim();
					}

					String[] secondList = findSecondPart(index + 1);
					int secondStart = plainText.indexOf(secondList[0]);
					int secondEnd = plainText.indexOf(secondList[1]) + secondList[1].length();

					text = text + " " + plainText.substring(secondStart, secondEnd).trim();
				} else {
					text = plainText.substring(startOffset, endOffset).trim();
				}

				index++;
				text = cleanText(index + 1, text);

				texttDocuments.add(createTextDocument(index, text, bookProperties));
			}
		}
		return texttDocuments;
	}

	private static String cleanText(int index, String text)
	{
		text = text.replaceAll("\\n\\n", "\n");
		text = NormalizationUtils.normalizeHyphensAndNewlines(text);
		text = NormalizationUtils.normalizeNewlines(text);
		
		text = text.replaceAll("((vgl\\.\\p{Z})?\\.\\p{Z}(Abb)\\.\\p{Z}\\d+\\.\\d+(\\p{Alpha}\\u2013\\p{Alpha})?(\\p{Alpha}\\u002c\\p{Alpha})?(\\p{Alpha})?)","");
		text = text.replaceAll("((\\p{Z}\\u002c\\p{Z})?7\\p{Z}Abschn\\.\\p{Z}\\d+\\.\\d(\\.\\d(\\.\\d)?)?)", "");
		text = text.replaceAll("(\\p{Z})?\\(\\)", "");

		text = text.replaceAll("\\.\\d\n", ". ");
		text = text.replaceAll("\\.\\d\\p{Z}", ". ");

		text = text.replaceAll("(\\p{Alpha})\\d\\?", "$1?");
		text = text.replaceAll("(\\p{Alpha})\\d\\p{Z}", "$1 ");
		text = text.replaceAll("(\\p{Lower})\\d\\.", "$1.");
		text = text.replaceAll("(\\u00ab)\\d\\p{Z}", "$1 ");
		text = text.replaceAll("\\d(\\u00ab)", "$1");
		text = text.replaceAll("\\p{Z}\\d\\p{Z}\\u002c\\p{Z}\\d\\p{Z}", "");

		text = text.replaceAll(" \\u00ab", "\u00ab");

		text = text.replaceAll("a t", "at"); // Pa tien ten
		text = text.replaceAll("ien te", "iente"); // Pa tien ten
		text = text.replaceAll(", ?\\. ", ". ");
		text = text.replaceAll(" \\.", ".");
		text = text.replaceAll("\\.\\.\\.", " ...");
		text = text.replaceAll(" \\,", ",");
		text = text.replaceAll(" Weitere Beispiel dafür, .+", "");
		text = text.replaceAll(" 13", " "); // ^13
		text = text.replaceAll("wa r", "war"); // Nr. 29
		text = text.replaceAll("Anwei sung", "Anweisung");
		text = text.replaceAll("abzufassen7", "abzufassen");
		text = text.replaceAll("Bloch 8 ", "Bloch");
		text = text.replaceAll("VGCC 2 ", "VGCC");
		text = text.replaceAll("Geburtskanal 13 nochmals", "Geburtskanal nochmals");
		
		if (text.startsWith("a ")) // Nr. 7
		{
			text = text.replaceFirst("a ", "");
			text = text.replaceAll("b und c.*", "");
		}

		text = text.replaceAll("Mo nate", "Monate");
		text = text.replaceAll("Krankheit 1 gelang", "Krankheit gelang");

		if (index == 43)
		{
			text = text.replaceAll("(\\p{Z}\\p{Alpha}(\\p{Z})?\\u0029\\p{Z})", "\n$1");
			text = text.replaceAll("f \\u0029", "f)");
			text = text.replaceAll("\\n73763", "");
			text = text.replaceAll("Einige Wochen", "\nEinige Wochen");
			text = text.replaceAll("\\n ", "\n");
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
			list[0] = "doch immerhin schon mehrere Zentimeter";
			list[1] = "kleine Rente. ";
			break;
		case 1:
			list[0] = "Witwe ein Jahr nach Grablegung des Verstor-";
			list[1] = "7 Abschn. 79.4.4. ";
			break;
		case 7:
			list[0] = "Rektumexstirpa tion notwendig. In den darauf ";
			list[1] = "les Organversagen nicht verhindern. ";
			break;
		case 12:
			list[0] = "Schwung bringen, wozu auch die Einwande-";
			list[1] = "Fall – davor liegen. ";
			break;
		case 15:
			list[0] = "Der Grund dafür ist das 1990 zum ersten Mal ";
			list[1] = "Schweigen6? ";
			break;
		case 21:
			list[0] = "abgebrochen werden. Sein Allgemeinzustand ";
			list[1] = "kulose. ";
			break;
		case 22:
			list[0] = "der Zustand des Patienten nicht. Im  Gegenteil, ";
			list[1] = "karzinom« auf. ";
			break;
		case 27:
			list[0] = "Schädel. So weit so gut! Erschwerend war ledig-";
			list[1] = "immer sie auch trafen (. Abb. 14.5a,b). ";
			break;
		case 28:
			list[0] = "Ich konnte aber sein Gekritzel kaum lesen. Im ";
			list[1] = "Er wa r seinem Coma hepaticum  erlegen. ";
			break;
		case 35:
			list[0] = "graphisch als Placenta praevia herausstellte. ";
			list[1] = "portsystems beraubt. ";
			break;
		case 39:
			list[0] = "Interesse an seiner Frau war gleich Null. Dies ";
			list[1] = "tinom «.";
			break;
		case 40:
			list[0] = "rungsvermögen und Schlafstörungen hinzu. ";
			list[1] = "nach. ";
			break;
		case 41:
			list[0] = "sung erteilt, ihn fortan nicht mehr mit solchen ";
			list[1] = "schwanger. ";
			break;
		case 42:
			list[0] = "myome und ein Makroadenom der Hypophyse. ";
			list[1] = "73763";
			break;
		case 44:
			list[0] = "der eigentliche Brutzler – ein Mathematik- ";
			list[1] = "Gesellschaft« des Philosophen Ernst Bloch 8 ? ";
			break;
		case 46:
			list[0] = "Nervengewebe infiltriert. Sie drückten auf ";
			list[1] = "multilocularis«. ";
			break;
		case 50:
			list[0] = "führenden Bildgebung ein entsprechender ";
			list[1] = "alle in den Fall involvierten Ärzte erkannt hatte. ";
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
		return (plainText.contains("978-3-662-48725-9"));
	}
}
