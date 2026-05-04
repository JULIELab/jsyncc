package de.julielab.jsyncc.readbooks.casereports;

import org.apache.commons.lang3.exception.ContextedException;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CasesIntensiveCare
{

	public String parseBook(Path pdfPath) throws ContextedException
	{
		String plainText = ExtractionUtils.getContentByTika(pdfPath.toString());
		try
		{
			Files.write(Paths.get(pdfPath.toString().replaceAll("pdf", "txt")), plainText.getBytes());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return plainText;
	}

	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{

		String plainText = ExtractionUtils.getContentByTika(bookProperties.bookPath);
		try
		{
			Files.write(Paths.get(bookProperties.bookPath.replaceAll("pdf", "txt")), plainText.getBytes());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		List<TextDocument> textDocuments = new ArrayList<>();

		boolean readText = false;
		boolean readFirstPart = false;

		int localIndex = 1;

		String currentText = "";
		String chapter = "";

		String[] lines = plainText.split("\\n");

		for (int i = 0; i < lines.length; i++)
		{
			if (lines[i].startsWith("Kapitel "))
			{
				chapter = lines[i].replaceAll("Kapitel \\d+ · ", "");
				chapter = chapter.replaceAll("\\d+", "");
				chapter = chapter.replaceAll("\\s+", " ");
			}

			if ( (lines[i].contains("Fallbeispiel")) && (lines[i].contains("1")) && (lines[i].contains("Teil")))
			{
				readFirstPart = true;
			}

			if (lines[i].matches("Fallbeispiel\\s(\\d+|Teil\\s\\d)\\s*"))
			{
				if (!(currentText.equals("")))
				{
					textDocuments = normalizeTextAndCreateDocument(currentText , localIndex, textDocuments, chapter, bookProperties);
					localIndex++;
					currentText = "";
				}

				readText = true;
			}
			else if (
				lines[i].matches("\\d+\\.\\d\\s\\u00b7*\\s*[a-zA-ZöäüßÖÄÜ0-9()»«,:;-]+(\\s[a-zA-ZöäüßÖÄÜ0-9()»«,:;-]+)*\\s*")
			||
				lines[i].matches("Literatur\\s*")
				)
			{
				readText = false;
			}
			else if ( (readFirstPart) && (lines[i].matches("") ) )
			{
				readText = false;
				readFirstPart = false;

				if (textDocuments.size() == 13)
				{
					currentText = currentText + "\n" + lines[i+1];
					currentText = currentText + "\n" + lines[i+3];
					currentText = currentText + "\n" + lines[i+5];
					currentText = currentText + "\n" + lines[i+7];
					currentText = currentText + "\n" + lines[i+9];
					currentText = currentText + "\n" + lines[i+11];
					currentText = currentText + "\n" + lines[i+13];
					currentText = currentText + "\n" + lines[i+15];
					currentText = currentText + "\n" + lines[i+17];
					currentText = currentText + "\n" + lines[i+19];
					i = i + 19;
				}
				if (textDocuments.size() == 34)
				{
					currentText = currentText + "\n" + lines[i+1];
					currentText = currentText + "\n" + lines[i+2];
					currentText = currentText + "\n" + lines[i+3];
					currentText = currentText + "\n" + lines[i+4];
					currentText = currentText + "\n" + lines[i+5];
					currentText = currentText + "\n" + lines[i+6];
					currentText = currentText + "\n" + lines[i+7];
					currentText = currentText + "\n" + lines[i+8];
					currentText = currentText + "\n" + lines[i+9];
					currentText = currentText + "\n" + lines[i+10];
					currentText = currentText + "\n" + lines[i+11];
					currentText = currentText + "\n" + lines[i+12];
					currentText = currentText + "\n" + lines[i+13];
					i = i + 13;
				}
				if (textDocuments.size() == 47)
				{
					currentText = currentText + "\n" + lines[i+1];
					currentText = currentText + "\n" + lines[i+2];
					currentText = currentText + "\n" + lines[i+3];
					currentText = currentText + "\n" + lines[i+4];
					currentText = currentText + "\n" + lines[i+5];
					currentText = currentText + "\n" + lines[i+6];
					currentText = currentText + "\n" + lines[i+7];
					currentText = currentText + "\n" + lines[i+8];
					currentText = currentText + "\n" + lines[i+9];
					currentText = currentText + "\n" + lines[i+10];
					currentText = currentText + "\n" + lines[i+11];
					currentText = currentText + "\n" + lines[i+12];
					currentText = currentText + "\n" + lines[i+13];
					currentText = currentText + "\n" + lines[i+14];
					currentText = currentText + "\n" + lines[i+15];
					currentText = currentText + "\n" + lines[i+16];
					i = i + 16;
				}
			}
			if (readText)
			{
				String content = lines[i];
				if (
							(!content.matches("\\A+(\\d+)\\s*\\d*"))
						&& (!content.matches("Fallbeispiel\\s(\\d+|Teil\\s\\d)\\s*"))
						&& (!content.matches("\\s*Internetlinks"))
						&& (!content.matches("\\s*Kapitel\\s\\d+\\s\\u00B7\\s[a-zA-Zöäüß0-9]+(\\s+\\u2013*[a-zA-Zöäüß0-9]+)*"))
						&& (!content.matches("\\d+\\.\\d\\.\\d\\s\\u00b7\\s[a-zA-Zöäüß]+(\\s[a-zA-Zöäüß]+)*\\s*"))
						&& (!content.matches("\\s\\.\\sAbb\\.\\s\\d+\\.\\d\\s[a-zA-Zöäüß0-9().]+(\\s+[a-zA-Zöäüß0-9().]+)*\\s*"))
						&& (!content.matches(""))
					)
				{
					currentText = currentText + "\n" + content;
				}
			}
		}

		textDocuments = normalizeTextAndCreateDocument(currentText , localIndex, textDocuments, chapter, bookProperties);
		return textDocuments;
	}

	public static List<TextDocument> normalizeTextAndCreateDocument(
			String text, int localIndex, List<TextDocument> textDocuments, String chapter, BookProperties bookProperties)
	{
		text = text.replaceAll("\\A+\\s", "");
		text = text.replaceAll("\\n\\s4", "=PAR= ");
		text = text.replaceAll("Pa tient", "Patient");
		text = text.replaceAll("prob lemlos", "problemlos");
		text = text.replaceAll("Nor adrenalin", "Noradrenalin");
		text = text.replaceAll("aus geprägte", "ausgeprägte");
		text = text.replaceAll("Intensivsta tion", "Intensivsta tion");
		text = text.replaceAll("dilata tionstracheotomiert", "dilatationstracheotomiert");
		text = text.replaceAll("Lun gen embolie", "Lungenembolie");
		text = text.replaceAll("Situa tion", "Situation");
		text = text.replaceAll("Intensivsta tion", "Intensivstation");
		text = text.replaceAll("Reduk tion", "Reduktion");
		text = text.replaceAll("dilata tion", "dilatation");
		text = text.replaceAll("Ka lium", "Kalium");
		text = text.replaceAll("Cisa tra curium", "Cisatracurium");

		text = NormalizationUtils.correctBullets(text);
		text = NormalizationUtils.normalizeNewlines(text);

		text = text.replaceAll(" Was muss der Intensivarzt nun tun\\?", "");
		text = text.replaceAll(" \\. Tab. 10\\.4 .*", "");
		text = text.replaceAll(" \\. Tab\\. 16\\.2..*Körpergewicht", ""); //\. Tab\. 16\.2..*Körpergewicht 
		text = text.replaceAll("geblähtes Kolon\\. ", "geblähtes Kolon.\n");
		text = text.replaceAll(" anhand von \\. Tab\\. 23\\.2", "");
		text = text.replaceAll(" \\. Tab\\. 23\\.5 .*", "");
		text = text.replaceAll(". Abb. 31.2 .* eliminiert ", "");
		text = text.replaceAll(" \\(\\. Abb\\. 35\\.4\\)", "");
		text = text.replaceAll(" \\(\\. Abb\\. 37\\.1\\)", "");
		text = text.replaceAll(" \\(\\. Abb\\. 37\\.5\\)", "");
		text = text.replaceAll(" \\. Abb\\. 40\\.6 .*", "");
		text = text.replaceAll(" \\(\\. Abb\\. 43\\.2\\)", "");
		text = text.replaceAll(" \\(\\. Abb\\. 50\\.1\\)", "");
		text = text.replaceAll("\\. Tab\\. 56\\.3 .* 14–21 Tage ", "");
		text = text.replaceAll(" \\(\\. Abb\\. 57\\.3\\)", "");
		text = text.replaceAll("A.-cerebri- media-", "A.-cerebri-media-");
		text = text.replaceAll(" \\(\\. Abb\\. 37\\.2\\)", "");
		text = text.replaceAll("Kapitel .*356 ", "");
		text = text.replaceAll("som nolent", "somnolent");
		text = text.replaceAll("Leberzir rhose", "Leberzirrhose");
		text = text.replaceAll("Darm ischämie", "Darmischämie");

		text = LanguageTools.removeHyphenNew(text);
		text = text.replaceAll("=PAR= ", "\n\u002D ");

		TextDocument textDocument = new TextDocument();

		if ( (localIndex & 1) != 0)
		{
			textDocument.setIdLong(bookProperties.getSourceShort() + "-" + (textDocuments.size() + 1));
			textDocument.setText(text);
		}
		else if (1 < localIndex)
		{
			textDocument = textDocuments.get(textDocuments.size() - 1);
			textDocuments.remove(textDocuments.size() - 1);

			String newText = textDocument.getText() + "\n" + text;
			textDocument.setText(newText);
		}

		ArrayList<String> topics = new ArrayList<String>();
		topics.add(bookProperties.topics.get(0));
		textDocument.setTopic(topics);
		textDocument.setDocumentType(bookProperties.documentType.get(0));
		textDocument.setSource(
			bookProperties.getTitle() + " " +
			bookProperties.getEditorAuthor() + " " +
			bookProperties.getYear() + " " +
			bookProperties.getPublisher() + " " +
			bookProperties.getDoi()
		);

		textDocument.setSourcShort(bookProperties.sourceShort);
		textDocument.setBookId(bookProperties.bookId);

		textDocuments.add(textDocument);
		return textDocuments;
	}

	public boolean validateText(String plainText)
	{
		return  (plainText.contains("978-3-642-34433-6"));
	}
}
