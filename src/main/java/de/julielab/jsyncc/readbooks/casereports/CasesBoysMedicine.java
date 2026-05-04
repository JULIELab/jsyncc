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
import java.util.List;

public class CasesBoysMedicine
{
	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{
		ArrayList<TextDocument> textDocuments = new ArrayList<TextDocument>();

		String plainText = ExtractionUtils.getContentByTika(bookProperties.bookPath);
		try {
			Files.write(Paths.get(bookProperties.bookPath.toString().replaceAll("pdf", "txt")), plainText.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (validateText(plainText))
		{
			boolean readCaseTextFirstPart = false;
			boolean readCaseTextSecondPart = false;
			boolean readCaseTextMiddlePart = false;

			String patientName = "";
			String textCase = "";
			String textMiddlePart = "";
			String heading = "";
			int index = 1;

			String[] lines = plainText.split("\n");

			for (int i = 0; i < lines.length; i++)
			{
				if (lines[i].matches("\\d+\\s\\d+\\s[A-Za-z]+.*"))
				{
					heading = lines[i].replaceAll("\\d+\\s\\d+", "");
				}

				if (	(readCaseTextFirstPart)
						&&
							(
								(lines[i].startsWith("Fragestellung"))
							||
								(lines[i].startsWith("Definition"))
							)
					)
				{
					readCaseTextFirstPart = false;
					readCaseTextMiddlePart = true;
				}

				if ( (readCaseTextSecondPart) && (lines[i].equals("")) )
				{
					readCaseTextSecondPart = false;
					textDocuments.add(createTextDocument(textCase, heading, index, bookProperties));
					index++;
					textCase = "";
				}

				if (readCaseTextFirstPart)
				{
					if (textCase.equals(""))
					{
						String[] line = lines[i].split("(\\s|,)");
						
						if (line[0].equals("Beispiel"))
						{
							patientName = line[1];
						}
						else
						{
							patientName = line[0];
						}
					}
					
					textCase = textCase + "\n" + lines[i];
				}

				if (readCaseTextSecondPart)
				{
					textCase = textCase + "\n" + lines[i];
				}

				if (
						(readCaseTextMiddlePart)
					&&
						( !(lines[i].startsWith("Beispiel " + patientName)) )
					)
				{
					if (lines[i].startsWith("Tab"))
					{
						i = i + 11;
					}
					else if (lines[i].startsWith("6 7 8 9"))
					{
						i = i + 35;
					}
					else if (lines[i].matches("\\AAbb\\.\\s\\d+\\.\\d+\\s.*"))
					{
						while (!(lines[i].equals("")))
						{
							i++;
						}
					}
					else if (lines[i].startsWith("Beispiel einer"))
					{
						i = i + 2;
					}
					else
					{
						textMiddlePart = textMiddlePart + "\n" + lines[i];
					}
				}

				if (lines[i].startsWith("Fallbeispiel"))
				{
					readCaseTextFirstPart = true;
				}

				if (
						(lines[i].startsWith("Beispiel " + patientName))
					&&
						(!readCaseTextFirstPart)
					)
				{
					readCaseTextSecondPart = true;
					readCaseTextMiddlePart = false;
				}
			}
		}
		return textDocuments;
	}

	public static boolean validateText(String plainText) { return(plainText.contains("978-3-658-17323-4")); }


	private static TextDocument createTextDocument(String text, String heading, int index, BookProperties bookProperties)
	{
		text = text.replaceFirst("\n", "");
		text = text.replaceAll("\\s?\\((vgl\\.\\s)?Abb\\.\\s\\d+\\.\\d+\\)", "");

		if (text.startsWith("Beispiel "))
		{
			text = text.replaceFirst("Beispiel ", "");
		}

		text = NormalizationUtils.normalizeNewlines(text);
		text = LanguageTools.removeHyphenNew(text);

		TextDocument textDocument = new TextDocument();
		textDocument.setHeading(heading);
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
}
