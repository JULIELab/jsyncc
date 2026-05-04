package de.julielab.jsyncc.readbooks.pubmed;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.JsonReader;

/** 
 * @author Christina Lohr
 * Install the Entres API from:
 * https://www.ncbi.nlm.nih.gov/books/NBK179288/
 * 
 * This class works on the output of the following command:
 * esearch -db pubmed -query "Case Reports[Publication Type] AND GER[LA]" | efetch -format xml > allGermanPubMedCaseAbstracts.xml
 * 
 */

public class PubMedCaseAbstracts
{
	public static List<TextDocument> extractContent(BookProperties bookProperties) throws IOException
	{
		List<TextDocument> textDocuments = new ArrayList<>();

		HashSet<String> pmids = new HashSet<String>();

		String language = "ger";

		List<String> lines = Files.readAllLines(Paths.get(bookProperties.bookPath));

		boolean readAbstract = false;
		String pmid = "";
		String text = "";

		for (int i=0; i < lines.size(); i++)
		{
			if (lines.get(i).startsWith("<PMID Version=\"1\">"))
			{
				pmid = lines.get(i).replaceAll("<PMID Version=\"1\">", "");
				pmid = pmid.replaceAll("</PMID>", "");
			}

			if (readAbstract)
			{
				text = text + "\n" + lines.get(i);
			}

			if (lines.get(i).startsWith("<OtherAbstract Type=\"Publisher\" Language=\""+ language +"\">"))
			{
				readAbstract = true;
			}

			if ((lines.get(i).startsWith("</OtherAbstract>")) && (!(text.isEmpty())) )
			{
				readAbstract = false;

				text = text.replaceAll("<AbstractText>","");
				text = text.replaceAll("</AbstractText>","");
				text = text.replaceAll("<AbstractText/>","");
				text = text.replaceAll("</OtherAbstract>","");

				text = text.replaceAll("<AbstractText Label=\"", "");
				text = text.replaceAll("\" NlmCategory=\"UNASSIGNED\">", "\n");
				text = text.replaceAll("\" NlmCategory=\"BACKGROUND\">", "\n");

				text = text.replaceAll("\">", "\n");
				text = text.replaceAll("\u00AD", ""); //soft hyphen

				if (text.startsWith("\n"))
				{
					text = text.replaceFirst("\n", "");
				}

				TextDocument textDocument = new TextDocument();
				textDocument.setText(text);
				textDocument.setSource("PubMed Case Abstract, PMID: " + pmid);
				textDocument.setDocumentType(bookProperties.documentType.get(0));
				textDocument.setTopic(bookProperties.topics);
				textDocument.setBookId(bookProperties.sourceShort);

				LanguageDetector detector = new OptimaizeLangDetector().loadModels();
				LanguageResult result = detector.detect(text);
				String lang = result.getLanguage();

				if ( (!(pmids.contains(pmid))) && (lang.equals("de")) )
				{
					String jsyncc_id = JsonReader.getPMIDkey("jsyncc_2_pubmed_keys.json", pmid);
					if (jsyncc_id != "==")
					{
						textDocument.setIdLong(bookProperties.sourceShort + '-' + pmid + "-" + jsyncc_id);
						textDocuments.add(textDocument);
						pmids.add(pmid);
					}
				}
				else
				{
					System.out.println("PUBMED-TEXT-WARNING: " + pmid + " has more than 1 Abstract or is not German.");
				}

				text = "";
			}
		}

		Files.write(Paths.get("jsyncc_pmids.txt"), pmids.toString().getBytes());

		return textDocuments;
	}
}
