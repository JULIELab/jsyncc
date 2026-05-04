package de.julielab.jsyncc.readbooks.casedescriptions.discussion;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.LanguageTools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CasesEmergency{

	public boolean validateText(String plainText) { return (plainText.contains("978-3-662-47231-6")); }

	public static List<TextDocument> extractContent(BookProperties bookProperties)
	{
		boolean readElements = false;
		boolean readTableOfContents = false;
		int index = 1;
		String text = "";
		ArrayList<TextDocument> textDocuments = new ArrayList<>();
		ArrayList<String> tableOfContents = new ArrayList<>();

		String plainText = "";
		ProcessBuilder pb = new ProcessBuilder("pdftotext", bookProperties.bookPath);

		try {
			Process p;
			p = pb.start();
			p.waitFor();

			List<String> lines = Files.readAllLines(Paths.get(bookProperties.bookPath.toString().replaceAll(".pdf", ".txt")));
			for (int i = 0; i < lines.size(); i++)
			{
				plainText = plainText + "\n" + lines.get(i);
			}

		} catch (IOException | InterruptedException e) { e.printStackTrace(); }

		String[] lines = plainText.split("\n");

		for (int i = 0; i < lines.length; i++)
		{
			if (lines[i].matches("\\u000CInhaltsverzeichnis"))
			{
				readTableOfContents = true;
			}

			if (readTableOfContents)
			{
				if (
						(lines[i].contains("\u0008")) && !(lines[i].contains("Sachverzeichnis"))
					)
				{

					String contentEntry = lines[i];
					contentEntry = contentEntry.replaceAll("\\d+[\\u2002\\u2003]+", "");
					contentEntry = contentEntry.replaceAll("\\uFFFD", "");
					contentEntry = contentEntry.replaceAll("\\u2002", " ");
					contentEntry = contentEntry.replaceAll("\\u2003", " ");
					contentEntry = contentEntry.replaceAll("\\u0008", " ");
					contentEntry = contentEntry.replaceAll("\\d+\\s", "");
					contentEntry = contentEntry.replaceAll("\\s+\\d+", "");
					tableOfContents.add(contentEntry.trim());

					//System.out.println(contentEntry.trim());
					//System.out.println("---------------------");
				}
			}
		}

		for (int i = 0; i < lines.length; i++) {

			// get Content of Single Texts
			if ((lines[i].matches("Literatur")) || (lines[i].matches("Fazit")) || (lines[i].matches("Letzte Worte"))) {
				readElements = false;
			}

			if (readElements) {
				text = text + "\n" + lines[i];
			}

			if (lines[i].startsWith("77 ")) {
				if (!(text.equals(""))) {
					textDocuments.addAll(normalizeElement(text, index, tableOfContents, bookProperties));
					index = index + 1;
					text = "";
				}

				text = text + "\n" + lines[i];

				readElements = true;
			}

			// last element
			if (lines[i].endsWith("Sachverzeichnis")) {
				if (!(text.equals(""))) {
					textDocuments.addAll(normalizeElement(text, index, tableOfContents, bookProperties));
					index = index + 1;
					text = "";
				}

				readElements = false;
			}
		}

		return textDocuments;
	}

	private static ArrayList<TextDocument> normalizeElement(String text, int index, ArrayList<String> tableOfContents, BookProperties bookProperties)
	{
		text = text.replaceFirst("77 ", "");
		text = text.replaceAll("\\u0003", "");
		text = text.replaceAll("\\u00A0", " "); // No-Break Space
		text = text.replaceAll("\\u2013", "-"); // – / En dash

		String[] lines = text.split("\n");
		ArrayList<TextDocument> textDocuments = new ArrayList<TextDocument>();;

		text = "";
		boolean partCopyRight = false;
		String tempAuthor = "";

		for (int i = 0; i < lines.length; i++)
		{
			if ((!(lines[i].matches("\\u000C\\d+\\u2003[\\p{Alnum}öäüÖÄÜß\\s\\p{Punct}]+")))
					&& (!(lines[i].matches("\\u000C\\d+"))) && (!(lines[i].matches("\\d+")))
					&& (!(lines[i].equals("")))) {
				if (lines[i].matches(".*\\uF02A.*")) {
					partCopyRight = true;
					tempAuthor = lines[i].substring(0, lines[i].length() - 4);

					if (index == 9) {
						tempAuthor = tempAuthor + " und " + lines[i + 4];
					}

					if (index == 43) {
						tempAuthor = tempAuthor + " und " + lines[i + 3];
					}
				}
				if ((!partCopyRight) && (!(lines[i].equals(tempAuthor)))) {
					// marking of paragraph of enumerations
					if (lines[i].matches("\\u2002? ?\\d+\\.\\u2002 ?\\u0007?.*")) {
						lines[i] = lines[i].replaceAll("\\u2002", "");
						lines[i] = lines[i].replaceAll("\\u0007", " ");

						if (lines[i].startsWith(" ")) {
							lines[i] = lines[i].replaceFirst(" ", "");
						}

						lines[i] = "___" + lines[i];
					}

					text = text + lines[i] + "\n";
				}

				if (lines[i].equals(tempAuthor)) {
					partCopyRight = false;
				}
			}
		}

		text = text.replaceAll("\\n+", "\n");

		if (text.startsWith("\n")) {
			text = text.replaceFirst("\\n", "");
		}
		if (text.endsWith("\n")) {
			text = text.substring(0, text.length() - 1);
		}

		// normalize enumerations
		text = text.replaceAll("\\u0007", ""); // BEL
		text = text.replaceAll("\\u2002", ""); // En space
		text = text.replaceAll("\\u000C", ""); // Form Feed

		// remove numbers in [] - bibliographical references
		text = text.replaceAll(" ?\\[\\d+[\\, \\d]*\\]", "");
		text = text.replaceAll(" ?\\[\\d+\\-\\d+\\]", "");

		// correct mismatched parsing with bullet points
		text = text.replaceAll("\\u2022\t\n", "\n");
		text = text.replaceAll("\\u2022\t ", "\u2022 ");

		text = text.replaceAll("\\nHypoxie\\?", "\n\u2022 Hypoxie?");
		text = text.replaceAll("\\nHypovolämie\\?", "\n\u2022 Hypovolämie?");
		text = text.replaceAll("\\nHypothermie\\?", "\n\u2022 Hypothermie?");
		text = text.replaceAll("\\nHypo\\-\\, Hyperkaliämie", "\n\u2022 Hypo-, Hyperkaliämie");
		text = text.replaceAll("\\nHerzbeuteltamponade\\?", "\n\u2022 Herzbeuteltamponade?");
		text = text.replaceAll("\\nIntoxikation\\?", "\n\u2022 Intoxikation?");
		text = text.replaceAll("\\nThromboembolie\\?", "\n\u2022 Thromboembolie?");
		text = text.replaceAll("\\nSpannungspneumothorax\\?", "\n\u2022 Spannungspneumothorax?");

		text = text.replaceAll("\\nA \\(Airway\\)", "\n\u2022 A (Airway)");
		text = text.replaceAll("\\nB \\(Breathing\\)", "\n\u2022 B (Breathing)");
		text = text.replaceAll("\\nC \\(Circulation\\)", "\n\u2022 C (Circulation)");
		text = text.replaceAll("\\nD \\(Disability\\)", "\n\u2022 D (Disability)");
		text = text.replaceAll("\\nE \\(Exposure/Environment\\)", "\n\u2022 E (Exposure/Environment)");

		// space error in pdf
		text = text.replaceAll("-Kreislauf stabil", "- Kreislauf stabil");

		// This is an error by authors of the book.
		text = text.replaceAll("eines sog- QuickTrach", "eines sog. QuickTrach");

		// remove all newlines and normalize the newlines
		// but save the part with the discussion "Diskussion"
		text = text.replaceAll("\nDiskussion\n", "\n__Diskussion__");
		text = text.replaceAll("\n", "\u0020");
		text = text.replaceAll(" \\u2022", "\n\u2022");
		text = text.replaceAll(" __Diskussion__", "\nDiskussion\n");

		text = text.replaceAll("\u2022", "-"); // bullet •

		text = text.replaceAll("\u2009", "\u0020"); // "Thin space" -> to "normal spache
		text = text.replaceAll("\u00AD", ""); //soft hyphen

		// remove the internal marking of enumerations
		text = text.replaceAll("___", "");
		text = text.replaceAll("\u2013", "-"); // En dash −
		text = text.replaceAll("\u2212", "-"); // Minus −

		// split the text into the parts of the description and discussion
		// and make the entries
		lines = text.split("\nDiskussion\n");

		String caseText = LanguageTools.removeHyphenNew(lines[0]);
		String discText = LanguageTools.removeHyphenNew(lines[1]);

		caseText = caseText.replaceAll(tableOfContents.get(index-1), "").replaceAll("  ", " "); // new
		discText = discText.replaceAll(tableOfContents.get(index-1), "").replaceAll("  ", " "); // new

		caseText = cleanDetails(caseText); // new
		discText = cleanDetails(discText); // new

		/*
		System.out.println(index);
		System.out.println("-------------------");
		System.out.println(tableOfContents.get(index-1));
		System.out.println("-------------------");
		System.out.println(caseText);
		System.out.println("-------------------");
		System.out.println(discText);
		System.out.println("=================================");
		*/

		TextDocument textDocumentCase = new TextDocument();
		textDocumentCase.setText(caseText);
		textDocumentCase.setDocumentType(bookProperties.documentType.get(0));
		textDocumentCase.topics.add(bookProperties.topics.get(0));
		textDocumentCase.setHeading(tableOfContents.get(index-1));
		textDocumentCase.setSource(
			bookProperties.getTitle() + " " +
			bookProperties.getEditorAuthor() + " " +
			bookProperties.getYear() + " " +
			bookProperties.getPublisher() + " " +
			bookProperties.getDoi()
		);
		textDocumentCase.setIdLong(bookProperties.sourceShort + "-" + ((index*2)-1) );
		textDocumentCase.setSourcShort(bookProperties.sourceShort);
		textDocumentCase.setBookId(bookProperties.bookId);

		// in relation of ...
		ArrayList<String> cRelList = new ArrayList<String>();
		cRelList.add(bookProperties.sourceShort + "-" + (index+1));
		textDocumentCase.inRelationOf = cRelList;

		textDocuments.add(textDocumentCase);

		TextDocument textDocumentDiscuss = new TextDocument();
		textDocumentDiscuss.setText(discText);
		textDocumentDiscuss.setDocumentType(bookProperties.documentType.get(1));
		textDocumentDiscuss.topics.add(bookProperties.topics.get(0));
		textDocumentDiscuss.setHeading(tableOfContents.get(index-1));
		textDocumentDiscuss.setSource(
			bookProperties.getTitle() + " " +
			bookProperties.getEditorAuthor() + " " +
			bookProperties.getYear() + " " +
			bookProperties.getPublisher() + " " +
			bookProperties.getDoi()
		);
		textDocumentDiscuss.setIdLong(bookProperties.sourceShort + "-" + (index*2) );
		textDocumentDiscuss.setSourcShort(bookProperties.sourceShort);
		textDocumentDiscuss.setBookId(bookProperties.bookId);

		// in relation of ...
		ArrayList<String> dRelList = new ArrayList<String>();
		dRelList.add(bookProperties.sourceShort + "-" + (index));
		textDocumentDiscuss.inRelationOf = dRelList;

		textDocuments.add(textDocumentDiscuss);

		return textDocuments;
	}
	
	public static String cleanDetails(String text)
	{
		text = text.replaceAll("Nadel-Ent- lastungspunktion", "Nadel-Entlastungspunktion");
		text = text.replaceAll("7 Das zentrale", "Das zentrale");
		text = text.replaceAll("jedem 7 Notarztstützpunkt", "jedem Notarztstützpunkt");
		text = text.replaceAll("Ein hin- ter den ", "Ein hinter den ");
		text = text.replaceAll("15 Nach initialer ", "Nach initialer ");
		text = text.replaceAll("findet sich 24-jähriger treibt im Fluss dahingegen", "findet sich dahingegen");
		text = text.replaceAll("24 Vollbrand in Hochhaus", "");

		text = text.replaceAll("Mythos der mit", "Mythos der Notfallkoniotomie mit"); // ?
		text = text.replaceAll("offen-chirurgischen ", "offen-chirurgischen Notfallkoniotomie"); //?

		text = text.replaceAll("durchgeführten n, ist", "durchgeführten Notfallkoniotomien, ist");
		text = text.replaceAll("einer sind dem", "einer Notfallkoniotomie sind dem");
		text = text.replaceAll("präklinische n", "präklinische Notfallkoniotomien");
		text = text.replaceAll("die , ", "die Notfallkoniotomie, ");

		text = text.replaceAll("konvulsiven Status epilepticus als", "konvulsiven als");
		text = text.replaceAll("Behandlung des vorgesehen", "Behandlung des Status epilepticus vorgesehen");

		text = text.replaceAll("konvulsive ist", "konvulsive Status epilepticus ist");
		text = text.replaceAll("konvulsiven übergehen", "konvulsiven Status epilepticus übergehen");
		text = text.replaceAll("konvulsiven betrachtet ", "konvulsiven Status epilepticus betrachtet ");
		text = text.replaceAll("konvulsiven sind", "konvulsiven Status epilepticus sind");
		text = text.replaceAll("eines konvulsiven und", "eines konvulsiven Status epilepticus und");
		text = text.replaceAll("konvulsiven .", "konvulsiven Status epilepticus.");

		text = text.replaceAll("des ist", "des Status epilepticus ist");
		text = text.replaceAll("des nicht-konvulsiven unterscheidet", "des nicht-konvulsiven Status epilepticus unterscheidet");
		text = text.replaceAll("den nicht-konvulsiven in der", "den nicht-konvulsiven Status epilepticus in der ");
		text = text.replaceAll("eines nicht-konvulsiven als", "eines nicht-konvulsiven Status epilepticus");
		text = text.replaceAll("behutsame Kommu45 nikation in der", "behutsame Kommunikation in der");
		text = text.replaceAll("Re-Evaluierung  der", "Re-Evaluierung der");
		text = text.replaceAll("eines mit deutlicher", "eines Status epilepticus mit deutlicher");

		text = text.replaceAll("chirurgischen NotfallkoniotomieIntervention", "chirurgischen Intervention");
		text = text.replaceAll("in der  mongolischen", "in der mongolischen");
		text = text.replaceAll("einer hängt in erster", "einer Lawinenverschüttung hängt in erster");
		text = text.replaceAll("epilepticus.tatus", "epilepticus");
		text = text.replaceAll("\\( Kurier", "\\( Kurier");
		text = text.replaceAll("\\( Frankfurter", "\\( Frankfurter");
		text = text.replaceAll("\\( Nordlicht", "\\( Nordlicht");
		text = text.replaceAll("\\( Süddeutsche", "\\( Süddeutsche");
		text = text.replaceAll("\\( Tagesspiegel", "\\( Tagesspiegel");
		text = text.replaceAll("epilepticus\\.ls", "epilepticus als");
		text = text.replaceAll("Status epilepticus.nfallsformen", "Anfallsformen wurden erstmalig");
		text = text.replaceAll("konvulsiven Status epilepticus.taten", "konvulsiven Staten");
		text = text.replaceAll("nicht-konvulsiven Status epilepticus.tatus epilepticus", "nicht-konvulsiven Status epilepticus epilepticus");
		text = text.replaceAll("epilepticus\\.nterscheidet", "epilepticus unterscheidet");
		text = text.replaceAll("Status epilepticus\\.nfalles", "Anfalls");
		text = text.replaceAll("konvulsiven Status epilepticus.n der ", "konvulsiven Status epilepticus.n der ");
		text = text.replaceAll("\\( Die Presse", "\\( Die Presse");

		text = text.replaceAll("konvulsiven Status epilepticus epilepticus", "konvulsiven Status epilepticus");
		text = text.replaceAll("Anfallsformen wurden erstmalig wurden erstmalig von ", "Anfallsformen wurden erstmalig von ");
		text = text.replaceAll("konvulsiven Anfalls", "konvulsiven Anfalles");
		text = text.replaceAll("Status epilepticus.n der ", "Status epilepticus in der");
		text = text.replaceAll("nichtkonvulsiven Anfallesformen wurden", "nichtkonvulsiven Anfallsformen wurden");
		text = text.replaceAll("in dermongolischen", "in der mongolischen");

		text = text.replaceAll("-\n", "");
		text = text.replaceAll("ACS bei 75-jähriger Patientin", "");
		return text;
	}
}
