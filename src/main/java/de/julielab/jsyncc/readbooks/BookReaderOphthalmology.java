package de.julielab.jsyncc.readbooks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;

import de.julielab.jsyncc.tools.FileTools;
import de.julielab.jsyncc.tools.LanguageTools;

public class BookReaderOphthalmology {
	public static String BOOK_09 = FileTools.getSinglePDFFileName("src/main/resources/books/09-Fallbeispiele-Augenheilkunde");
	public static String source = "Michael A. Thiel (2013). Fallbeispiele Augenheilkunde. Springer-Verlag.";
	public static String sourceShort = "Thiel2013Ophthalmology";

	public static int indexLocal = 1;

	public static ArrayList<TextDocument> ListOfDocuments = new ArrayList<>(); // Fälle
	public static ArrayList<String> tableOfContents = new ArrayList<>(); // Inhaltsverzeichnis

	public static void main(String[] args) throws IOException {
		extractContent();

		System.out.println(ListOfDocuments.size());
	}

	public static ArrayList<TextDocument> extractContent() throws IOException {
		if (BOOK_09 == null) {
			return ListOfDocuments;
		}
		boolean readTableOfContents = false;
		boolean readSituationDescription = false;

		int indexOfTableOfContents = 0;

		String actText = "";

		String element = LanguageTools.getContentByTika(BOOK_09);
		Files.write(Paths.get(BOOK_09.replaceAll("pdf", "txt")), element.getBytes());

		String[] lines = element.split("\\n");

		for (int i = 0; i < lines.length; i++) {
			// test readTableOfContents
			if (lines[i].matches("Inhaltsverzeichnis")) {
				readTableOfContents = true;
			} else if (lines[i].startsWith("Serviceteil")) {
				readTableOfContents = false;
			}

			if (lines[i].endsWith("Klinischer Fall")) {
				readSituationDescription = true;
			} else if (lines[i].matches(" z Diskussion")) {
				readSituationDescription = false;

				TextDocument document = new TextDocument();

				if (!(actText.equals(""))) {
					document.text = editOneReport(actText);

					String heading = tableOfContents.get(indexOfTableOfContents);
					if (heading.endsWith(" ")) {
						heading = heading.substring(0, heading.length() - 1);
					}

					document.heading = heading;
					document.source = source;
					document.type = "case description";
					document.topic.add("ophthalmology");
					document.id = Integer.toString(BookReader.index);
					BookReader.index++;

					document.idLong = sourceShort + "-" + indexLocal;
					indexLocal++;

					ListOfDocuments.add(document);
					actText = "";
					indexOfTableOfContents++;

					CheckSum checkSum = new CheckSum();
					checkSum.checkSumText = DigestUtils.md5Hex(actText);
					checkSum.id = Integer.toString(BookReader.index);
					BookReader.listCheckSum.add(checkSum);
				}
			}

			// Inhaltsverzeichnis parsen
			if (readTableOfContents) {
				String content = lines[i];
				// String chapterTitle = "\\d+\\s(.+?)[. ]{2,}\\d+";
				// "\\d+(\\s[a-zA-Zöäüß()?!,’„“:\\.\u2013-]+)*\\s*(\\u002E\\s)*\\d*"
				if ((content.matches("\\d+(\\s[a-zA-Zöäüß()?!,’„“:\\.\u2013-]+)*\\s*(\\u002E\\s)*\\d*")) || (content
						.matches("\\d+\\sAkuter Verschluss der A\\. centralis retinae\\s*(\\u002E\\s)*\\d*"))) {
					content = content.replaceAll("(\\d+)+\\z", "");
					content = content.replaceAll("(\\.\\s)+\\z", "");
					content = content.replaceAll("\\A(\\d+\\s)", "");
					tableOfContents.add(content);
				}
			}

			if (readSituationDescription) {
				String content = lines[i];

				if (!content.matches("\\d+([%°])*|\\d+\\s\\d+|\\d+\\.\\d|\\+(\\s\\d+)*")
						&& (!content.startsWith(
								"M. Thiel, W. Bernauer, M. Zürcher Schüpfer, M. Schmid (Hrsg.), Fallbeispiele Augenheilkunde, "))
						&& (!content.startsWith("DOI 10.1007"))
						&& (!content.matches("M\\. Zürcher Schüpfer, W\\. Dedes"))
						&& (!content.matches("Kapitel\\s\\d+\\s+\\u2022\\s+[a-zA-Zöäüß0-9]+(\\s+[a-zA-Zöäüß0-9]+)*"))
						&& (!content.startsWith("Kapitel "))
						&& (!content.matches("\\d+\\.\\d\\.\\d\\s\\u00b7\\s[a-zA-Zöäüß]+(\\s[a-zA-Zöäüß]+)*\\s*"))
						&& (!content.matches(
								"\\s\\.\\sAbb\\.\\s\\d+\\.\\d+\\s[a-zA-Zöäüß0-9().„“»«,:;-]+(\\s+[a-zA-Zöäüß0-9().„“»«,:;-]+)*\\s*"))
						&& (!content.startsWith(" . Abb. ")) && (!content.startsWith(" ?Granulomatöse Konjunktivitis"))
						&& (!content.startsWith("M. Brunner, A.R. von Hochstetter, J.K. Lacoste, W. Bernauer"))
						&& (!content.startsWith(" Halo’s trotz durchgängiger YAG-Iridotomie"))
						&& (!content.startsWith("J. Stürmer"))
						&& (!content.startsWith(" Massive Progredienz des Glaukoms "))
						&& (!content.equals("trotz „guter“ Druckeinstellung"))
						&& (!content.equals(" Akuter Verschluss der A. centralis retinae"))
						&& (!content.equals("K. Landau")) && (!content.equals(" Traumatisches Maculaloch"))
						&& (!content.equals("M.K. Schmid"))
						&& (!content.equals(" Zunehmender Visusverlust bei diffuser "))
						&& (!content.equals("Glaskörper-Infiltration"))
						&& (!content.equals("T.J. Wolfensberger, Y. Guex-Crosier"))
						&& (!content.equals(" Stauungspapille: Wo liegt die Ursache?"))
						&& (!content.equals("N. Lansel")) && (!content.startsWith(" Patients and doctors delay"))
						&& (!content.equals("Verzug mit Konsequenzen"))
						&& (!content.equals(" Blickdiagnose: „evidenter“ Zusammenhang "))
						&& (!content.equals("von Gewicht und Sehstörungen"))
						&& (!content.equals("A. Thölen, N. Lansel, H. Schramm"))
						&& (!content.equals(" Zunehmend gerötete Augen und Hyperplasie "))
						&& (!content.equals("der Bindehaut")) && (!content.startsWith("Substantia propria"))
						&& (!content.equals(""))) {

					// System.out.println("c_" + content);
					actText = actText + "\n" + content;
				}
			}
		}

		return ListOfDocuments;
	}

