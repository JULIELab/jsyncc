package de.julielab.jsyncc.readbooks.casereports.discussion;

import de.julielab.jsyncc.readbooks.BookExtractor;
import de.julielab.jsyncc.readbooks.BookReader;
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
import java.nio.file.Path;

public class CasesOphthalmology implements BookExtractor {
	public static final Path BOOKS = Paths.get("books");
	public static final String OUPUT_DIR = "output";

	public static final Path OUT = Paths.get(OUPUT_DIR);
	public static final Path OUT_TXT = Paths.get(OUPUT_DIR + "/" + "txt");
	public static final Path OUT_TXT_SECTION = Paths.get(OUPUT_DIR + "/" + "section");
	public static final Path OUT_XML = Paths.get(OUPUT_DIR + "/" + "xml");
	public static final Path OUT_SENTENCES = Paths.get(OUPUT_DIR + "/" + "sentences");
	public static final Path OUT_TOKENS = Paths.get(OUPUT_DIR + "/" + "tokens");

	private static final int ID = 9;
	private static final String SOURCE = BookReader.yaml.getSourceById(ID);
	private static final String SOURCE_SHORT = BookReader.yaml.getSourceShortById(ID);

	public static final String BOOK = "books/09-Fallbeispiele-Augenheilkunde/978-3-642-42219-5.pdf";
	public static final String TYPE_1 = "CaseReportLong";
	public static final String TYPE_2 = "Discussion";
	public static final String TOPIC = "Augenheilkunde";

