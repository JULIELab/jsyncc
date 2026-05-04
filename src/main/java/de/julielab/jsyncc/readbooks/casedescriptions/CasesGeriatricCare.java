package de.julielab.jsyncc.readbooks.casedescriptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;

/**
 * This Book Parsing Class uses the commandline tool 'pdftotext'.
 * This run under Windows and Linus.
 * Be carefully, the output is not the same.
 * This class is a adapted for a usage of Ubuntu LTE 16 and Windows 10.
 * (April / May 2020)
 * 
 * @author Christina Lohr
 *
 */


public class CasesGeriatricCare
{

	public static List<TextDocument> extractContent(BookProperties bookProperties)
	{
		ProcessBuilder pb = new ProcessBuilder("pdftotext", bookProperties.bookPath);
		Process p;

		String plainText = "";
		try {
			p = pb.start();
			p.waitFor();
			List<String> lines = Files.readAllLines(Paths.get(bookProperties.bookPath.replaceAll(".pdf", ".txt")));

			for (int i = 0; i < lines.size(); i++)
			{
				plainText = plainText + "\n" + lines.get(i);
			}
		} catch (IOException | InterruptedException e){
			e.printStackTrace();
		}
		plainText = plainText.replaceFirst("\n", "");

		plainText = plainText.replaceAll("\u2002", " "); // En Space
		plainText = plainText.replaceAll("\u00A0", " "); // No Break Space

		plainText = plainText.replaceAll("\n\u0007 ", "");
		plainText = plainText.replaceAll("\n\u0007", "");
		plainText = plainText.replaceAll("\u0007\n", " ");
		plainText = plainText.replaceAll("\u0007 ", "");
		plainText = plainText.replaceAll("\u0007", ""); // for Linux

		// Linux output contains En Space (\u2020), Windows 10 makes a normal space char

		String[] lines = plainText.split("\n");

		List<TextDocument> textDocuments = new ArrayList<>();
		String text = "";

		boolean readCaseExample = false;
		int index = 1;

		for (int i = 0; i < lines.length; i++)
		{
			if ( ( (
					( lines[i].equals("Literatur") )
				&&
					( !(lines[i+1].isEmpty()) )
				&&
					(readCaseExample)
				)
				||
					(lines[i].startsWith("12.3.2"))
				||
					(lines[i].startsWith("12.4.2"))
				||
					(lines[i].startsWith("ist. ."))
				||
					(lines[i].startsWith("Eine Checkliste"))
				||
					(lines[i].startsWith("Sarah ist sich"))
				)
					&& (text.length() > 0))
			{
				if (lines[i].startsWith("ist. ."))
				{
					text = text + " " + "ist.";
				}

				readCaseExample = false;

				String[] parts = text.split("\\n\\n");

				String heading = parts[0].replaceFirst("\\n", "");
				heading = heading.replaceAll("\\n", " ");
				text = text.replaceFirst(parts[0], "");

				TextDocument textDocument = new TextDocument();

				textDocument.setIdLong(String.format("%s-%s", bookProperties.sourceShort, index++));
				textDocument.setSource(
						bookProperties.getTitle() + " " +
						bookProperties.getEditorAuthor() + " " +
						bookProperties.getYear() + " " +
						bookProperties.getPublisher() + " " +
						bookProperties.getDoi()
				);
				textDocument.setHeading(heading);
				textDocument.setTopic(bookProperties.topics);
				textDocument.setText(cleanText(text));
				textDocument.setDocumentType(bookProperties.documentType.get(0));

				textDocument.setSourcShort(bookProperties.sourceShort);
				textDocument.setBookId(bookProperties.bookId);

				textDocuments.add(textDocument);

				text = "";
			}
			
			if (
					(lines[i].matches("Fallbeispiel Beratung bei"))
				&&
					(lines[i-1].isEmpty())
				||
					((lines[i]).endsWith("Fallbeispiel Beratung bei"))
				||
					((lines[i]).endsWith("Fallbeispiel Beratung bei Ulcus"))
				)
			{
				readCaseExample = true;
			}

			if (readCaseExample)
			{
				if ((lines[i].startsWith(". Tab. 11.2")) && (lines[i].contains("Beratungs")))
				{
					i = i + 25;
				}
				else if (lines[i].startsWith(". Tab. 12.1 "))
				{
					i = i + 22;
				}
				else if (lines[i].startsWith(". Tab. 12.3"))
				{
					i = i + 36;
				}
				else if (lines[i].startsWith(". Tab. 12.4 "))
				{
					i = i + 26;
				}
				else if (lines[i].startsWith(". Tab. 12.5"))
				{
					i = i + 23;
				}
				else if (lines[i].startsWith(". Tab. 12.6"))
				{
					i = i + 48;
				}
				else if (lines[i].startsWith(". Tab. 13.1 "))
				{
					i = i + 58;
				}
				else if (lines[i].matches(". Tab. 12.6 "))
				{
					i = i + 47;
				}
				else if (lines[i].matches("1\\."))
				{
					i = i + 25;
				}
				else if (lines[i].matches("Zielvereinbarung"))
				{
					i = i + 32; //34;
				}
				else if (lines[i].matches("\\(zunehmende\\)"))
				{
					i = i + 19; //20;
				}

				text = text + "\n" + lines[i];
			}
		}

		return textDocuments;
	}

