package de.julielab.jsyncc.readbooks.casedescriptions;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.LanguageTools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CasesClinicalInfectiological{

	public static List<TextDocument> extractContent(BookProperties bookProperties)
	{
		String plainText = "";
		ProcessBuilder pb = new ProcessBuilder("pdftotext", bookProperties.bookPath);

		try {
			Process p;
			p = pb.start();
			p.waitFor();

			List<String> lines = Files.readAllLines(Paths.get(bookProperties.bookPath.toString().replaceAll(".pdf", ".txt")));
			for (int i = 0; i < lines.size(); i++) {
				plainText = plainText + "\n" + lines.get(i);
			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		ArrayList<TextDocument> textDocuments = new ArrayList<>();

		if (validateText(plainText))
		{
			List<String> listOfHeadings = new ArrayList<>();

			Pattern start = Pattern.compile("(\\u003E\\u003E\\n+(Haustiere besitzt die Familie nicht; andere Tierkontakte: die Familie wohnt zwar neben dem Zoo,\n+)?((Vorgeschichte und klinische Präsentation)|(Vorgeschichte und klinischer Verlauf)|(Klinische Präsentation)))",Pattern.MULTILINE);
			Pattern end = Pattern.compile("(\\?\\p{Z}Fragen)", Pattern.MULTILINE);

			Matcher startMatcher = start.matcher(plainText);
			Matcher endMatcher = end.matcher(plainText);

			listOfHeadings = extractHeadings(plainText);

			List<String> secondparts = extractSecondParts(plainText);

			int index = 0;
			while (startMatcher.find())
			{
				int startOffset = startMatcher.end();
				endMatcher.find(startOffset);
				int endOffset = endMatcher.start();

				String text = plainText.substring(startOffset, endOffset).trim();
				text = cleanText(index, text, plainText);

				text = text + "\n" + secondparts.get(index);

				TextDocument textDocument = createTextDocument(listOfHeadings.get(index), text, index + 1, bookProperties);
				textDocument.setSourcShort(bookProperties.sourceShort);
				textDocument.setBookId(bookProperties.bookId);
				textDocuments.add(textDocument);

				index++;
			}
		}
		return textDocuments;
	}

	public static List<String> extractHeadings(String plainText)
	{
		List<String> listOfHeadings = new ArrayList<>();

		Pattern headingStart = Pattern.compile("\\u000C\\d+\\n+(.*?)\n", Pattern.MULTILINE);
		Matcher headingMatcher = headingStart.matcher(plainText);

		while (headingMatcher.find())
		{
			String heading = headingMatcher.group(1);

			if ( !(heading.startsWith("Kapitel")
					| heading.contains("Klinische Präsentation")
					| heading.contains("Untersuchungsbefunde")
					| heading.contains("Therapie")
					| heading.contains("Diskussion")
					| heading.contains("Literatur")
					|| heading.contains("Stichwortverzeichnis")
					| heading.matches("\\d+"))
				)
			{
				if (heading.startsWith("Langsames"))
				{
					heading = heading + " vor Schaden nicht";
				}
				if (heading.startsWith("Über die Leber"))
				{
					heading = heading +  " Ein seltsamer Tumor";
				}
				listOfHeadings.add(heading);
			}
		}
		return listOfHeadings;
	}

	private static List<String> extractSecondParts(String plainText)
	{
		ArrayList<String> textList = new ArrayList<>();

		Pattern start = Pattern.compile("(> Weiterer klinischer Verlauf)|(> Therapie und Weiterer klinischer Verlauf)|(\\nWeiterer Verlauf\\n)",Pattern.MULTILINE);
		Pattern end = Pattern.compile("(! Diagnose)|(Staphylococcus aureus wird)|(\nWichtig\n)|(\nDiskussion\n)|(\n\\? Fragen\n)|(\n\\? Weitere Fragen\n)|(\nWeiterführende Literatur\n)|(\nMikrobiologische Untersuchungen\n)", Pattern.MULTILINE);

		Matcher startMatcher = start.matcher(plainText);
		Matcher endMatcher = end.matcher(plainText);

		int index = 0;
		while (startMatcher.find())
		{
			int startOffset = startMatcher.end();
			endMatcher.find(startOffset);
			int endOffset = endMatcher.start();

			String text = plainText.substring(startOffset, endOffset).trim();
			
			if (index == 8)
			{
				int s = plainText.indexOf("Untersuchungsbefunden wurde bei Verdacht");
				int e = plainText.indexOf("Eitrige Meningitis und Sepsis durch");

				String ext = plainText.substring(s, e).replaceAll("! Diagnose", "");
				text = text + "\n" + ext;
			}
			if (index == 26)
			{
				int s = plainText.indexOf("mikrobiologischen Befundes wird die");
				int e = plainText.indexOf("Spontaner Gasbrand durch");

				String ext = plainText.substring(s, e).replaceAll("! Diagnose", "");
				text = text + "\n" + ext;
			}
			if (text.startsWith("Der Patient wird unverzüglich"))
			{
				int s = plainText.indexOf("deutlich kleiner ist.");
				int e = plainText.indexOf("1. MRSA-Sepsis");

				String ext = plainText.substring(s, e).replaceAll("! Diagnosen", "");
				text = text + "\n" + ext;
			}
			if (index == 30)
			{
				int s = plainText.indexOf("Zunächst wird unter dem Verdacht");
				int e = plainText.indexOf("⊡ Abb. 31.1.");
				String ext = plainText.substring(s, e).replaceAll("! Diagnose", "");
				//text = text + "\n" + ext;

				s = plainText.indexOf("Wert\n\nNormbereich\n\nBKS\n\n25 mm/h");
				e = plainText.indexOf("Normalgewichtige, klinisch völlig");
				ext = ext + "\n" + plainText.substring(s, e);
				ext = ext.replaceAll("Differenzialblutbild\n\nKörperliche Untersuchung", "");
				ext = ext.replaceAll("Lymphozyten", "Differenzialblutbild\nLymphozyten\n");
				text = ext + "\n" + text;
			}

			text = cleanSecondText(text, index + 1);

			if (text.startsWith("Am 19.6."))
			{
				String t = textList.get(textList.size()-1);
				textList.remove(textList.size()-1);
				index--;
				text = t + "\n" + text;
			}
			if (text.startsWith("Der Patient wird unverzüglich"))
			{
				String t = textList.get(textList.size()-1);
				textList.remove(textList.size()-1);
				index--;
				text = t + "\n" + text;
			}

			textList.add(text);
			index++;

			// need empty entries
			if ( (index == 17) || (index == 41) )
			{
				index++;
				textList.add("");
			}
		}
		return textList;
	}

	private static String cleanText(int index, String text, String plainText)
	{
		if (index == 6)
		{
			String first = "a\n";
			int endFirst = text.indexOf(first) - 1;
			int startSecond = plainText.indexOf("Die serologischen Untersuchungen auf Herpes-Viren ergaben keinen Hinweis auf eine");

			String second = "Borreliose im Spätstadium.";
			int endSecond = plainText.indexOf(second) + second.length() + 1;
			text = text.substring(0, endFirst) + plainText.substring(startSecond, endSecond);
		}
		else if (index == 8)
		{
			text = text.replaceAll("\nGerinnung\n", "");
			text = text.replaceAll("\nBlutgasanalyse", "");
			text = text.replaceAll("INR", "Gerinnung\nINR");
			text = text.replaceAll("pH", "Blutgasanalyse\npH");
		}
		else if (index == 13)
		{
			text = text.replaceAll("109", "10^9");
			text = text.replaceAll(" \\(⊡ Abb. 14.5a,b,", ".");
			text = text.replaceAll("Sabouraud", "Nach Übernachtkultur wachsen auf Sabouraud");
			text = text.replaceAll("\\(Vergrösserung ×100 und ×400\\)", "");
		}
		else if (index == 16)
		{
			String[] t = text.split("\n17\n");
			String s[] = t[1].split("\n\n");
			text = t[0] + "\n" + s[3] + "\n" + s[0] + "\n" + s[1] + "\n" + s[4];
		}
		else if (index == 17)
		{
			String first = "40\n";
			int endFirst = text.indexOf(first) - 1;
			text = text.substring(0, endFirst);
		}
		else if (index == 19)
		{
			text = text.replaceAll("Aktueller\\nBefund", "Aktueller Befund");
			text = text.replaceAll("\\(b\\) bei Aufnahme", "");
		}
		else if (index == 23)
		{
			String first = "\n1. Welche Erkrankungen kommen differentialdiagnostisch in Frage?";
			int endFirst = text.indexOf(first) - 1;
			text = text.substring(0, endFirst);
		}
		else if (index == 24)
		{
			String first = "\u22A1 Abb. 25.1. Transthorakale Echokardiografie (2D-Mode) bei Aufnahme. Es zeigt sich eine bikuspide";
			int endFirst = text.indexOf(first) - 1;
			int startSecond = plainText.indexOf("Der körperliche Untersuchungsbefund ergibt");

			String second = "Verlauf gestaltet sich komplikationslos.";
			int endSecond = plainText.indexOf(second) + second.length() + 1;
			text = text.substring(0, endFirst) + plainText.substring(startSecond, endSecond);
		}
		else if (index == 29)
		{
			text = text.replaceAll("Rückkehr-", "Rückkehr");
		}
		else if (index == 31)
		{
			text = text.replaceAll("Parameter", "");
			text = text.replaceAll("2\\.06\\.\n", "Parameter\n2.06.\n");
			text = text.replaceAll("und Pleuritis", "");
		}
		else if (index == 33)
		{
			text = text.replaceAll("Tumormarker:", "");
			text = text.replaceAll("– CA 19–9", "Tumormarker:\n\n– CA 19–9");
			text = text.replaceAll("Proteinelektrophorese:", "");
			text = text.replaceAll("– Albumin", "Proteinelektrophorese:\n\n– Albumin");
			text = text.replaceAll("34", "");
		}
		else if (index == 34)
		{
			text = text.replaceAll("der Kontrastmitteldarstellung \\(Pfeil\\)", "");
		}
		else if (index == 35)
		{
			text = text.replaceAll("Abbildung\\)", "");
		}

		text = text.replaceAll("\\u000C\\d+", "");
		text = text.replaceAll("Kapitel\\p{Z}\\d+\\p{Z}\\u00B7\\p{Z}.*", "");
		text = text.replaceAll("^\\d+\n", "");
		text = text.replaceAll("\n(a|b)\n", "");

		text = text.replaceAll("\\p{Z}?\\u0028\\u22A1\\p{Z}Abb\\.\\p{Z}\\d+\\.\\d+(a\\,b)?\\u0029", "");
		text = text.replaceAll("^\\u22A1\\p{Z}Abb\\.\\p{Z}\\d+\\.\\d(\\.)?(a\\,b\\.?)?.*", "");

		text = text.replaceAll("Klinische Präsentation", "");
		text = text.replaceAll("Literatur\n?", "");

		text = text.replaceAll("ascendensProthese", "ascendens-Prothese");
		text = text.replaceAll("\\n°", " °");

		String[] lines = text.split("\\n");
		text = "";
		boolean lab = false;

		for (int i = 0; i < lines.length; i++)
		{
			if (
					(lines[i].equals("Parameter"))
				||
					(lines[i].equals("Aktueller Wert"))
				||
					(lines[i].equals("Aktueller Befund"))
				||
					(lines[i].startsWith("Legionellen-AG-Test"))
				)
			{
				lab = true;
			}

			if ( !(lines[i].startsWith("\u22A1 Abb.")) ) // ⊡ Abb. // Squared Dot Operator
			{
				if (
					(	(text.endsWith(","))
					||
						(!lab)
					||
						(lines[i].startsWith("\u25ac")) )
					&&
						( ! (lines[i].startsWith("Untersuchungsbefunde")) )
					)
				{
					text = text + " " + lines[i];
				}
				else
				{
					text = text + "\n" + lines[i];
				}
			}
			
			if ( lab
				&&
					(
						(lines[i].startsWith("Weitere Beobachtungen"))
					||
						(lines[i].startsWith("Untersuchungsbefunde"))
					||
						(lines[i].startsWith("Im Normbereich"))
					||
						(lines[i].startsWith("Röntgen Thorax am Aufnahmetag"))
					||
						(lines[i].startsWith("Elektrokardiographisch"))
					||
						(lines[i].startsWith("Im Differenzialblutbild"))
					||
						(text.endsWith("Mykoplasmen-IgM EIA\n\nNegativ\n"))
					)
				)
			{
				text = text + " ";
				lab = false;
			}
		}

		text = text.replaceAll("Urinsediment Alle", "Urinsediment\nAlle");
		text = text.replaceAll(" Körperliche Untersuchung ", "\nKörperliche Untersuchung\n");
		text = text.replaceAll("> Weiterer klinischer Verlauf.*", "");
		text = text.replaceAll("> Weiterer Verlauf .*", "");
		text = text.replaceAll("⊡ Abb. 11.1", "Abbildung");
		text = text.replaceAll("⊡ Abb. 25.1", "Abbildung");
		text = text.replaceAll("⊡ Abb. 37.1", "Abbildung");
		text = text.replaceAll("⊡ Abb. 18.1", "Die Abbildung");

		text = text.replaceAll("11 Pleurapunktat", "Pleurapunktat");
		text = text.replaceAll("Resultate\\. ", "Resultate.\n");
		text = text.replaceAll("\nUntersuchungsbefunde ", "\nUntersuchungsbefunde\n");
		text = text.replaceAll(" Röntgen Thorax", "\nRöntgen Thorax");
		text = text.replaceAll("Postoperativer Verlauf ", "Postoperativer Verlauf\n");
		text = text.replaceAll("Lymphknotenpaket ", "");
		text = text.replaceAll( "Mischke et al. 2005\\)", "");
		text = text.replaceAll("Gegenüber .* Verschattung ", "");
		text = text.replaceAll(" Differenzialdiagnostik am Aufnahmetag ", "\nDifferenzialdiagnostik am Aufnahmetag\n");
		text = text.replaceAll(" rechts, Hepatisationsstadium .*", "");
		text = text.replaceAll("Immunsuppressive .* Blutbild", "");
		text = text.replaceAll("aus der Blutkultur \\(×1000\\) ", "");
		text = text.replaceAll("Untersuchungsbefunde\\nbei der Aufnahme ", "Untersuchungsbefunde bei der Aufnahme\n");
		text = text.replaceAll("zahlreichen.* Vergrößerung ", "");

		text = text.replaceAll(" \\.", ".");
		text = text.replaceAll("\u25ac", "\n- "); // ▬ Black Rectangle
		text = text.replaceAll(" +", " ");
		text = text.replaceAll("\\n ", "\n");
		text = text.replaceAll("\\n+", "\n");
		text = text.replaceAll(" \\n", "\n");

		text = text.replaceAll("Wert Normbereich .* Differenzialblutbild", "");

		if (text.startsWith("\n"))
		{
			text = text.replaceFirst("\\n", "");
		}
		if (text.startsWith(" "))
		{
			text = text.replaceFirst(" ", "");
		}
		if (text.endsWith(" "))
		{
			text = text.substring(0, text.length() - 1);
		}

		text = LanguageTools.removeHyphenNew(text);
		text = text.replaceAll("Thromobzyten\\(", "Thromobzyten- (");

		return text;
	}
	
	private static String cleanSecondText(String text, int index)
	{
		if (index == 7)
		{
			text = text.replaceAll("⊡ Tab\\. 7\\.1\\. .*\\n(.*\\n)*1:512", "");
			text = text.replaceAll("⊡ Tab. 7.3. .*\n(.*\n)*\u000c41", "");
			text = text.replaceAll(" \\(⊡ Tab. \\d\\.\\d\\)", "");
			text = text.replaceAll("⊡ Tab. \\d\\.\\d", " der Tabelle");
		}
		if (index == 13)
		{
			text = text.replaceAll("Fortgang der Diagnostik\\n(.*\\n)*.*vorgelegen hat\\.", "");
		}
		if (index == 15)
		{
			text = text.replaceAll("⊡ Abb\\. 15\\.1a.*\n(.*\n)*","");
			text = text.replaceAll("mit einem rosa bis rotem Zentrum \\(sog\\. »Kuhaugen«\\)", "");
		}
		if (index == 19)
		{
			text = text.replaceAll(" \\(⊡ Abb\\. 19\\.1a–d\\)", "");
			text = text.replaceAll("⊡ Abb\\. \\d+\\.\\d.*", "");
		}
		if (index == 24)
		{
			text = text.replaceAll("125", "");
		}

		text = text.replaceAll("\\n⊡ Abb. \\d+.\\d+. (.*\\n)?.*\\n?", " "); // ggf. prüfen!

		text = text.replaceAll("\u000c\\d+", "");
		text = text.replaceAll("Kapitel\\s\\d+\\s\\u00B7\\p{Z}.*", "");
		text = text.replaceAll("\n(a|b)\n", "");

		text = text.replaceAll("\\p{Z}?\\u0028\\u22A1\\p{Z}Abb\\.\\p{Z}\\d+\\.\\d+(a\\,b)?\\u0029", "");
		text = text.replaceAll("^\\u22A1\\p{Z}Abb\\.\\p{Z}\\d+\\.\\d(\\.)?(a\\,b\\.?)?.*", "");
		text = text.replaceAll("(\n| )\\(⊡ Abb\\. \\d+\\.\\d+(a|b|c|d|e)?\\)", "");
		text = text.replaceAll("⊡ Abb. 34.2a,b.*", "");
		text = text.replaceAll(" \\(⊡ Abb\\. 41\\.2, ⊡ Abb\\. 41\\.3\\)", "");
		text = text.replaceAll(" \\(⊡ Abb\\. 28\\.2 und ⊡ Abb\\. 28.3\\)", "");
		
		text = text.replaceAll("\\(⊡ Abb. 43\\.2\\.\\)", "");
		text = text.replaceAll(" \\(⊡Abb\\. 5\\.1\\)", "");
		
		text = text.replaceAll("\\n\\(⊡ Tab\\. 15\\.1\\)", "");
		text = text.replaceAll("siehe ⊡ Tab\\. 37\\.1", "siehe Tabelle");
		
		text = text.replaceAll("\nLiteratur\n", "");

		if (! ( 
				(index == 22)
			||
				(index == 31)
				)
			)
		{
			text = text.replaceAll("\\n", " ");
		}
		else if (index == 22)
		{
			text = text.replaceAll("\\n", " ");
			text = text.replaceAll("folgendem Antibiogramm:", "folgendem Antibiogramm:\n");
			text = text.replaceAll("Ochrobactrum anthropi\\, ", "\nOchrobactrum anthropi, ");
		}
		else if (index == 31)
		{
			String[] lines = text.split("\\n");
			text = "";

			for (int i = 0; i < lines.length; i++)
			{
				if ( (i < 7) && (i > 63) )
				{
					text = text + " " + lines[i];
				}
				else
				{
					text = text + "\n" + lines[i];
				}
			}
		}

		text = text.replaceAll("Parasitologische Untersuchungen", "\nParasitologische Untersuchungen\n");
		text = text.replaceAll(" \\.", ".");
		text = text.replaceAll("\u25ac", "\n- "); // ▬ Black Rectangle
		text = text.replaceAll(" +", " ");
		text = text.replaceAll("\\n ", "\n");
		text = text.replaceAll("\\n+", "\n");
		text = text.replaceAll(" \\n", "\n");

		if (text.startsWith("\n"))
		{
			text = text.replaceFirst("\\n", "");
		}
		if (text.startsWith(" "))
		{
			text = text.replaceFirst(" ", "");
		}
		if (text.endsWith(" "))
		{
			text = text.substring(0, text.length() - 1);
		}

		text = LanguageTools.removeHyphenNew(text);

		return text;
	}

	private static TextDocument createTextDocument(String heading, String textn, int index, BookProperties bookProperties)
	{
		TextDocument textDocument = new TextDocument();
		textDocument.setHeading(heading);
		textDocument.setText(textn);
		textDocument.setDocumentType(bookProperties.documentType.get(0));
		textDocument.setTopic(bookProperties.topics);
		textDocument.setSource(
				bookProperties.getTitle() + " " +
				bookProperties.getEditorAuthor() + " " +
				bookProperties.getYear() + " " +
				bookProperties.getPublisher() + " " +
				bookProperties.getDoi()
		);
		textDocument.setIdLong(String.format("%s-%s", bookProperties.sourceShort, index++));

		return textDocument;
	}

	public static boolean validateText(String plainText) {
		return plainText.contains("978-3-540-69846-3");
	}
}
