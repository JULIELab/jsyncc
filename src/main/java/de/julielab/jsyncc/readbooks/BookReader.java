package de.julielab.jsyncc.readbooks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.exception.ContextedException;
import org.apache.uima.UIMAException;

import de.julielab.jsyncc.readbooks.casedescriptions.CasesAnesthetics;
import de.julielab.jsyncc.readbooks.casedescriptions.CasesClinicalInfectiological;
import de.julielab.jsyncc.readbooks.casedescriptions.CasesCulture;
import de.julielab.jsyncc.readbooks.casedescriptions.CasesGeriatricCare;
import de.julielab.jsyncc.readbooks.casedescriptions.CasesPathology;
import de.julielab.jsyncc.readbooks.casedescriptions.CasesPolyneuropathy;
import de.julielab.jsyncc.readbooks.casedescriptions.CasesSleepMedicine;
import de.julielab.jsyncc.readbooks.casedescriptions.discussion.CasesEmergency;
import de.julielab.jsyncc.readbooks.casedescriptions.discussion.CasesPediatricEmergencies;
import de.julielab.jsyncc.readbooks.casedescriptions.discussion.CasesRheumatologyPractice;
import de.julielab.jsyncc.readbooks.casereports.CasesAcuteAbdomenChildhood;
import de.julielab.jsyncc.readbooks.casereports.CasesAging;
import de.julielab.jsyncc.readbooks.casereports.CasesBasicsGynecology;
import de.julielab.jsyncc.readbooks.casereports.CasesBasicsRadiology;
import de.julielab.jsyncc.readbooks.casereports.CasesBoysMedicine;
import de.julielab.jsyncc.readbooks.casereports.CasesGeneralMedicine;
import de.julielab.jsyncc.readbooks.casereports.CasesHumanGenetics;
import de.julielab.jsyncc.readbooks.casereports.CasesIntensiveCare;
import de.julielab.jsyncc.readbooks.casereports.CasesInternalMedicine;
import de.julielab.jsyncc.readbooks.casereports.CasesPediatrics;
import de.julielab.jsyncc.readbooks.casereports.CasesSportsCardiology;
import de.julielab.jsyncc.readbooks.casereports.CasesSurgery;
import de.julielab.jsyncc.readbooks.casereports.discussion.CasesOphthalmology;
import de.julielab.jsyncc.readbooks.emergencyreports.ReportsEmergency;
import de.julielab.jsyncc.readbooks.operativereports.ReportsGeneralSurgery;
import de.julielab.jsyncc.readbooks.operativereports.ReportsGeneralVisceralVascularThoracicSurgery;
import de.julielab.jsyncc.readbooks.operativereports.ReportsOrthopedicsAndAccidentSurgery;
import de.julielab.jsyncc.readbooks.pubmed.PubMedCaseAbstracts;
import de.julielab.jsyncc.tools.JsonReader;
import de.julielab.jsyncc.tools.TextDocumentOutputUtils;

public class BookReader {

	public static final Path BOOKS = Paths.get("books");
	public static final String OUPUT_DIR = "output";

	public static final Path PATH_OUT				= Paths.get(OUPUT_DIR);
	public static final Path PATH_OUT_TXT			= Paths.get(OUPUT_DIR + File.separator + "txt");
	public static final Path PATH_OUT_XML			= Paths.get(OUPUT_DIR + File.separator + "xml");
	public static final Path PATH_OUT_SENTENCES		= Paths.get(OUPUT_DIR + File.separator + "sentences");
	public static final Path PATH_OUT_TOKENS		= Paths.get(OUPUT_DIR + File.separator + "tokens");
	public static final Path PATH_OUT_TYPES			= Paths.get(OUPUT_DIR + File.separator + "types_vocabulary");
	public static final Path PATH_OUT_MISC			= Paths.get(OUPUT_DIR + File.separator + "misc");
	public static final Path PATH_OUT_STATS			= Paths.get(OUPUT_DIR + File.separator + "stats");

