package de.julielab.jsyncc.readbooks.emergencyreports;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.ExtractionUtils;
import de.julielab.jsyncc.tools.LanguageTools;

import org.apache.commons.lang3.exception.ContextedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportsEmergency
{
	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{

		String plainText = ExtractionUtils.getContentByTika(bookProperties.bookPath);
		try
		{
			Files.write(Paths.get(bookProperties.bookPath.replaceAll("pdf", "txt")), plainText.getBytes());
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		List<TextDocument> textDocuments = new ArrayList<>();

		if (validateText(plainText))
		{
			ArrayList<String> texts = getDocumentTexts(plainText);
			int index = 0;
			
			for (int i = 0; i < texts.size(); i++)
			{
				String textSection = createSectionText(texts.get(i));
				String text = sectionText2Text(textSection);
				textDocuments.add(createTextDocument(text, index++, textSection, bookProperties));
			}
		}

		return textDocuments;
	}

	public static ArrayList<String> getDocumentTexts(String plainText)
	{
		ArrayList<String> texts = new ArrayList<>();

		Pattern textPattern = Pattern.compile("kNotaufnahmeprotokoll.*\\n(.*\\n)*?N\\.N\\.?");
		Matcher textMatcher = textPattern.matcher(plainText);

		int i = 0;
		while (textMatcher.find())
		{
			String text = textMatcher.group();
			
			if ( (i==63) || (i==148))
			{
				String[] t = text.split("\\(Facharzt\\)");
				texts.add(t[0]);
				String t2[] = t[1].split("kNotaufnahmeprotokoll");
				texts.add(t2[1]);
				i++;
			}
			else
			{
				texts.add(text);
				i++;
			}
		}
		return texts;
	}

	public static boolean validateText(String plainText)
	{
		return plainText.contains("ISSN 2364-2246");
	}

	private static TextDocument createTextDocument(String text, int index, String textSection, BookProperties bookProperties)
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

	private static String createSectionText(String text)
	{
		text = text.replaceAll("kNotaufnahmeprotokoll\n+", "");
		text = text.replaceAll("\\n\\d+(\\.\\d+)?.*\\n", "");
		text = text.replaceAll("©.*\n", "");
		text = text.replaceAll("\n+Polytraumadokumentation in der Notaufnahme\n+", "");
		text = text.replaceAll("\n+A. Krumnow et al.\n+", "");
		text = text.replaceAll("\n+H. Siekmann et al.\n+", "");
		text = text.replaceAll("\n+R. Neef und D. Uhlmann\n+", "");
		text = text.replaceAll("\n+O. Richter\n+", "");
		text = text.replaceAll("N\\. N\\.", "N.N.");
		text = text.replaceAll("\u00A0", " ");
		text = text.replaceAll("\u00AD", ""); //soft hyphen

		String[] lines = text.split("\n\n+");
		text = "";

		for (int i = 0; i < lines.length; i++)
		{
			String[] words = lines[i].split(" ");

			if ( !(words[0].isEmpty()) )
			{
				String line = lines[i].replaceAll("\n", " ");
				line = line.replaceAll(" +", " ");
				line = LanguageTools.removeHyphenNew(line);
				String sectionName = words[0].replaceAll("\n", "");

				if (sectionName.startsWith("Anamnese"))
				{
					text = text + "\n<Anamnese>\n" + line + "\n</Anamnese>";
				}
				else if (
						(sectionName.startsWith("Antibiose"))
					||
						(sectionName.startsWith("Medikamente"))
					||
						(sectionName.startsWith("Medikation"))
				)
				{
					text = text + "\n<Medikation>\n" + line + "\n</Medikation>";
				}
				else if (
						(sectionName.startsWith("Befund"))
					||
						(sectionName.startsWith("Klinischer"))
					||
						(sectionName.equals("Labor"))
					||
						(sectionName.startsWith("Röntgen"))
					||
						(sectionName.endsWith("CT"))
					||
						(sectionName.startsWith("CT-"))
					||
						(sectionName.equals("EKG"))
					||
						(sectionName.endsWith("MRT"))
					||
						(sectionName.startsWith("CT-"))
					||
						(sectionName.startsWith("Dünnschicht"))
					||
						(sectionName.startsWith("Lokalbefund"))
					||
						(sectionName.endsWith("ographie"))
					||
						(sectionName.endsWith("skopie"))
					||
						(sectionName.endsWith("Diagnostik"))
					||
						(sectionName.endsWith("punktat"))
					||
						(sectionName.endsWith("dokumentation"))
					||
						(sectionName.startsWith("Vital"))
					||
						(sectionName.equals("Rechts"))
					||
						(sectionName.equals("Links"))
					||
						(sectionName.equals("A."))
					||
						(sectionName.equals("CBI"))
					||
						(sectionName.equals("Befund"))
					||
						(sectionName.equals("Kontrolle"))
					||
						(sectionName.equals("Pulse"))
					||
						(sectionName.equals("Eingehende"))
					||
						(sectionName.equals("Digital-rektale"))

						)
				{
					text = text + "\n<Befund>\n" + line + "\n</Befund>";
				}
				else if (
						(sectionName.startsWith("Allergien"))
					||
						(sectionName.startsWith("Tetanus"))
					||
						(line.startsWith("Letzte Tetanus"))
						)
				{
					text = text + "\n<CAVE>\n" + line + "\n</CAVE>";
				}
				else if (
						(sectionName.startsWith("Nebenerkrankungen")) // TODO prüfen
					||
						(sectionName.startsWith("Diagnose"))
					||
						(sectionName.startsWith("Verdachtsdiagnose"))
						)
				{
					text = text + "\n<Diagnose>\n" + line + "\n</Diagnose>";
				}
				else if (sectionName.startsWith("Procedere"))
				{
					text = text + "\n<Procedere>\n" + line + "\n</Procedere>";
				}
				else if (
						(sectionName.startsWith("Therapie"))
					||
						(sectionName.startsWith("Maßnahmen"))
						)
				{
					text = text + "\n<TherapieMaßnahmen>\n" + line + "\n</TherapieMaßnahmen>";
				}
				else if (sectionName.startsWith("Gynäkologische")) // Gyn. Vorstellung
				{
					text = text + "\n<Verlauf>\n" + line + "\n</Verlauf>";
				}
				else if (
						(sectionName.startsWith("N.N."))
					||
						(sectionName.equals("N. N."))
					||
						(sectionName.startsWith("Prof."))
						)
				{
					text = text + "\n<FinalRemarks>\n" + line + "\n</FinalRemarks>";
				}
				else
				{
					if (!(line.startsWith("Notaufnahmeberichte ")))
					{
						text = text + "\n" + line;
					}
				}
			}

		}

		text = "Notaufnahmeprotokoll" + text;
		text = cleanDoubleSections(text);

		text = text.replaceAll("\\nBefund ",		"\nBefund\n");
		text = text.replaceAll("\\nAnamnese ",		"\nAnamnese\n");
		text = text.replaceAll("\\nDiagnose ",		"\nDiagnose\n");
		text = text.replaceAll("\\nMedikation ",	"\nMedikation\n");
		text = text.replaceAll("\\nProcedere ",		"\nProcedere\n");
		text = text.replaceAll("\\nTherapie ",		"\nTherapie\n");

		return text;
	}

	private static String cleanDoubleSections(String text)
	{
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

	private static String sectionText2Text(String text)
	{
		text = text.replaceAll("</.*>\n", "");
		text = text.replaceAll("<.*>\n?", "");
		return text;
	}

}