	private static String cleanText(String text)
	{
		text = text.replaceAll("\nTeufelskreislauf Schmerz\n", "");

		text = text.replaceAll("\u000c.*", "");
		text = text.replaceAll("\\n\\d+\\n", "\n");
		text = text.replaceAll("Kapitel \\d+.* \u00B7 .*", ""); // Middle dot · \u00B7
		text = text.replaceAll("\\d+\\.\\d+ \u00B7 .*", "");
		text = text.replaceAll("\u00A0", " "); // No-Break Space

		text = text.replaceAll("\\. Tab\\. 11\\.2", " der Tabelle"); 
		text = text.replaceAll("\\(\\. Tab\\. \\d+\\.\\d+\\)","");
		text = text.replaceAll("\\(\\d Abschn\\. \\d+\\.\\d+\\)", "");
		text = text.replaceAll("\\,?\\s?\\d\\s*Abschn\\. \\d+\\.\\d+", "");

		text = text.replaceAll(". Abb\\. 14\\.2\\.", "die Abbildung.");
		text = text.replaceAll("\\(\\. Abb\\. 13\\.1\\)", "");

		text = text.replaceAll(" \\,", ",");
		text = text.replaceAll(" \\.", ".");
		text = text.replaceAll("\n\\.", ".");

		text = text.replaceAll("\n44", "\n##PARAGRAPH##");
		text = text.replaceAll("\\nLiteratur\\n\\n", "");

		if (text.startsWith("\n"))
		{
			text = text.replaceFirst("\\n+", "");
		}

		text = text.replaceAll("\\n\\n+", "##DUPP##");
		text = text.replaceAll("\\n", " ");
		text = text.replaceAll("##PARAGRAPH##", "\n- ");
		text = text.replaceAll("z##DUPP##", "\nzzzzzzzzzzzz\n"); // Windows
		text = text.replaceAll(" z \\t ", "\nzzzzzzzzzzzz\n"); // Linux -> check!!
		text = text.replaceAll("##DUPP##", " ");

		text = text.replaceAll("Dauerkatheters Der", "Dauerkatheters\nDer");
		text = text.replaceAll("Auskleiden Beim", "Auskleiden\nBeim");
		text = text.replaceAll("vorher. Der Hausarzt", "vorher.\nDer Hausarzt");
		text = text.replaceAll("Anlegen eines Kompressionsverbandes ", "Anlegen eines Kompressionsverbandes\n");
		text = text.replaceAll("ungen aus dem Krankenhaus ", "ungen aus dem Krankenhaus\n");
		text = text.replaceAll("Therapieverlauf von Frau D\\. mit Diabetes Typ 2", "");

		text = text.replaceAll("Inhalte einer Beratung zwischen Pflegexperte und Patient", "");

		if (text.startsWith("Dekubitus "))
		{
			text = text.replaceFirst("Dekubitus ", "");
		}
		if (text.startsWith("cruris venosum durch eine Auszubildende "))
		{
			text = text.replaceFirst("cruris venosum durch eine Auszubildende ", "");
		}
		if (text.startsWith("diabetischem Fußsyndrom "))
		{
			text = text.replaceFirst("diabetischem Fußsyndrom ", "");
		}

		text = text.replaceAll("Erste Beratung der Angehörigen durch den Pflegedienst ", "");
		text = text.replaceAll("Zweite Beratung der Angehörigen durch den Pflegedienst ", "");
		text = text.replaceAll("Dritte Beratung der Angehörigen durch den Pflegedienst ", "");
		text = text.replaceAll("Die schwerste.*Muskelpumpe\\) ", "");

		Pattern p = Pattern.compile("Diese Maßnahmen.*\\n.*\\n.*beim Auskleiden");   // the pattern to search for
		Matcher m = p.matcher(text);
		if (m.find())
		{
			text = text.replaceAll(m.group(0), "");
			text = text.replaceAll("etwas zu trinken\\.", "etwas zu trinken.\n" + m.group(0));
		}

		text = text.replaceAll("\u00AD ?", ""); // ­ / discretionary hyphen
		text = text.replaceAll("zzzzzzzzzzzz", "");
		text = text.replaceAll(" z ", "\n"); // Linux

		text = text.replaceAll("\\t", "");
		text = text.replaceAll(" +", " ");
		text = text.replaceAll(" \\n", "\n");
		text = text.replaceAll("\\n ", "\n");
		text = text.replaceAll("\\n+", "\n");

		return text;
	}

	public boolean validateText(String plainText) { return  (plainText.contains("978-3-662-53028-3")); }
}