	@Override
	public List<TextDocument> extractContent(String plainText) {
		List<TextDocument> listDocuments = new ArrayList<>();

		if (validateText(plainText)) {
			List<String> headings = getHeadings(plainText);
			boolean readCaseText = false;
			boolean readDiscussionText = false;
			int index = 0;
			String text = "";
			String textDiscussion = "";
			String[] lines = plainText.split("\\n");

			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];

				if (line.endsWith("Klinischer Fall")) {
					readCaseText = true;
					readDiscussionText = false;

					if (!(textDiscussion.isEmpty())) {
						textDiscussion = cleanDiscussionText(textDiscussion, index);
						listDocuments.add(createTextDocument(headings.get(index), textDiscussion, (index), TYPE_2, ""));

						textDiscussion = "";
					}
				} else if ((readCaseText) && (line.matches(" z Diskussion"))) {
					readCaseText = false;
					readDiscussionText = true;

					if (!(text.isEmpty())) {
						index++;
						text = cleanText(text);
						listDocuments.add(createTextDocument(headings.get(index - 1), text, index, TYPE_1,
								createSectionText(text)));
						text = "";
					}
				} else if ((readDiscussionText) && (line.matches("Literatur"))) {
					readDiscussionText = false;

					if (!(textDiscussion.isEmpty())) {
						textDiscussion = cleanDiscussionText(textDiscussion, index);
						listDocuments
								.add(createTextDocument(headings.get(index - 1), textDiscussion, (index), TYPE_2, ""));

						textDiscussion = "";
					}
				}

				if (readCaseText) {
					if (!line.matches("\\d+([%°])*|\\d+\\s\\d+|\\d+\\.\\d|\\+(\\s\\d+)*")
							&& (!line.startsWith(
									"M. Thiel, W. Bernauer, M. Zürcher Schüpfer, M. Schmid (Hrsg.), Fallbeispiele Augenheilkunde, "))
							&& (!line.startsWith("DOI 10.1007")) && (!line.matches("M\\. Zürcher Schüpfer, W\\. Dedes"))
							&& (!line.matches("Kapitel\\s\\d+\\s+\\u2022\\s+[a-zA-Zöäüß0-9]+(\\s+[a-zA-Zöäüß0-9]+)*"))
							&& (!line.startsWith("Kapitel "))
							&& (!line.matches("\\d+\\.\\d\\.\\d\\s\\u00b7\\s[a-zA-Zöäüß]+(\\s[a-zA-Zöäüß]+)*\\s*"))
							&& (!line.matches(
									"\\s\\.\\sAbb\\.\\s\\d+\\.\\d+\\s[a-zA-Zöäüß0-9().„“»«,:;-]+(\\s+[a-zA-Zöäüß0-9().„“»«,:;-]+)*\\s*"))
							&& (!line.startsWith(" . Abb. ")) && (!line.startsWith(" Granulomatöse Konjunktivitis"))
							&& (!line.startsWith("M. Brunner, A.R. von Hochstetter, J.K. Lacoste, W. Bernauer"))
							&& (!line.startsWith(" Halo’s trotz durchgängiger YAG-Iridotomie"))
							&& (!line.startsWith("J. Stürmer"))
							&& (!line.startsWith(" Massive Progredienz des Glaukoms "))
							&& (!line.equals("trotz „guter“ Druckeinstellung"))
							&& (!line.equals(" Akuter Verschluss der A. centralis retinae"))
							&& (!line.equals("K. Landau")) && (!line.equals(" Traumatisches Maculaloch"))
							&& (!line.equals("M.K. Schmid"))
							&& (!line.equals(" Zunehmender Visusverlust bei diffuser "))
							&& (!line.equals("Glaskörper-Infiltration"))
							&& (!line.equals("T.J. Wolfensberger, Y. Guex-Crosier"))
							&& (!line.equals(" Stauungspapille: Wo liegt die Ursache?")) && (!line.equals("N. Lansel"))
							&& (!line.startsWith(" Patients and doctors delay"))
							&& (!line.equals("Verzug mit Konsequenzen"))
							&& (!line.equals(" Blickdiagnose: „evidenter“ Zusammenhang "))
							&& (!line.equals("von Gewicht und Sehstörungen"))
							&& (!line.equals("A. Thölen, N. Lansel, H. Schramm"))
							&& (!line.equals(" Zunehmend gerötete Augen und Hyperplasie "))
							&& (!line.equals("der Bindehaut")) && (!line.startsWith("Substantia propria"))
							&& (!line.startsWith("W. Bernauer, J.K. Lacoste"))
							&& (!line.equals(" Kataraktoperation als Drucksenkung bei "))
							&& (!line.equals("Kapselhäutchen und Kapselhäutchenglaukom?")) && (!line.equals(""))) {
						text = text + "\n" + line;
					}
				}

				if (readDiscussionText) {
					if ((!(line.matches("\\d+(\\s+\\d+)?"))) && (!(line.startsWith("DOI 10.1007")))
							&& (!(line.startsWith(" z Diskussion"))) && (!(line.equals("Diskussion")))
							&& (!(line.startsWith("M. Thiel"))) && (!(line.startsWith("M.A. Thiel")))
							&& (!(line.matches("Kapitel \\d+\u2002 •.*"))) && (!(line.startsWith(" Fataler Tumor des")))
							&& (!(line.startsWith("M. Zürcher")))) {
						textDiscussion = textDiscussion + line + "\n";
					}
				}
			}
		}

		return listDocuments;
	}

	private static List<String> getHeadings(String plainText) {
		List<String> headings = new ArrayList<>();

		plainText = plainText.replaceAll("intraepithelialen \nNeoplasie", "intraepithelialen Neoplasie");
		plainText = plainText.replaceAll("progredientem \nKeratokonus", "progredientem Keratokonus");
		plainText = plainText.replaceAll("und \nKapselhäutchenglaukom", "und Kapselhäutchenglaukom");

		String regExHeadings = "\\d\\d?\\s(.*)\\.\\s(\\.\\s)+\\d+";

		Pattern textPattern = Pattern.compile(regExHeadings);
		Matcher textMatcher = textPattern.matcher(plainText);

		while (textMatcher.find()) {
			String title = textMatcher.group();

			if (!(title.contains("Stichwortverzeichnis"))) {
				title = title.replaceAll("\\d\\d?\\s", "");
				title = title.replaceAll("\\s(\\.\\s)+\\d+", "");
				headings.add(title);
			}
		}

		return headings;
	}

	private static String cleanText(String text) {
		text = text.replaceAll("\u00A0", " ");
		text = text.replaceAll("\\. Abb\\.(\u00A0|\u0020)\\d+\\.\\d+", " ");
		text = text.replaceAll(" ?\\(\\,? +und ?\\n? *\\)", " ");
		text = text.replaceAll("\\( ?– ?\\)", " ");
		text = text.replaceAll(" ?\\(\\)", " ");
		text = text.replaceAll(" ?\\( ?a\\, ?\\n?b\\)", " ");
		text = text.replaceAll(" ?\\(\\, ?\\)", " ");
		text = text.replaceAll("\\n\\.", ". ");
		text = text.replaceAll("\\n\\,", ", ");
		text = text.replaceAll("\\,\\s*\\)", ")");
		text = text.replaceAll("\\( ; ", "(");
		text = text.replaceAll(" ?\\( \\,", "");
		text = text.replaceAll(" und \\)", "");
		text = text.replaceAll(" ?\\( [a-z]\\)", "");
		text = text.replaceAll(" ?\\( a\\, b\\)", "");

		text = text.replaceAll(" ?\\( \\)\\.", ". ");
		text = text.replaceAll(" ?\\( \\)\\:", ": ");
		text = text.replaceAll(" ?\\( \\)\\,", ", ");
		text = text.replaceAll(" ?\\( \\)", " ");
		text = text.replaceAll(" \\.", ".");

		String[] lines = text.split("\n");
		text = "";

		for (int i = 0; i < lines.length; i++) {
			if (!(lines[i].equals(""))) {
				if (lines[i].startsWith(" z Klinischer Fall")) {
					text = text + lines[i].replaceAll(" z ", "\n") + "\n";
				} else if (lines[i].startsWith(" z ")) {
					text = text + lines[i].replaceAll(" z ", "\n") + "\n";
				} else {
					if (text.endsWith("\n")) {
						text = text + lines[i];
					} else {
						text = text + " " + lines[i];
					}
				}
			}
		}

		text = text.replaceAll("\\n+", "\n");
		text = text.replaceFirst("\n", "");
		text = text.replaceAll(" +", " ");
		text = text.replaceAll("\u202F", " "); // narrow space
		text = LanguageTools.removeHyphenNew(text);

		return text;
	}

	private String cleanDiscussionText(String text, int index) {
		String[] segements = text.split("\\n\\n+");
		text = "";

		for (int i = 0; i < segements.length; i++) {
			if (!(segements[i].startsWith(" "))) {
				text = text + "\n" + segements[i];
			}
		}

		text = text.replaceFirst("\n", "");

		text = text.replaceAll("\\n", " ");
		text = text.replaceAll("\u00A0", " "); // No-Break Space
		text = text.replaceAll("\u202F", " "); // narrow space
		text = LanguageTools.removeHyphenNew(text);
		text = text.replaceAll("\\s?\\(\\.\\sAbb\\.\\s\\d+.\\d+\\)", "");

		if (index == 15) {
			text = text.replaceAll(" V Uvea.*", "");
		} else if (index == 19) {
			text = text.replaceAll("Alm A.*", "");
		} else if (index == 21) {
			text = text.replaceAll(" RNFL.*μm", "");
			text = text.replaceAll(" TARGET PRESSURE.*", "");
		} else if (text.endsWith("\n")) {
			text = text.substring(0, text.length() - 1);
		} else if (index == 23) {
			text = text.replaceAll("schonen\\. Di.*", "schonen.");
		} else if (index == 24) {
			text = text.replaceAll(" Änderung PeriData .*", "");
		} else if (index == 25) {
			text = text.replaceAll("Operation kommen\\..*", "Operation kommen.");
		} else if (index == 31) {
			text = text.replaceAll("Pupil .* --> ", "");
		}

		return text;
	}

	@Override
	public String parseBook(Path pdfPath) throws ContextedException {
		String plainText = ExtractionUtils.getContentByTika(BOOK);
		try {
			Files.write(Paths.get(BOOK.replaceAll("pdf", "txt")), plainText.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return plainText;
	}

	@Override
	public boolean validateText(String plainText) {
		return plainText.contains("ISBN 978-3-642-42219-5");
	}

	private static TextDocument createTextDocument(String heading, String text, int index, String type,
			String textSection) {
		if (type.equals(TYPE_1)) {
			index = ((index * 2) - 1);
		} else {
			index = index * 2;
		}

		TextDocument textDocument = new TextDocument();
		textDocument.setHeading(heading);
		textDocument.setText(text);
		textDocument.setTextSection(textSection);
		textDocument.setType(type);
		textDocument.getTopic().add(TOPIC);
		textDocument.setSource(SOURCE);
		textDocument.setIdLong(SOURCE_SHORT + "-" + index);

		return textDocument;
	}

	private String createSectionText(String text) {
		String[] lines = text.split("\n");
		text = "";

		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("Klinischer Fall")) {
				text = text + "\n" + "<Historysection>";
			} else if (lines[i].startsWith("Abklärung und Intervention")) {
				text = text + "\n" + "</Historysection>";
				text = text + "\n" + "<Proceduressection>";
			} else if (lines[i].startsWith("Verlauf")) {
				text = text + "\n" + "</Proceduressection>";
				text = text + "\n" + "<Hospitalcoursesection>";
			}

			text = text + "\n" + lines[i];
		}

		if (text.contains("<Hospitalcoursesection>")) {
			text = text + "\n" + "</Hospitalcoursesection>";
		} else if (text.contains("<Proceduressection>")) {
			text = text + "\n" + "</Proceduressection>";
		}

		return text;
	}
}
