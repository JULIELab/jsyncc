package de.julielab.jsyncc.readbooks.casedescriptions;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.ExtractionUtils;
import de.julielab.jsyncc.tools.LanguageTools;

import org.apache.commons.lang3.exception.ContextedException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CasesSleepMedicine {

	public String parseBook(Path pdfPath) throws ContextedException
	{
		String plainText = ExtractionUtils.getContentByPdftotext(pdfPath);
		return plainText;
	}

	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException
	{
		ArrayList<TextDocument> texttDocuments = new ArrayList<>();

		String plainText = ExtractionUtils.getContentByPdftotext(Paths.get(bookProperties.bookPath));

		if (validateText(plainText))
		{
			ArrayList<String> caseExamples = getCaseExampleTexts(plainText);

			// first part - SHORT examples

			for (int i = 0; i < caseExamples.size(); i++)
			{
				String text = cleanCaseText(caseExamples.get(i));
				String heading = getCaseHeading(caseExamples.get(i));

				TextDocument document = createTextDocument(i, heading, text, "", bookProperties);
				texttDocuments.add(document);
			}

			// second part - LONG examples

			ArrayList<String> patientReports = getReportTexts(plainText);
			ArrayList<String> headings = getPatientreportHeadings(plainText);

			for (int i = 0; i < patientReports.size(); i++)
			{
				String sectionText = cleanReportSectionTexts(patientReports.get(i));
				String text = sectionText.replaceAll("</?(Sub)?Title>","");
				text = text.replaceAll("</?.*>","");
				text = text.replaceAll("\\n+", "\n");

				if (text.startsWith("\n"))
				{
					text = text.replaceFirst("\\n", "");
				}
				if (text.endsWith("\n"))
				{
					text = text.substring(0, text.length() - 1);
				}

				TextDocument document = createTextDocument(
						texttDocuments.size(),
						headings.get(i),
						text,
						sectionText,
						bookProperties
						);
				texttDocuments.add(document);
			}
		}
		return texttDocuments;
	}

	private static TextDocument createTextDocument(int index, String title, String text, String textSection, BookProperties bookProperties)
	{
		TextDocument textDocument = new TextDocument();

		textDocument.setText(checkTextForWindowsArtefactsPart2(text));
		textDocument.setSource(
				bookProperties.getTitle() + " " +
				bookProperties.getEditorAuthor() + " " +
				bookProperties.getYear() + " " +
				bookProperties.getPublisher() + " " +
				bookProperties.getDoi()
		);
		textDocument.setDocumentType(bookProperties.documentType.get(0));
		textDocument.setHeading(title);
		textDocument.setIdLong(bookProperties.sourceShort + "-" + (index + 1));
		textDocument.setTopic(bookProperties.topics);

		textDocument.setSourcShort(bookProperties.sourceShort);
		textDocument.setBookId(bookProperties.bookId);

		return textDocument;
	}

	public static boolean validateText(String plainText)
	{
		return (plainText.contains("978-3-662-50271-6")); // ISBN
	}

	private static ArrayList<String> getCaseExampleTexts(String plainText)
	{
		ArrayList<String> textList = new ArrayList<>();
		Pattern casePattern = Pattern.compile("\\nFallbeispiel(.+\\n)*\\n(\\s(.+\\n){1,2})+(.+\\n)+?\\n");
		Matcher caseMatcher = casePattern.matcher(plainText);

		while (caseMatcher.find())
		{
			String text = caseMatcher.group();
			textList.add(text);
		}

		return textList;
	}

	private static String cleanCaseText(String text)
	{
		text = cleanSpaces(text);

		text = text.replaceAll("\\n55 ", "\n- ");
		text = text.replaceAll("\\n–– ", "\n- ");

		String[] lines = text.split("\n");
		String finalText = "";

		for (int i = 0; i < lines.length; i++)
		{
			if (
					( !(lines[i].isEmpty()) )
				&&
					( !(lines[i].matches("\\d+\\s*")) )
				&&
					( !(lines[i].startsWith("\\d+\\d · ")) )
				)
			{
				if (
						(1 < i)
					&&
						( !(lines[i].startsWith("-")) )
					&&
						( lines[i-1].startsWith("-") )
					&&
						( Character.isUpperCase(lines[i].charAt(0)) )
					)
				{
					finalText = finalText + " " + lines[i];
				}
				else if (lines[i].startsWith("-"))
				{
					finalText = finalText + "\n" + lines[i];
				}
				else
				{
					finalText = finalText + " " + lines[i];
				}
			}

			if (lines[i].endsWith("neben der Rhythmisierung"))
			{
				i = i + 32;
			}
		}

		if (finalText.startsWith(" "))
		{
			finalText = finalText.replaceFirst(" ", "");
		}

		finalText = finalText.replaceAll(" Neben der", "\nNeben der");
		finalText = finalText.replaceAll(" Die Pflegedienstleitung", "\nDie Pflegedienstleitung");
		finalText = finalText.replaceAll(" Dies ergab", "\nDies ergab");
		finalText = finalText.replaceAll(" Die Schlafstörungen des ", "\nDie Schlafstörung des ");

		finalText = LanguageTools.removeHyphenNew(finalText);

		return checkTextForWindowsArtefacts(finalText);
	}

	private static String checkTextForWindowsArtefacts(String text)
	{
		text = text.replaceAll(" Einschlafstörungen\\.\\nÜbergeordnete Bedingungsanalyse", " Einschlafstörungen. Übergeordnete Bedingungsanalyse");
		text = text.replaceAll("\nFolgende Therapieziele werden", "\nTherapieziele Folgende Therapieziele werden");
		text = text.replaceAll("\nFolgende Therapieziele wurden", "\nTherapieziele Folgende Therapieziele wurden");
		text = text.replaceAll("teilzuhaben\\. Therapieziele", "teilzuhaben.");
		text = text.replaceAll("angespannt Akut berichtete Symptomatik und schnell", "angespannt und schnell");
		text = text.replaceAll(" Herr K\\.\\, 34 Jahre alt\\,", "\nBiographische Anamnese\nHerr K., 34 Jahre alt,");
		text = text.replaceAll("seine Mutter verstorben\\. Biographische Anamnese", "seine Mutter verstorben.");

		text = text.replaceAll("zu können\\. Biographische Anamnese", "zu können.\\nBiographische Anamnese");
		text = text.replaceAll(" seien Biographische Anamnese unzufrieden", " seien unzufrieden");
		
		text = text.replaceAll("Beziehung öffnen\\. Therapieziele", "Beziehung öffnen.");
		
		text = text.replaceAll("scheiterten.\\nSomatische Anamnese\\nAppetitlosigkeit", "scheiterten. Somatische Anamnese Appetitlosigkeit");
		text = text.replaceAll("einfordern zu können\\, Therapieziele", "einfordern zu können,");

		text = text.replaceAll("der Schlafstörung identifiziert werden\\.", "der Schlafstörung identifiziert werden. Somatische Anamnese");
		
		return text;
	}
	
	private static String checkTextForWindowsArtefactsPart2(String text)
	{
		text = text.replaceAll("\nSomatische Anamnese\\nKeine körperlichen Beschwerden bekannt\\.", " Somatische Anamnese Keine körperlichen Beschwerden bekannt.");
		text = text.replaceAll("medikamentös behandelt worden\\. Akut berichtete Symptomatik Die Patientin sei in der Nachkriegszeit ", "medikamentös behandelt worden.\nBiographische Anamnese\nDie Patientin sei in der Nachkriegszeit ");
		text = text.replaceAll("beheben, scheiterten\\.\nSomatische Anamnese\nAppetitlosigkeit, Magenschmerzen,", "beheben, scheiterten. Somatische Anamnese Appetitlosigkeit, Magenschmerzen,");
		
		text = text.replaceAll("Therapieziele Therapieziele", "Therapieziele");
		text = text.replaceAll("Somatische Anamnese Somatische Anamnese", "Somatische Anamnese");
		
		
		return text;
	}
	
	private static String getCaseHeading(String text)
	{
		text = text.trim().replaceAll("(\\n.*)+", "");
		text = text.replaceAll(".*?S", "S");	//beide Headings beginnen mit "S"
		return text;
	}
	
	private static String cleanSpaces(String plainText)
	{
		plainText = plainText.replaceAll("\u000C", ""); // page break sympbol
		plainText = plainText.replaceAll("\u2003", " "); // Em-Space
		plainText = plainText.replaceAll("\u00a0", " "); // No-Break Space
		plainText = plainText.replaceAll(" +", " ");

		return plainText;
	}

	private static ArrayList<String> getReportTexts(String plainText)
	{
		plainText = cleanSpaces(plainText);
		
		ArrayList<String> textList = new ArrayList<>();
		ArrayList<Integer> index = new ArrayList<>();

		Pattern reportPattern = Pattern.compile("Patientenbericht.*?\\n.*?\\nz\\sz\\s+Anamnese");
		Matcher reportMatcher = reportPattern.matcher(plainText);

		while (reportMatcher.find())
		{
			int reportStartIndex = reportMatcher.start();
			index.add(reportStartIndex);
		}

		String text = "";

		for (int i = 0; i < index.size() - 1; i++)
		{
			text = plainText.substring(index.get(i), index.get(i+1));
			textList.add(text);
		}

		text = plainText.substring( index.get( index.size()-1 ) );
		text = text.split("\\n\\n125")[0];
		textList.add(text);
		return textList;
	}

	private static String cleanReportSectionTexts(String text)
	{
		text = text.replaceAll("Patientenbericht.*?\\n.*?\\nz\\sz\\s+", ""); // replace Headings
		text = text.replaceAll("\\n44", "\n- ");

		//text = text.replaceAll("Akut berichtete Symptomatik ",	"\n<SubTitle>Akut berichtete Symptomatik</SubTitle>\n\n");
		text = text.replaceAll("Akut berichtete Symptomatik ",	""); // TODO 3 Stellen

		//text = text.replaceAll("Medikamentenanamnese\\n",	"\n<SubTitle>Medikamentenanamnese</SubTitle>\n\n");
		text = text.replaceAll("Medikamentenanamnese\\n",	""); // TODO
		text = text.replaceAll("Medikamentenanamnese ",		"\n<SubTitle>Medikamentenanamnese</SubTitle>\n");

		text = text.replaceAll("Biographische Anamnese ",		"\n<SubTitle>Biographische Anamnese</SubTitle>\n\n");
		text = text.replaceAll(" Biographische Anamnese",	""); // Error with wrong places

		text = text.replaceAll("Somatische Anamnese\\n",	"\n<SubTitle>Somatische Anamnese</SubTitle>\n\n");
		text = text.replaceAll("\\nPsychische Anamnese ",	"\n<SubTitle>Psychische Anamnese</SubTitle>\n\n");
		text = text.replaceAll("Verhaltensanalyse\\n",		"\n<SubTitle>Verhaltensanalyse</SubTitle>\n\n");
		text = text.replaceAll("Soziale Anamnese ",			"\n<SubTitle>Soziale Anamnese</SubTitle>\n\n");

		text = text.replaceAll("\\nTherapieplan ",		"\n<SubTitle>Therapieplan</SubTitle>\n\n");
		text = text.replaceAll("\\nTherapieplan\\n",	"");

		text = text.replaceAll("\\nTherapieverlauf ",	"\n<SubTitle>Therapieverlauf</SubTitle>\n\n");
		text = text.replaceAll("Prognose",				"\n<SubTitle>Prognose</SubTitle>\n\n");
		text = text.replaceAll("Psychopathologischer Befund",	"");

		text = text.replaceAll("\\nz\\sz\\s+", "\n\n");
		text = text.replaceAll("\\nz \\t", "\n\n");

		text = text.replaceFirst("Anamnese\n",		"\n<Title>Anamnese</Title>\n\n<SubTitle>Akut berichtete Symptomatik</SubTitle>\n\n");
		text = text.replaceAll("\\nDiagnostik\\n",	"\n<Title>Diagnostik</Title>\n\n<SubTitle>Psychopathologischer Befund</SubTitle>\n\n");
		text = text.replaceAll("\\nDiagnosen\\n",		"\n<Title>Diagnosen</Title>\n\n");
		text = text.replaceAll("\\nTherapie\\n",		"\n<Title>Therapie</Title>\n\n<SubTitle>Therapieziele</SubTitle>\n\n");
		
		text = text.replaceAll(" F ", " F");

		String[] lines = text.split("\n");
		String finalText = "";

		for (int i = 0; i < lines.length; i++)
		{
			if (
					( !(lines[i].isEmpty()) )
				&&
					( !(lines[i].matches("\\d+\\s*")) )
				&&
					( !(lines[i].startsWith("Patientenbericht")) )
				)
			{
				if (
						( !(lines[i].startsWith("-")) )
					&&
						( !(lines[i].startsWith("<")) )
					)
				{
					finalText = finalText + " " + lines[i];
				}
				else
				{
					finalText = finalText + "\n" + lines[i];
				}
			}
		}

		finalText = finalText.replaceAll("> ", ">\n");

		lines = finalText.split("\n");
		finalText = "";

		String section = "";
		String presection = "";
		
		for (int i = 0; i < lines.length; i++)
		{
			if (
					(1 < i)
				&&
					( !(lines[i].startsWith("<")))
				&&
					( !(lines[i].startsWith("-")))
				&&
					( lines[i-1].startsWith("-") )
				)
			{
				finalText = finalText + " " + lines[i];
			}
			else if (lines[i].startsWith("<Title>"))
			{
				finalText = finalText + "\n";

				presection = section;
				section = lines[i].replaceAll("</?Title>", "");

				if ( !(presection.isEmpty()) )
				{
					finalText = finalText + "</" + presection + ">" + "\n";
				}

				finalText = finalText + "<" + section + ">";
				finalText = finalText + "\n" + lines[i];
			}
			else
			{
				finalText = finalText + "\n" + lines[i];
			}
		}
		finalText = finalText + "\n</" + section + ">";

		finalText = finalText.replaceAll("Kollegeneingesetzt", "Kollegen eingesetzt"); // error in book
		finalText = finalText.replaceAll("Beschwerden bekannt. Keine körperlichen", "Keine körperlichen Beschwerden bekannt.\n<SubTitle>Medikamentenanamnese</SubTitle>\n");

		finalText = finalText.replaceAll(" Rücken- und Nacken", "\n<SubTitle>Somatische Anamnese</SubTitle>\nRücken- und Nacken");
		finalText = finalText.replaceAll("körperlichen Befunde.\n<SubTitle>Somatische Anamnese</SubTitle>", "körperlichen Befunde.");
		finalText = finalText.replaceAll("<SubTitle>Somatische Anamnese</SubTitle>\nEbenso", "<SubTitle>Medikamentenanamnese</SubTitle>\nEbenso");

		finalText = finalText.replaceAll("Appetitlosigkeit\\, Magenschmerzen", "\n<SubTitle>Somatische Anamnese</SubTitle>\nAppetitlosigkeit, Magenschmerzen");

		finalText = finalText.replaceAll("Herr A\\. verfügt über eine hohe Veränderungsmotivation, ist pünktlich und arbeitet aktiv in der Therapie mit, daher ist ein guter Therapieerfolg zu erwarten. Herr K.", "Herr K.");

		finalText = finalText.replaceAll(" Der Patient erhält zu Beginn","\n<SubTitle>Therapieplan</SubTitle>\n\n\nDer Patient erhält zu Beginn");
		finalText = finalText.replaceAll("könne alles machen\\. Akut berichtete Symptomatik", "könne alles machen.");

		finalText = finalText.replaceAll(" z ", "\n");
		finalText = finalText.replaceAll(" z\\n", "\n");
		finalText = finalText.replaceAll(" +\n", "\n");
		finalText = finalText.replaceAll("\\nz", "\n");

		if (finalText.startsWith("\n"))
		{
			finalText = finalText.replaceFirst("\n+", "");
		}

		finalText = finalText.replaceAll("\\n ", "\n");
		finalText = LanguageTools.removeHyphenNew(finalText);
		
		return checkTextForWindowsArtefacts(finalText);
	}

	private static ArrayList<String> getPatientreportHeadings(String plainText)
	{
		Pattern headingPattern = Pattern.compile("Patientenbericht.*?\\n.*?\\nz\\sz\\s+Anamnese\\n.*?");
		Matcher headingMatcher = headingPattern.matcher(plainText);

		ArrayList<String> headings = new ArrayList<>();

		while (headingMatcher.find())
		{
			String heading = headingMatcher.group().trim();
			heading = heading.replaceAll(".* – ", "");
			heading = heading.replaceAll("\\n", " ");
			heading = heading.replaceAll(" z z Anamnese", "");

			headings.add(heading);
		}
		return headings;
	}
}