	public static Integer booksIndex = 0;

	public static final String BOOK_PROP_JSON = "Books_properties.json";

	public static void main(String[] args) throws IOException, UIMAException, ContextedException, InterruptedException
	{

		if (!(PATH_OUT.toFile().exists()))
		{
			Files.createDirectory(PATH_OUT);
		}

		if (!(PATH_OUT_STATS.toFile().exists()))
		{
			Files.createDirectory(PATH_OUT_STATS);
		}

		List<BookProperties> listBookProperties = JsonReader.getBookList(BOOK_PROP_JSON);

		System.out.println("Size in Book list / " + BOOK_PROP_JSON + ": " + listBookProperties.size());
		System.out.println();

		int allDocuments = 0;
		String meta_data = "";
		Set<String> all_topics = new HashSet<>();

		for (int i = 0; i < listBookProperties.size(); i++)
		{
			BookProperties bookProp_i = JsonReader.getBookPropertyById(listBookProperties.get(i).getBookId(), BOOK_PROP_JSON);

			if (bookProp_i.bookId.equals("01"))
			{
				listBookProperties.get(i).textDocuments = ReportsOrthopedicsAndAccidentSurgery.extractContent_1(bookProp_i);
			}

			if (bookProp_i.bookId.equals("02"))
			{
				listBookProperties.get(i).textDocuments = ReportsOrthopedicsAndAccidentSurgery.extractContent_2(bookProp_i);
			}

			if (bookProp_i.bookId.equals("03"))
			{
				listBookProperties.get(i).textDocuments = ReportsOrthopedicsAndAccidentSurgery.extractContent_3(bookProp_i);
			}

			if (bookProp_i.bookId.equals("04"))
			{
				listBookProperties.get(i).textDocuments = ReportsGeneralSurgery.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("05"))
			{
				listBookProperties.get(i).textDocuments = CasesEmergency.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("06"))
			{
				listBookProperties.get(i).textDocuments = CasesSurgery.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("07"))
			{
				listBookProperties.get(i).textDocuments = CasesAnesthetics.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("08"))
			{
				listBookProperties.get(i).textDocuments = CasesAging.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("09"))
			{
				listBookProperties.get(i).textDocuments = CasesIntensiveCare.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("10"))
			{
				listBookProperties.get(i).textDocuments = CasesCulture.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("11"))
			{
				listBookProperties.get(i).textDocuments = CasesOphthalmology.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("12"))
			{
				listBookProperties.get(i).textDocuments = CasesGeriatricCare.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("13"))
			{
				listBookProperties.get(i).textDocuments = CasesPolyneuropathy.extractContent(bookProp_i);
			}
			
			if (bookProp_i.bookId.equals("14"))
			{
				listBookProperties.get(i).textDocuments = CasesRheumatologyPractice.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("15"))
			{
				listBookProperties.get(i).textDocuments = CasesPediatrics.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("16"))
			{
				listBookProperties.get(i).textDocuments = CasesInternalMedicine.extractContent(bookProp_i);
			}
			
			if (bookProp_i.bookId.equals("17"))
			{
				listBookProperties.get(i).textDocuments = CasesBoysMedicine.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("18"))
			{
				listBookProperties.get(i).textDocuments = CasesPediatricEmergencies.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("19"))
			{
				listBookProperties.get(i).textDocuments = CasesBasicsRadiology.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("20"))
			{
				listBookProperties.get(i).textDocuments = CasesClinicalInfectiological.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("21"))
			{
				listBookProperties.get(i).textDocuments = CasesHumanGenetics.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("22")) // nur 3 Texte
			{
				
				listBookProperties.get(i).textDocuments = CasesSportsCardiology.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("23"))
			{
				listBookProperties.get(i).textDocuments = CasesPathology.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("24")) // 17 Texte
			{
				listBookProperties.get(i).textDocuments = CasesAcuteAbdomenChildhood.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("25"))
			{
				listBookProperties.get(i).textDocuments = ReportsGeneralVisceralVascularThoracicSurgery.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("26"))
			{
				listBookProperties.get(i).textDocuments = CasesSleepMedicine.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("27"))
			{
				listBookProperties.get(i).textDocuments = CasesGeneralMedicine.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("28"))
			{
				listBookProperties.get(i).textDocuments = CasesBasicsGynecology.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("29"))
			{
				listBookProperties.get(i).textDocuments = ReportsEmergency.extractContent(bookProp_i);
			}

			if (bookProp_i.bookId.equals("30"))
			{
				listBookProperties.get(i).textDocuments = PubMedCaseAbstracts.extractContent(bookProp_i);
			}

			System.out.println((i+1) + " // bookId: " + listBookProperties.get(i).getBookId() + " - Source: " + listBookProperties.get(i).getBookPath());

			allDocuments = allDocuments + listBookProperties.get(i).textDocuments.size();
			System.out.println("documents: " + listBookProperties.get(i).textDocuments.size() + "/" + allDocuments);

			System.out.println(
				bookProp_i.getTitle() + " " +
				bookProp_i.getEditorAuthor() + " " +
				bookProp_i.getYear() + " " +
				bookProp_i.getPublisher() + " " + 
				bookProp_i.getDoi()
			);
			System.out.println();
			booksIndex++;
			
			for (int j = 0; j < listBookProperties.get(i).textDocuments.size(); j++)
			{
				String meta_data_line = 
						listBookProperties.get(i).textDocuments.get(j).getIdLong() + '\t' +
						listBookProperties.get(i).textDocuments.get(j).getSourcShort() + '\t' +
						listBookProperties.get(i).textDocuments.get(j).getBookId() + '\t' +
						listBookProperties.get(i).textDocuments.get(j).getTopic() + '\t'+
						listBookProperties.get(i).textDocuments.get(j).getDocumentType();

				System.out.println(meta_data_line);
				meta_data = meta_data + meta_data_line + '\n';

				for (int k = 0; k < listBookProperties.get(i).textDocuments.get(j).getTopic().size(); k++ )
				{
					all_topics.add(listBookProperties.get(i).textDocuments.get(j).getTopic().get(k));
				}
			}
		}

		System.out.println("Books: " + booksIndex);
		System.out.println("Documents: " + allDocuments);

		TextDocumentOutputUtils.exportTxtFiles(listBookProperties, PATH_OUT_TXT, PATH_OUT_MISC, PATH_OUT_STATS);

		System.out.println("Books: " + booksIndex);
		System.out.println("Documents: " + allDocuments);
		System.out.println("Clinical Topics: " + all_topics.size() + ' ' + all_topics);
		System.out.println("Count of all topics: " + all_topics.size());

		meta_data = "ID\tSourceShort\tBookId\tTopics\tDocumentTypes\n" + meta_data;
		Files.write(Paths.get(PATH_OUT_STATS + File.separator + "meta_data.csv"), meta_data.getBytes());
		Files.write(Paths.get(PATH_OUT_STATS + File.separator + "all_topics.txt"), all_topics.toString().getBytes());
		System.out.println("Document per source and topic, see in " + PATH_OUT_STATS + File.separator + "meta_data.csv");
		System.out.println("All topics see in " + PATH_OUT_STATS + File.separator + "all_topics.txt");

		TextDocumentOutputUtils.exportCorpusAsXML(
			listBookProperties,
			PATH_OUT_XML
		);

		TextDocumentOutputUtils.exportMD5HexCheckSumsAsXML(
			listBookProperties,
			PATH_OUT_XML
		);

		TextDocumentOutputUtils.exportSentencesAndTokens(
			listBookProperties,
			PATH_OUT_SENTENCES,
			PATH_OUT_TOKENS,
			PATH_OUT_TYPES,
			PATH_OUT_MISC,
			PATH_OUT_XML,
			PATH_OUT_STATS
		);
	}
}
