package de.julielab.jsyncc.readbooks.operativereports;

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

public class ReportsGeneralVisceralVascularThoracicSurgery
{
	public static List<TextDocument> extractContent(BookProperties bookProperties) throws ContextedException {

		String plainText = ExtractionUtils.getContentByPdftotext(Paths.get(bookProperties.bookPath));
		plainText = plainText.replaceAll("j\\nj", " j");
		plainText = plainText.replaceAll("\\u000C", "");

		try {
			Files.write(Paths.get(bookProperties.bookPath.toString().replaceAll("pdf", "txt")), plainText.getBytes());
		} catch (IOException e) {
			throw new ContextedException(e);
		}

		List<TextDocument> textDocuments = new ArrayList<>();

		if (validateText(plainText))
		{
			List<String> rawDocuments = getTextDocuments(plainText);

			for (int i = 0; i < rawDocuments.size(); i++)
			{
				String text = cleanText(rawDocuments.get(i), i);

				TextDocument textDocument = new TextDocument();
				textDocument.setIdLong(bookProperties.sourceShort + "-" + (i+1));

				textDocument.setSource(
						bookProperties.getTitle() + " " +
						bookProperties.getEditorAuthor() + " " +
						bookProperties.getYear() + " " +
						bookProperties.getPublisher() + " " +
						bookProperties.getDoi()
				);
				textDocument.setText(text);
				textDocument.setDocumentType(bookProperties.documentType.get(0));

				textDocument.setTopic(bookProperties.topics);
				textDocuments.add(textDocument);

				textDocument.setSourcShort(bookProperties.sourceShort);
				textDocument.setBookId(bookProperties.bookId);
			}
		}

		return textDocuments;
	}

	public static boolean validateText(String plainText) {
		return ((plainText.contains("978-3-662-57282-5")) || (plainText.contains("978-3-662-57283-2")));
	}

	public static ArrayList<String> getTextDocuments(String plainText)
	{
		ArrayList<String> textDouments = new ArrayList<>();
		Pattern textPattern = Pattern.compile(" j.*?Indikation.*\\n(.*\\n)*?N\\.N\\.?\\,[ A-Za-zöäüßÖÄÜ,\\/]+");
		Matcher textMatcher = textPattern.matcher(plainText);

		while (textMatcher.find())
		{
			String text = textMatcher.group();
			textDouments.add(text);
		}
		return textDouments;
	}

