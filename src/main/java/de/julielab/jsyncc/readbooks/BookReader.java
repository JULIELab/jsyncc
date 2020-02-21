package de.julielab.jsyncc.readbooks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.exception.ContextedException;
import org.apache.uima.UIMAException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.julielab.jsyncc.tools.TextDocumentOutputUtils;
import de.julielab.jsyncc.tools.YamlReader;
import de.julielab.jsyncc.count.CountCorpus;
import de.julielab.jsyncc.readbooks.casereports.CasesInternalMedicine;
import de.julielab.jsyncc.readbooks.casereports.CasesSurgery;
import de.julielab.jsyncc.readbooks.operativereports.ReportsGeneralSurgery;
import de.julielab.jsyncc.readbooks.operativereports.ReportsOrthopedicsAndAccidentSurgery;

import de.julielab.jsyncc.checksum.CheckSum;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.annotation.TextAnnotation;

public class BookReader {
	private static final Logger LOGGER = LoggerFactory.getLogger(BookReader.class);

	public static final Path BOOKS = Paths.get("books");
	public static final String OUPUT_DIR = "output";

	public static final Path OUT = Paths.get(OUPUT_DIR);
	public static final Path OUT_TXT = Paths.get(OUPUT_DIR + "/" + "txt");
	public static final Path OUT_TXT_SECTION = Paths.get(OUPUT_DIR + "/" + "section");
	public static final Path OUT_XML = Paths.get(OUPUT_DIR + "/" + "xml");
	public static final Path OUT_SENTENCES = Paths.get(OUPUT_DIR + "/" + "sentences");
	public static final Path OUT_TOKENS = Paths.get(OUPUT_DIR + "/" + "tokens");

	public static Integer booksIndex = 0;
	public static YamlReader yaml;

	public static void main(String[] args) throws IOException, UIMAException, ContextedException
	{
		yaml = new YamlReader();
		if ( !(OUT.toFile().exists()) )
		{
			Files.createDirectory(OUT);
		}

		List<TextDocument> documents = readAllBooks(BOOKS);
		System.out.println("# TextDocuments:\t" + documents.size());

		CountCorpus.countTextDocumentElements(documents, false);

		List<TextAnnotation> annotatedCorpus = new ArrayList<>();
		annotatedCorpus = TextDocumentOutputUtils.writeTxtFilesAndAnnotations(documents, OUT_TXT.toString(), OUT_XML.toString());

		System.out.println("# documents of JSynCC: " + documents.size());
		System.out.println("# documents of annotated corpus items " + annotatedCorpus.size());

		if (!(OUT.toFile().exists())){
			Files.createDirectory(OUT);
		}

		TextDocumentOutputUtils.writeTxtFiles(OUT_TXT, documents);
		TextDocumentOutputUtils.writeTxtSectionFiles(OUT_TXT_SECTION, documents);
		TextDocumentOutputUtils.writeSentencesAndTokens(OUT_SENTENCES, OUT_TOKENS, documents);

		List<CheckSum> checkSums = CheckSum.createCheckSums(documents);
		TextDocumentOutputUtils.writeCheckSums(documents, checkSums);
		TextDocumentOutputUtils.writeXML(documents, checkSums, OUT_XML);
	}

	private static List<TextDocument> filterType(List <TextDocument> documents, String filter)
	{
		List <TextDocument> documentsFilter = new ArrayList<>();
		for (int i = 0; i < documents.size(); i++)
		{
			if (documents.get(i).getType().contains(filter))
			{
				documentsFilter.add(documents.get(i));
			}
		}
		return documentsFilter;
	}

	private static List<TextDocument> readAllBooks(Path booksRoot) throws IOException
	{
		List<TextDocument> documents = new ArrayList<>();

//		Books without Interface
		try
		{
			// // 1-3
			documents.addAll(ReportsOrthopedicsAndAccidentSurgery.extractContent());
			System.out.println(ReportsOrthopedicsAndAccidentSurgery.BOOK_1);
			System.out.println(ReportsOrthopedicsAndAccidentSurgery.BOOK_2);
			System.out.println(ReportsOrthopedicsAndAccidentSurgery.BOOK_3);

			// 4
			ReportsGeneralSurgery reportsGeneral = new ReportsGeneralSurgery();
			System.out.println(ReportsGeneralSurgery.BOOK);
			documents.addAll(reportsGeneral.extractContent(reportsGeneral.parseBook(Paths.get("books/04-Operationsberichte-fuer-Einsteiger"))));

			// 6
			documents.addAll(CasesSurgery.extractContent("books/06-Fallbuch-Chirurgie"));
			System.out.println(CasesSurgery.BOOK);

			// 10
			documents.addAll(CasesInternalMedicine.extractContent());
			System.out.println(CasesInternalMedicine.BOOK);

			booksIndex = booksIndex + 6;

		} catch (ContextedException | InterruptedException e) {
			e.printStackTrace();
		}

		// Books with Interface

		try (Stream<Path> files = Files.walk(booksRoot, 2))
		{
			Iterable<Path> pdfIter
				= files.filter(
						path -> path.getFileName().toString().endsWith(".pdf")
					&&
						path.getNameCount() == booksRoot.getNameCount() + 2
					)::iterator;

			for (Path documentPath : pdfIter)
			{
				documents.addAll(parseDocument(documentPath));
			}
		}

		System.out.println("# Books:\t\t" + booksIndex);

		return documents;
	}

	private static List<TextDocument> parseDocument(Path documentPath)
	{
		List<TextDocument> documents = new ArrayList<>();
		String dirName = documentPath.getName(documentPath.getNameCount() - 2).toString();

		try {
			BookExtractor extractor = BookExtractor.getExtractor(dirName);
			String bookTxt = extractor.parseBook(documentPath);

			if (!extractor.validateText(bookTxt))
			{
				throw new ContextedException(
						String.format("The file %s does not belong into directory %s!",
						documentPath.getFileName().toString(), dirName)
						);
			}
			documents = extractor.extractContent(bookTxt);
			System.out.println(dirName);
			booksIndex++;
		}
		catch (ContextedException e)
		{
			if (e.getMessage().startsWith("Unknown directory"))
			{
				LOGGER.warn(String.format("No BookExtractor for directory %s!", dirName));
			}
			else
			{
				LOGGER.error(String.format("Error while parsing %s!", documentPath.toString()), e);
			}
		}
		return documents;
	}
}