	public static String editOneReport(String element) {
		element = element.replaceAll("\00A0", " ");
		element = element.replaceAll("\\. Abb\\.(\u00A0|\u0020)\\d+\\.\\d+", " ");
		element = element.replaceAll(" ?\\(\\,? +und ?\\n? *\\)", " ");
		element = element.replaceAll("\\( ?– ?\\)", " ");
		element = element.replaceAll(" ?\\(\\)", " ");
		element = element.replaceAll(" ?\\( ?a\\, ?\\n?b\\)", " ");
		element = element.replaceAll(" ?\\(\\, ?\\)", " ");
		element = element.replaceAll("\\n\\.", ". ");
		element = element.replaceAll("\\n\\,", ", ");
		element = element.replaceAll("\\,\\s*\\)", ")");
		element = element.replaceAll("\\( ; ", "(");
		element = element.replaceAll(" ?\\( \\,", "");
		element = element.replaceAll(" und \\)", "");
		element = element.replaceAll(" ?\\( [a-z]\\)", "");
		element = element.replaceAll(" ?\\( a\\, b\\)", "");

		element = element.replaceAll(" ?\\( \\)\\.", ". ");
		element = element.replaceAll(" ?\\( \\)\\:", ": ");
		element = element.replaceAll(" ?\\( \\)\\,", ", ");
		element = element.replaceAll(" ?\\( \\)", " ");
		element = element.replaceAll(" \\.", ".");

		// this step is for the jtbd-tokenizer
		element = element.replaceAll("„", "\"");
		element = element.replaceAll("“", "\"");
		element = element.replaceAll("»", "\"");
		element = element.replaceAll("«", "\"");

		element = element.replaceAll("\u2013", "-"); // En dash −
		element = element.replaceAll("\u2212", "-"); // Minus −

		String[] e = element.split("\n");
		String t = "";

		for (int i = 0; i < e.length; i++) {
			if (!(e[i].equals(""))) {
				if (e[i].startsWith(" z Klinischer Fall")) {
					t = t + e[i].replaceAll(" z ", "\n") + "\n";
				} else if (e[i].startsWith(" z ")) {
					t = t + e[i].replaceAll(" z ", "\n") + "\n";
				} else {
					if (t.endsWith("\n")) {
						t = t + e[i];
					} else {
						t = t + " " + e[i];
					}
				}
			}
		}

		t = t.replaceAll("\\n+", "\n");
		t = t.replaceFirst("\n", "");
		t = t.replaceAll(" +", " ");

		element = LanguageTools.removeHyphenNew(t);

		return element;
	}

}