	private static String cleanText(String text, int i)
	{
		text = text.replaceAll("\\n{4,}\\d+\\n*.*\\n*\\d+\\n*","\n");
		text = text.replaceAll(" \u00A9 Springer-Verlag.+Op-Springer:", "");
		text = text.replaceAll("\\n\\d+\\n", "BREAKPARAGRAPH");

		text = text.replaceAll("BREAKPARAGRAPH.+", ""); // Authors and Clinics
		text = text.replaceAll("BREAKPARAGRAPH", ""); // Authors and Clinics

		text = text.replaceAll(" w\n", " w");
		text = text.replaceAll(" urde", "urde");
		text = text.replaceAll("\\n\\D\\. [A-Zöäü]+.*", "");

		String[] lines = text.split("\n");

		int end = text.indexOf(lines[lines.length - 1]);
		text = text.substring(0, end);

		text = text.replaceAll("\n", " ");
		text = text.replaceAll("\u00A0", " ");
		text = text.replaceAll("\u00AD", ""); //soft hyphen
		text = text.replaceAll("\u202F", " "); // narrow space
		text = text.replaceAll(" + ", " ");
		text = text.replaceAll(" \n", "\n");
		text = text.replaceAll(" jVorgeschichte\\/Indikation ?",	"\nVorgeschichte/Indikation\n");
		text = text.replaceAll(" jVorgeschiche\\/Indikation ?",		"\nVorgeschichte/Indikation\n");
		text = text.replaceAll(" jVorgeschichet\\/Indikation ?",	"\nVorgeschichte/Indikation\n");
		text = text.replaceAll(" jIndikation\\/Vorgeschichte ?",	"\nIndikation/Vorgeschichte\n");
		text = text.replaceAll(" jIndikation ?", "\nIndikation\n");

		text = text.replaceAll(" jDiagnose ?", "\nDiagnose\n");
		text = text.replaceAll(" jVorgehen ?", "\nVorgehen\n");
		text = text.replaceAll(" jVorgehen: ?", "\nVorgehen:\n");

		text = text.replaceAll(" jOperation ?", "\nOperation\n");
		text = text.replaceAll(" jOperation: ?", "\nOperation:\n");
		text = text.replaceAll(" jTherapie ?", "\nTherapie\n");
		text = text.replaceAll(" jWeiteres Prozedere ?", "\nWeiteres Prozedere\n");

		if (text.startsWith("\n"))
		{
			text = text.replaceFirst("\n", "");
		}

		text = text + "\n" + lines[lines.length - 1];
		text = LanguageTools.removeHyphenNew(text);

		if ( ((i+1) == 9) || ((i+1) == 10) )
		{
			text = text.replaceAll("TX 60\\. ", "TX 60.\n");
		}

		if ( ((i+1) == 101) || ((i+1) == 111) )
		{
			text = text.replaceAll("Z .n. Verkehrsunfall.", "Z .n.\nVerkehrsunfall.");
			text = text.replaceAll("einer L änge von", "einer L  änge von");
		}

		if ((i+1) == 102)
		{
			text = text.replaceAll("Heparin-Plombe mit 5000 I.E.", "Heparin-Plombe mit 5000 I.E.\n");
			text = text.replaceAll(" Hautdesinfektion", "Hautdesinfektion");
			text = text.replaceAll("dieHautdesinfektion", "die Hautdesinfektion");
		}

		if ((i+1) == 103)
		{
			text = text.replaceAll("Schenkel mit 5000 I.E.", "Schenkel mit 5000 I.E.\n");
			text = text.replaceAll(" Heparin in 10 ml Kochsalz.", "Heparin in 10 ml Kochsalz.");
		}
		if ( ((i+1) == 106) || ((i+1) == 107) || ((i+1) == 111) || ((i+1) == 112))
		{
			text = text.replaceAll("Verbandswechsel mit z. B. Alginat,", "Verbandswechsel mit z. B.\nAlginat,");
			text = text.replaceAll("KI/4 h, max. 3 g/Tag,", "KI/4 h, max.\n3 g/Tag,");
		}
		if ( ((i+1) == 106) || ((i+1) == 107) ||((i+1) == 108) || ((i+1) == 109) || ((i+1) == 110) || ((i+1) == 111) || ((i+1) == 112))
		{
			text = text.replaceAll("Piritramid s.c.\\/6 h. Nahrungsaufnahme", "Piritramid s.c./6 h.\nNahrungsaufnahme");
			text = text.replaceAll("Pirtramid s.c.\\/6 h. Nahrungsaufnahme", "Pirtramid s.c./6 h.\nNahrungsaufnahme");
		}
		if ((i+1) == 110)
		{
			text = text.replaceAll("Lymphknotenschwellung mit V. a. ein Lymphom.",  "Lymphknotenschwellung mit V. a.\nein Lymphom.");
			text = text.replaceAll("lateral der V. jugularis ", "lateral der V.  jugularis ");
		}
		if ((i+1) == 114)
		{
			text = text.replaceAll("Z. n. Verkehrsunfall", "Z. n.\nVerkehrsunfall");
		}
		if ( ((i+1) == 117) || ((i+1) == 118) || ((i+1) == 162) )
		{
			text = text.replaceAll("Vicryl der Stärke 1. ", "Vicryl der Stärke 1.\n");
		}
		if ((i+1) == 118)
		{
			text = text.replaceAll("des Segments 2. Rückruf", "des Segments 2.\nRückruf");
		}
		if ((i+1) == 120)
		{
			text = text.replaceAll("Z. n. VATS-Lobektomie", "Z. n.\nVATS-Lobektomie");
		}
		if ((i+1) == 121)
		{
			text = text.replaceAll("Segment 2. Das präoperative", "Segment 2.\nDas präoperative");
		}
		if ((i+1) == 122)
		{
			text = text.replaceAll("Station 11. Es folgt", "Station 11.\nEs folgt");
		}
		if ( ((i+1) == 123) || ((i+1) == 124) || ((i+1) == 125) || ((i+1) == 156) || ((i+1) == 157) || ((i+1) == 158) || ((i+1) == 160) )
		{
			text = text.replaceAll("der Stärke 0. Unter", "der Stärke 0.\nUnter");
		}
		if ((i+1) == 124)
		{
			text = text.replaceAll("anamnestischen Z. n. Sternotomie", "anamnestischen Z. n.\nSternotomie");
		}
		if ( ((i+1) == 128) || ((i+1) == 129) || ((i+1) == 130) || ((i+1) == 131) || ((i+1) == 132) || ((i+1) == 133) || ((i+1) == 134) || ((i+1) == 136) ||
				((i+1) == 137) || ((i+1) == 138) || ((i+1) == 139) || ((i+1) == 147)
			)
		{
			text = text.replaceAll("des M. lat. dorsi", "des M. lat.\ndorsi");
		}
		if ( ((i+1) == 135) || ((i+1) == 139) )
		{
			text = text.replaceAll("serratus anterior, Reluxierung des M.", "serratus anterior,\nReluxierung des M.");
		}
		if ( ((i+1) == 128) || ((i+1) == 130) || ((i+1) == 133) || ((i+1) == 136) || ((i+1) == 137) || ((i+1) == 138) || ((i+1) == 141) || ((i+1) == 147))
		{
			text = text.replaceAll("der P leura und ", "der P  leura und");
			text = text.replaceAll("Desinfektion des Op-Gebietes, intrakutane", "Desinfektion des Op- Gebietes, intrakutane");
		}
		if ((i+1) == 141)
		{
			text = text.replaceAll("unddigitales", "und digitales");
		}
		if ( ((i+1) == 150) || ((i+1) == 15) )
		{
			text = text.replaceAll("TX 45. Übernähung", "TX 45.\nÜbernähung");
		}
		if ((i+1) == 162)
		{
			text = text.replaceAll("Lungenunterlappenbasis, V. a. eine", "Lungenunterlappenbasis, V. a.\neine");
		}
		if ((i+1) == 178)
		{
			text = text.replaceAll("mit 250 mm Hg. Hautschnitt", "mit 250 mm Hg.\nHautschnitt");
		}
		if ((i+1) == 179)
		{
			text = text.replaceAll("nach 48 h. Übliche", "nach 48 h.\nÜbliche");
		}
		if ((i+1) == 18)
		{
			text = text.replaceAll("TA 55. Zum Magen", "TA 55.\nZum Magen");
		}
		if ((i+1) == 180)
		{
			text = text.replaceAll("nach 48 h.Engmaschige", "nach 48 h.\nEngmaschige");
			text = text.replaceAll("48 h. Engmaschige", "48 h.\nEngmaschige");
		}
		if ((i+1) == 183)
		{
			text = text.replaceAll("liegen M. gastrocnemius med. und lat.", "liegen M. gastrocnemius med.\nund lat.");
			text = text.replaceAll("M. gastrocnemius lat. herangezogen", "M. gastrocnemius lat.\nherangezogen");
		}
		if ((i+1) == 184)
		{
			text = text.replaceAll("am 1. p.o. Tag.", "am 1. p.o.\nTag.");
		}
		if ((i+1) == 188)
		{
			text = text.replaceAll("Prothese i. S. der Inlay", "Prothese i. S.\nder Inlay");
		}
		if ((i+1) == 190)
		{
			text = text.replaceAll("Zufallsbefund bei Z. n. rezidivierender", "Zufallsbefund bei Z. n.\nrezidivierender");
		}
		if ((i+1) == 194)
		{
			text = text.replaceAll("Revaskularisation i. S. der Aneurysmaausschaltung", "Revaskularisation i. S.\nder Aneurysmaausschaltung");
		}
		if ((i+1) == 198)
		{
			text = text.replaceAll("links bei Z. n. Stent/PTA", "links bei Z. n.\nStent/PTA");
		}
		if ((i+1) == 2)
		{
			text = text.replaceAll("Schluckbeschwerden bei Z. n. zweimaliger", "Schluckbeschwerden bei Z. n.\nzweimaliger");
		}
		if ((i+1) == 203)
		{
			text = text.replaceAll("wird bei Z. n. Apoplex", "wird bei Z. n.\nApoplex");
		}
		if ((i+1) == 205)
		{
			text = text.replaceAll("Hypertonie, Hyperlipidämie, Z. n. Myokardinfarkt", "Hypertonie, Hyperlipidämie, Z. n.\nMyokardinfarkt");
		}
		if ((i+1) == 208)
		{
			text = text.replaceAll("Stadium IV n. F. mit Nekrosen", "Stadium IV n. F.\nmit Nekrosen");
		}
		if ((i+1) == 27)
		{
			text = text.replaceAll("Z. n. tiefer anteriorer Rektumresektion", "Z. n.\ntiefer anteriorer Rektumresektion");
		}
		if ( ((i+1) == 35) || ((i+1) == 37) || ((i+1) == 41) )
		{
			text = text.replaceAll("mit dem TEA 60. Setzen", "mit dem TEA 60.\nSetzen");
		}
		if ((i+1) == 2)
		{
			text = text.replaceAll("Z. n. auswärtiger SD-OP", "Z. n.\nauswärtiger SD-OP");
		}
		
		if ((i+1) == 4)
		{
			text = text.replaceAll("ebenfalls intaktes Neuromonitoring-Signal und EMG", "ebenfalls intaktes Neuromonitoring- Signal und EMG");
		}

		if ((i+1) == 6)
		{
			text = text.replaceAll("rechten N. recurrens, ebenfalls intaktes Neuromonitoring-Signal und -EMG", "rechten N. recurrens, ebenfalls intaktes Neuromonitoring- Signal und -EMG");
		}

		if ((i+1) == 41)
		{
			text = text.replaceAll("mittels TEA 60. Abgabe", "mittels TEA 60.\nAbgabe");
		}
		if ((i+1) == 42)
		{
			text = text.replaceAll("mit dem TEA 60. Setzen", "mit dem TEA 60.\nSetzen");
		}
		if ( ((i+1) == 31) || ((i+1) == 36) || ((i+1) == 43) || ((i+1) == 44) )
		{
			text = text.replaceAll("Art und Weise. 3 cm", "Art und Weise.\n3 cm");
		}
		if ((i+1) == 45)
		{
			text = text.replaceAll("mit GIA 80. Stichinzision", "mit GIA 80.\nStichinzision");
		}
		if ((i+1) == 5)
		{
			text = text.replaceAll("dringende V. a. medulläres", "dringende V. a.\nmedulläres");
		}
		if ((i+1) == 52)
		{
			text = text.replaceAll("Analgesie, z. B. Ibuprofen", "Analgesie, z. B.\nIbuprofen");
		}
		if ((i+1) == 64)
		{
			text = text.replaceAll("Vicryl der Stärke 0. Desinfektion.", "Vicryl der Stärke 0.\nDesinfektion.");
		}
		if ( ((i+1) == 157) || ((i+1) == 159) || ((i+1) == 160) )
		{
			text = text.replaceAll("Nahtmaterial der Stärke 0. ", "Nahtmaterial der Stärke 0.\n");
		}
		if ((i+1) == 162)
		{
			text = text.replaceAll("feiner P räparierschere lässt", "feiner P  räparierschere lässt");
		}
		
		if ( ((i+1) == 41) || ((i+1) == 66) )
		{
			text = text.replaceAll("umliegenden Segmenten d. h. vor allem", "umliegenden Segmenten d. h.\nvor allem");
		}
		if ((i+1) == 67)
		{
			text = text.replaceAll("V. a. Lebermetastase eines", "V. a.\nLebermetastase eines");
		}
		if ((i+1) == 7)
		{
			text = text.replaceAll("bestand der V. a. auf ein", "bestand der V. a.\nauf ein");
		}
		if ((i+1) == 78)
		{
			text = text.replaceAll("CT-morphologischem V. a. auf eine", "CT-morphologischem V. a.\nauf eine");
		}
		if ((i+1) == 71)
		{
			text = text.replaceAll("die alte N arbe, ein", "die alte N  arbe, ein");
		}
		if ((i+1) == 80)
		{
			text = text.replaceAll("Bei Z. n. Entfernung eines", "Bei Z. n.\nEntfernung eines");
		}
		if ( ((i+1) == 84) || ((i+1) == 97) )
		{
			text = text.replaceAll(" Z. n. offener Appendektomie.", " Z. n.\noffener Appendektomie.");
			text = text.replaceAll("Appendektomie.und Unterbauch", "Appendektomie und Unterbauch");
			text = text.replaceAll("Optiktrokar 10 mm. Gasinsufflation", "Optiktrokar 10 mm.\nGasinsufflation");
		}
		if ((i+1) == 85)
		{
			text = text.replaceAll("Optiktrokars 10 mm. Gasinsufflation und", "Optiktrokars 10 mm.\nGasinsufflation und");
		}
		if ((i+1) == 95)
		{
			text = text.replaceAll("Unterbauch bei Z. n. Sectio", "Unterbauch bei Z. n.\nSectio");
		}
		if ( ((i+1) == 96) || ((i+1) == 98) )
		{
			text = text.replaceAll("bei Z. n. Median", "bei Z. n.\nMedian");
		}
		if ( ((i+1) == 2) || ((i+1) == 207) )
		{
			text = text.replaceAll("Z. n. Endarteriektomie", "Z. n.\nEndarteriektomie");
			text = text.replaceAll("von ca. 20 m\\. ", "von ca. 20 m.\n"); // etl. wieder zurück nehmen, da Satz 
		}
		if ((i+1) == 206)
		{
			text = text.replaceAll("Bein bei Z. n. PTA", "Bein bei Z. n.\nPTA");
		}
		if ((i+1) == 47)
		{
			text = text.replaceAll("mit GIA 80. Stichinzision", "mit GIA 80.\nStichinzision");
		}
		if ((i+1) == 74)
		{
			text = text.replaceAll("A. mesenterica sup. abgehend", "A. mesenterica sup.\nabgehend");
		}
		if ((i+1) == 205)
		{
			text = text.replaceAll("linken A. iliaca externa", "linken A.  iliaca externa");
		}
		if ((i+1) == 206)
		{
			text = text.replaceAll("von Heparin-Kochsalz-Lösung und erneutes", "von Heparin-Kochsalz- Lösung und erneutes");
		}
		if ((i+1) == 113)
		{
			text = text.replaceAll("1\\.–5\\. Rippe als Rezidivprophylaxe\\. ", "1.–5. Rippe als Rezidivprophylaxe.\n");
			text = text.replaceAll("1\\.–5\\. Rippe als Rezidivprophylaxe an\\. ", "1.–5. Rippe als Rezidivprophylaxe an.\n");
			text = text.replaceAll("besteht der Z\\. n\\. zweimaligem", "besteht der Z. n.\nzweimaligem");
			text = text.replaceAll("Z. n. Spontanpneumothorax", "Z. n.\nSpontanpneumothorax");
		}
		if ((i+1) == 96)
		{
			text = text.replaceAll("Omentum-Anteilen", "Omentum- Anteilen");
		}

		return text;
	}
}
