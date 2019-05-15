package de.julielab.jsyncc.readbooks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tika.exception.TikaException;
import org.apache.uima.UIMAException;

import de.julielab.jsyncc.checksum.CheckSum;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.annotation.TextAnnoation;
import de.julielab.jsyncc.tools.PipelineSentencesTokensFraMed;
import de.julielab.jsyncc.tools.JaxBxmlHandler;
import de.julielab.jsyncc.tools.TextDocumentOutputUtils;

public class BookReader {
//	public static ArrayList<TextDocument> ListDocuments = new ArrayList<TextDocument>();
//	public static ArrayList<CheckSum> listCheckSum = new ArrayList<CheckSum>();

//	public static ArrayList<TextAnnoation> annotatedCorpus = new ArrayList<TextAnnoation>();

	public static int index = 0;
	public static String TEXT = "";

	public static final Path OUT = Paths.get("output");
	public static final Path OUT_XML = Paths.get("output" + "/" + "xml");
	public static final Path OUT_TXT = Paths.get("output" + "/" + "txt");
	public static final Path OUT_SENT = Paths.get("output" + Pattern.quote(System.getProperty("file.separator")) + "annoSenTok");

	public static void main(String[] args) throws IOException, TikaException, InterruptedException, UIMAException {
		
		List<TextDocument> documents = new ArrayList<TextDocument>();
		ArrayList<TextAnnoation> annotatedCorpus = new ArrayList<TextAnnoation>();
		List<CheckSum> checkSums = createCheckSums(documents);

		documents.addAll(BookReaderReportsOrthopedicsAndAccidentSurgery.extractContent());
		documents.addAll(BookReaderGeneral.extractContent());
		documents.addAll(BookReaderReportsEmergency.extractContent());
		documents.addAll(BookReaderCasesSurgery.extractContent());
		documents.addAll(BookReaderCasesAnesthetics.extractContent());
		documents.addAll(BookReaderCulture.extractContent());
		documents.addAll(BookReaderOphthalmology.extractContent());
		documents.addAll(BookReaderCasesInternalMedicine.extractContent());

//		TextDocumentOutputUtils.writeTxtFilesAndAnnotations(documents, OUT_TXT.toString(), OUT_XML.toString());

		annotatedCorpus = TextDocumentOutputUtils.writeTxtFilesAndAnnotations(documents, OUT_TXT.toString(), OUT_XML.toString());

		System.out.println("# documents of JSynCC: " + documents.size());
		System.out.println("# documents of annotated corpus items " + annotatedCorpus.size());

		if (!(OUT.toFile().exists())){
			Files.createDirectory(OUT);
		}

		TextDocumentOutputUtils.writeTxtFiles(OUT_TXT, documents);
		TextDocumentOutputUtils.writeXML(documents, checkSums, OUT_XML);
		TextDocumentOutputUtils.writeCheckSums(documents, checkSums);
	}

	public static List<CheckSum> createCheckSums(List<TextDocument> listDocuments) {
		List<CheckSum> listCheckSum = new ArrayList<>();

		for (int i = 0; i < listDocuments.size(); i++) {
			String text = listDocuments.get(i).getText();

			CheckSum checkSum = new CheckSum();
			checkSum.setCheckSumText(DigestUtils.md5Hex(text));
			checkSum.setId(listDocuments.get(i).getId());
			checkSum.setIdLong(listDocuments.get(i).getIdLong());

			listCheckSum.add(checkSum);
		}

		return listCheckSum;
	}
}
