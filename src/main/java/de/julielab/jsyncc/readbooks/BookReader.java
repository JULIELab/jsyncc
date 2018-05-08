package de.julielab.jsyncc.readbooks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.apache.tika.exception.TikaException;
import org.apache.uima.UIMAException;

import de.julielab.jsyncc.annotation.TextAnnoation;
import de.julielab.jsyncc.tools.GetSentencesTokensFraMed;
import de.julielab.jsyncc.tools.JAXBXMLHandler;

public class BookReader {
	public static ArrayList<TextDocument> ListDocuments = new ArrayList<TextDocument>();
	public static ArrayList<CheckSum> listCheckSum = new ArrayList<CheckSum>();

	public static ArrayList<TextAnnoation> annotatedCorpus = new ArrayList<TextAnnoation>();

	public static int index = 0;
	public static String TEXT = "";

	public static Path outDir = Paths.get("output");
	public static Path outDirXML = Paths.get("output" + "/" + "xml");
	public static Path outDirTXT = Paths.get("output" + "/" + "txt");
	public static Path outDirSent = Paths
			.get("output" + Pattern.quote(System.getProperty("file.separator")) + "annoSenTok");

	public static void main(String[] args) throws IOException, TikaException, InterruptedException, UIMAException {
		if (!(outDir.toFile().exists())) {
			Files.createDirectory(outDir);
		}

		if (!(outDirXML.toFile().exists())) {
			Files.createDirectory(outDirXML);
		}

		if (!(outDirTXT.toFile().exists())) {
			Files.createDirectory(outDirTXT);
		}

		ListDocuments.addAll(BookReaderReportsOrthopedicsAndAccidentSurgery.extractContent());
		ListDocuments.addAll(BookReaderGeneral.extractContent());
		ListDocuments.addAll(BookReaderReportsEmergency.extractContent());
		ListDocuments.addAll(BookReaderCasesSurgery.extractContent());
		ListDocuments.addAll(BookReaderCasesAnesthetics.extractContent());
		ListDocuments.addAll(BookReaderCulture.extractContent());
		ListDocuments.addAll(BookReaderOphthalmology.extractContent());
		ListDocuments.addAll(BookReaderCasesInternalMedicine.extractContent());

		writeXML();
		writeCheckSums();
		writeTxtFilesAndAnnotations();

		System.out.println("# documents of JSynCC: " + ListDocuments.size());
		System.out.println("# checkSums of JSynCC: " + listCheckSum.size());
		System.out.println("annotatedCorpus " + annotatedCorpus.size());
	}

	public static void writeXML() throws IOException {
		String corpus = "jsyncc-corpus.xml";

		try {
			JAXBXMLHandler.marshalCorpus(ListDocuments, new File(outDirXML + "/" + corpus));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		System.out.println(outDirXML + "/" + corpus + " created successfully.");
	}

	public static void writeCheckSums() throws IOException {
		String checksums = "jsyncc-checksums.xml";

		try {
			JAXBXMLHandler.marshalCheckSum(listCheckSum, new File(outDirXML + "/" + checksums));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		System.out.println(outDirXML + "/" + checksums + " created successfully.");
	}

	public static void writeTxtFilesAndAnnotations() throws IOException, UIMAException {
		String fullText = "";
		String fullSent = "";
		String fullTokens = "";

		for (int i = 0; i < ListDocuments.size(); i++) {
			String text = ListDocuments.get(i).text;

			GetSentencesTokensFraMed.runPipeline(text, ListDocuments.get(i).getIdLong());

			String sent = GetSentencesTokensFraMed.sentences;
			String token = GetSentencesTokensFraMed.tokens;

			fullText = fullText + text + "\n";
			fullSent = fullSent + sent + "\n";
			fullTokens = fullTokens + token + "\n";
		}

		Files.write(Paths.get(outDirTXT + "/" + "jsynncc-text.txt"), fullText.getBytes());
		System.out.println(outDirXML + "/" + "jsynncc-text.txt created successfully.");

		Files.write(Paths.get(outDirTXT + "/" + "jsynncc-sentences.txt"), fullSent.getBytes());
		System.out.println(outDirXML + "/" + "jsynncc-sentences.txt created successfully.");

		Files.write(Paths.get(outDirTXT + "/" + "jsynncc-tokens.txt"), fullTokens.getBytes());
		System.out.println(outDirXML + "/" + "jsynncc-tokens.txt created successfully.");

		String annoFile = "jsyncc-annotations.xml";

		try {
			JAXBXMLHandler.marshalAnnotation(annotatedCorpus, new File(outDirXML + "/" + annoFile));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		System.out.println(outDirXML + "/" + annoFile + " created successfully.");

		ProcessBuilder pb = new ProcessBuilder("tar", "cfzv", outDirXML + "/" + "jsyncc-annotations.xml.tar",
				"jsyncc-annotations.xml");

		Process p = pb.start();
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
